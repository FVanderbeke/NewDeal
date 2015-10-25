package org.newdeal.core.profiling;

import org.newdeal.core.bean.BeanType;
import org.newdeal.core.bean.Referenceable;
import org.newdeal.core.bean.dummy.DummyActivable;
import org.newdeal.core.dispatch.ActiveStation;
import org.newdeal.core.dispatch.Processor;
import org.newdeal.core.dispatch.dummy.DummyProcessor;
import org.newdeal.core.engine.ActiveDispatchEngine;
import org.newdeal.core.engine.DeclaredDispatchEngine;
import org.newdeal.core.engine.DispatchEngine;
import org.newdeal.core.engine.SystemEngine;
import org.newdeal.core.engine.local.LocalDispatchEngine;
import org.newdeal.core.engine.local.LocalSystemEngine;
import org.newdeal.core.repository.EngineRepository;
import org.newdeal.core.repository.dummy.DummyEngineRepository;
import org.newdeal.core.system.Capacity;
import org.newdeal.core.system.NbServers;
import org.newdeal.core.system.Pattern;
import org.newdeal.core.system.Request;
import org.newdeal.core.system.dummy.DummyRequest;
import org.newdeal.core.system.request.TerminatedResponse;

import java.time.Instant;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;

/**
 * @author Frederick Vanderbeke
 * @since 27/08/2015.
 *
 * Application used to profile the dispatching method.
 */
public class ProfileDispatch {
    public static void main(String[] args) throws Exception {
        EngineRepository engineRepository = new DummyEngineRepository();
        String dispatchEngineName = "dispatch-engine";
        String dispatchEngineLabel = "Dispatch Engine";
        String dispatchEngineComment = "Default dispatch engine.";


        DispatchEngine engine = new LocalDispatchEngine(dispatchEngineName, dispatchEngineLabel, dispatchEngineComment);

        SystemEngine systemEngine = new LocalSystemEngine(engineRepository);

        systemEngine.init();

        DeclaredDispatchEngine dde = systemEngine.declareDispatchEngine(engine, Instant.now()).get();
        ActiveDispatchEngine activeEngine = systemEngine.activateDispatchEngine(dde, Instant.now()).get();

        Referenceable dummyStationActivable = new DummyActivable(BeanType.STAGE, "dummy-stage", "Dummy Stage").getRef();

        ActiveStation station = activeEngine.declareFifoStation(Pattern.MARKOV, Pattern.MARKOV, Capacity.infinite(), NbServers.unbound(), dummyStationActivable).thenCompose(st -> activeEngine.activateStation(st)).get();

        Referenceable dummyOwner = null;

        Processor dummyProcessor = null;
        Request request = null;
        AtomicLong counter = new AtomicLong(0);
        long count;
        CompletableFuture<TerminatedResponse> responseCompletableFuture;
        final LongAdder responseCount = new LongAdder();
        while (true) {
            count = counter.incrementAndGet();

            TimeUnit.MILLISECONDS.sleep(2);

            request = new DummyRequest(org.newdeal.core.system.System.systemClock());

            dummyOwner = new DummyActivable(BeanType.SERVER, "dummy-server-" + count, "Dummy Server " + count).getRef();
            dummyProcessor = new DummyProcessor(dummyOwner);

            activeEngine.declareProcessor(dummyProcessor).thenCompose(activeEngine::activateProcessor).thenCompose(processor -> activeEngine.plug(processor, station, Instant.now()));

            responseCompletableFuture = activeEngine.handle(request, station.getOwner()).thenApply(handled -> (TerminatedResponse)handled);
            responseCompletableFuture.whenComplete((terminatedResponse, throwable) -> {

                if (throwable != null) {
                    throwable.printStackTrace();
                    return;
                }

                activeEngine.getActiveProcessor(terminatedResponse.getProcessorRef()).thenCompose(activeEngine::deactivateProcessor).thenCompose(activeEngine::undeclareProcessor);

                responseCount.increment();
                if (counter.get() % 1000 == 0)

                    System.out.println("response done : " + responseCount.longValue());
            });


        }
    }
}
