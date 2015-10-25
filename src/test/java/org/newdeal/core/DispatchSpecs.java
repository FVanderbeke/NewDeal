package org.newdeal.core;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.newdeal.core.bean.BeanType;
import org.newdeal.core.bean.Referenceable;
import org.newdeal.core.bean.dummy.DummyActivable;
import org.newdeal.core.dispatch.ActiveStation;
import org.newdeal.core.dispatch.PluggedProcessor;
import org.newdeal.core.dispatch.Processor;
import org.newdeal.core.dispatch.dummy.AbstractDummyProcessor;
import org.newdeal.core.dispatch.dummy.DummyProcessor;
import org.newdeal.core.engine.ActiveDispatchEngine;
import org.newdeal.core.engine.DeclaredDispatchEngine;
import org.newdeal.core.engine.DispatchEngine;
import org.newdeal.core.engine.SystemEngine;
import org.newdeal.core.engine.local.LocalDispatchEngine;
import org.newdeal.core.engine.local.LocalSystemEngine;
import org.newdeal.core.repository.EngineRepository;
import org.newdeal.core.repository.dummy.DummyEngineRepository;
import org.newdeal.core.system.*;
import org.newdeal.core.system.dummy.DummyArrayClock;
import org.newdeal.core.system.dummy.DummyIncrClock;
import org.newdeal.core.system.dummy.DummyRequest;
import org.newdeal.core.system.monad.Either;
import org.newdeal.core.system.request.*;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

/**
 * Unit tests used as specifications for the dispatch engine.
 *
 * @author Frederick Vanderbeke
 * @since 29/07/2015
 */
public class DispatchSpecs {

    private static SystemEngine systemEngine;

    private final static AtomicInteger seq = new AtomicInteger(1);

    @BeforeClass
    public static void beforeClass() {
        EngineRepository engineRepository = new DummyEngineRepository();

        systemEngine = new LocalSystemEngine(engineRepository);

        systemEngine.init();
    }

    @AfterClass
    public static void afterClass() {
        systemEngine.destroy();
    }

    private ActiveDispatchEngine declareDispatchEngine() throws Exception {
        int seqNb = seq.getAndIncrement();
        String dispatchEngineName = "dispatch-engine-" + seqNb;
        String dispatchEngineLabel = "Dispatch Engine " + seqNb;
        String dispatchEngineComment = "Default dispatch engine.";

        DispatchEngine engine = new LocalDispatchEngine(dispatchEngineName, dispatchEngineLabel, dispatchEngineComment);

        DeclaredDispatchEngine dde = systemEngine.declareDispatchEngine(engine, Instant.now()).get();

        return systemEngine.activateDispatchEngine(dde, Instant.now()).get();
    }

    private ActiveStation declareFifoStation(ActiveDispatchEngine engine) throws Exception {
        Referenceable dummyStationActivable = new DummyActivable(BeanType.STAGE, "dummy-stage", "Dummy Stage").getRef();

        return engine.declareFifoStation(Pattern.MARKOV, Pattern.MARKOV, Capacity.infinite(), NbServers.unbound(), dummyStationActivable).thenCompose(station -> engine.activateStation(station)).get();
    }

    private PluggedProcessor declareProcessor(ActiveDispatchEngine engine, ActiveStation station) throws Exception {
        Referenceable dummyOwner = new DummyActivable(BeanType.SERVER, "dummy-server", "Dummy Server").getRef();

        Processor dummyProcessor = new DummyProcessor(dummyOwner);

        return engine.declareProcessor(dummyProcessor).thenCompose(engine::activateProcessor).thenCompose(processor -> engine.plug(processor, station, Instant.now())).get();
    }

    @Test
    public void testDeclareFifoStation() throws Exception {
        ActiveDispatchEngine activeEngine = declareDispatchEngine();
        ActiveStation station = declareFifoStation(activeEngine);

        // Checking the model
        assertEquals("Bad arrival pattern.", Pattern.MARKOV, station.getModel().getArrivalPattern());
        assertEquals("Bad departure pattern.", Pattern.MARKOV, station.getModel().getDeparturePattern());
        assertEquals("Bad discipline.", Discipline.FIFO, station.getModel().getDiscipline());
        assertEquals("Bad capacity.", Capacity.infinite(), station.getModel().getCapacity());
        assertEquals("Bad number of servers.", NbServers.unbound(), station.getModel().getNbServers());

        // Checking the owner
        assertEquals("Bad bean type.", BeanType.STAGE, station.getOwner().getType());
        assertEquals("Bad owner name.", "dummy-stage", station.getOwner().getName());
        assertEquals("Bad owner label.", "", station.getOwner().getLabel());
        assertEquals("Bad owner comment", Optional.empty(), station.getOwner().getComment());
    }

    @Test
    public void testPlugProcessor() throws Exception {
        ActiveDispatchEngine activeEngine = declareDispatchEngine();
        ActiveStation station = declareFifoStation(activeEngine);
        PluggedProcessor processor = declareProcessor(activeEngine, station);

        // Checking processor.
        assertEquals("Bad bean type", BeanType.SERVER, processor.getOwner().getType());
        assertEquals("Bad owner name", "dummy-server", processor.getOwner().getName());
        assertEquals("Bad owner name", "", processor.getOwner().getLabel());
        assertEquals("Bad owner comment", Optional.empty(), processor.getOwner().getComment());
    }

    @Test
    public void testDeactivateProcessor() throws Exception {
        ActiveDispatchEngine activeEngine = declareDispatchEngine();
        ActiveStation station = declareFifoStation(activeEngine);
        PluggedProcessor processor = declareProcessor(activeEngine, station);

        Processor undeclaredProcessor = activeEngine.unplug(processor).thenCompose(activeEngine::deactivateProcessor).thenCompose(activeEngine::undeclareProcessor).get();

        assertEquals("Bad bean type", BeanType.SERVER, undeclaredProcessor.getOwner().getType());
        assertEquals("Bad owner name", "dummy-server", undeclaredProcessor.getOwner().getName());
        assertEquals("Bad owner name", "", undeclaredProcessor.getOwner().getLabel());
        assertEquals("Bad owner comment", Optional.empty(), undeclaredProcessor.getOwner().getComment());
    }

    @Test
    public void testSubmitRequest() throws Exception {

        ActiveDispatchEngine activeEngine = declareDispatchEngine();
        ActiveStation station = declareFifoStation(activeEngine);
        PluggedProcessor processor = declareProcessor(activeEngine, station);

        Instant creationDate = Instant.EPOCH.plusMillis(1000);
        Instant handleDate = creationDate.plusMillis(1000);
        Instant submitDate = handleDate.plusMillis(1000);
        Instant dispatchDate = submitDate.plusMillis(1000);
        Instant acceptDate = dispatchDate.plusMillis(1000);
        Instant handshakeDate = acceptDate.plusMillis(1000);
        Instant preProcDate = handshakeDate.plusMillis(1000);
        Instant procDate = preProcDate.plusMillis(1000);
        Instant postProcDate = procDate.plusMillis(1000);
        Instant termDate = postProcDate.plusMillis(1000);

        List<Instant> instants = Arrays.asList(creationDate, handleDate, submitDate, dispatchDate, acceptDate, handshakeDate, preProcDate, procDate, postProcDate, termDate);

        Clock dummyClock = new DummyArrayClock(instants);

        Request request = new DummyRequest(dummyClock);

        TerminatedResponse response =  activeEngine.handle(request, station.getOwner())
                .thenApply(handled -> (TerminatedResponse) handled).get();

        // Checking response.

        assertEquals(response.getTerminationDate().toEpochMilli(), termDate.toEpochMilli());
        assertEquals(response.getAcceptDate(), acceptDate);
        assertEquals(response.getCreationDate().toEpochMilli(), creationDate.toEpochMilli());
        assertEquals(response.getHandleDate().toEpochMilli(), handleDate.toEpochMilli());
        assertEquals(response.getDispatchDate().toEpochMilli(), dispatchDate.toEpochMilli());
        assertEquals(response.getHandshakeDate().toEpochMilli(), handshakeDate.toEpochMilli());
        assertEquals(response.getPostProcessDate().toEpochMilli(), postProcDate.toEpochMilli());
        assertEquals(response.getPreProcessDate().toEpochMilli(), preProcDate.toEpochMilli());
        assertEquals(response.getProcessorRef(), processor.getOwner());
        assertEquals(response.getResponseDate().toEpochMilli(), procDate.toEpochMilli());
        assertEquals(response.getSubmissionDate().toEpochMilli(), submitDate.toEpochMilli());
        assertEquals(response.getStationRef(), station.getOwner());
    }

    /**
     * Here we test the default workflow.
     */
    @Test
    public void testSubmitRequestBeforePlug() throws Exception {

        ActiveDispatchEngine activeEngine = declareDispatchEngine();
        ActiveStation station = declareFifoStation(activeEngine);

        Instant creationDate = Instant.EPOCH.plusMillis(1000);
        Instant handleDate = creationDate.plusMillis(1000);
        Instant submitDate = creationDate.plusMillis(1000);
        Instant dispatchDate = submitDate.plusMillis(1000);
        Instant acceptDate = dispatchDate.plusMillis(1000);
        Instant handshakeDate = acceptDate.plusMillis(1000);
        Instant preProcDate = handshakeDate.plusMillis(1000);
        Instant procDate = preProcDate.plusMillis(1000);
        Instant postProcDate = procDate.plusMillis(1000);
        Instant termDate = postProcDate.plusMillis(1000);

        List<Instant> instants = Arrays.asList(creationDate, handleDate, submitDate, dispatchDate, acceptDate, handshakeDate, preProcDate, procDate, postProcDate, termDate);

        Clock dummyClock = new DummyArrayClock(instants);

        Request request = new DummyRequest(dummyClock);

        CompletableFuture<TerminatedResponse> responseFuture = activeEngine.handle(request, station.getOwner())
                .thenApply(handled -> (TerminatedResponse) handled);

        PluggedProcessor processor = declareProcessor(activeEngine, station);

        TerminatedResponse response = responseFuture.get();
        // Checking response.

        assertEquals(response.getTerminationDate().toEpochMilli(), termDate.toEpochMilli());
        assertEquals(response.getAcceptDate(), acceptDate);
        assertEquals(response.getCreationDate().toEpochMilli(), creationDate.toEpochMilli());
        assertEquals(response.getHandleDate().toEpochMilli(), handleDate.toEpochMilli());
        assertEquals(response.getDispatchDate().toEpochMilli(), dispatchDate.toEpochMilli());
        assertEquals(response.getHandshakeDate().toEpochMilli(), handshakeDate.toEpochMilli());
        assertEquals(response.getPostProcessDate().toEpochMilli(), postProcDate.toEpochMilli());
        assertEquals(response.getPreProcessDate().toEpochMilli(), preProcDate.toEpochMilli());
        assertEquals(response.getProcessorRef(), processor.getOwner());
        assertEquals(response.getResponseDate().toEpochMilli(), procDate.toEpochMilli());
        assertEquals(response.getSubmissionDate().toEpochMilli(), submitDate.toEpochMilli());
        assertEquals(response.getStationRef(), station.getOwner());
    }

    /**
     * Testing balking response (due to station capacity limitation). A submitted request must be balked if not
     * enough zero space is left in the station.
     */
    @Test
    public void testBalkingRequest() throws Exception {

        ActiveDispatchEngine activeEngine = declareDispatchEngine();

        Referenceable dummyStationActivable = new DummyActivable(BeanType.STAGE, "dummy-zero-stage", "Dummy Empty Stage").getRef();

        ActiveStation station = activeEngine.declareFifoStation(Pattern.MARKOV, Pattern.MARKOV, Capacity.zero(), NbServers.unbound(), dummyStationActivable).thenCompose(st -> activeEngine.activateStation(st)).get();

        Instant creationDate = Instant.EPOCH.plusMillis(1000);
        Instant handleDate = creationDate.plusMillis(1000);
        Instant submitDate = handleDate.plusMillis(1000);
        Instant balkDate = submitDate.plusMillis(1000);

        List<Instant> instants = Arrays.asList(creationDate, handleDate, submitDate, balkDate);

        Clock dummyClock = new DummyArrayClock(instants);

        Request request = new DummyRequest(dummyClock);

        CompletableFuture<HandledRequest> handledFuture = activeEngine.handle(request, station.getOwner());

        HandledRequest handledRequest = handledFuture.get();

        assertTrue("Request must be balked.", handledRequest instanceof BalkedRequest);
        assertEquals(((BalkedRequest) handledRequest).getBalkDate().toEpochMilli(), balkDate.toEpochMilli());
    }

    /**
     * The goal here is to test a violent cancel upon the request handled future. This one will be stuck in dispatch
     * step.
     */
    @Test
    public void testCancellingRequest() throws Exception {

        ActiveDispatchEngine activeEngine = declareDispatchEngine();
        ActiveStation station = declareFifoStation(activeEngine);

        Instant creationDate = Instant.EPOCH.plusMillis(1000);
        Instant handleDate = creationDate.plusMillis(1000);
        Instant submitDate = handleDate.plusMillis(1000);
        Instant dispatchDate = submitDate.plusMillis(1000);
        Instant renegeDate = dispatchDate.plusMillis(1000);

        List<Instant> instants = Arrays.asList(creationDate, handleDate, submitDate, dispatchDate, renegeDate);

        Clock dummyClock = new DummyArrayClock(instants);

        Request request = new DummyRequest(dummyClock);

        CompletableFuture<HandledRequest> handledFuture = activeEngine.handle(request, station.getOwner());

        handledFuture.cancel(true);

        assertFalse("Even cancelled, request must be reneged and not marked as cancelled.", handledFuture.isCancelled());

        HandledRequest handledRequest = handledFuture.get();

        assertTrue("Request must be reneged.", handledRequest instanceof RenegedRequest);
        assertNotNull(((RenegedRequest) handledRequest).getRenegeDate().toEpochMilli());
    }

    /**
     * We want to test correct reneging (call from the .
     */
    @Test
    public void testRenegingRequest() throws Exception {

        ActiveDispatchEngine activeEngine = declareDispatchEngine();
        ActiveStation station = declareFifoStation(activeEngine);

        Instant creationDate = Instant.EPOCH.plusMillis(1000);
        Instant handleDate = creationDate.plusMillis(1000);
        Instant submitDate = handleDate.plusMillis(1000);
        Instant dispatchDate = submitDate.plusMillis(1000);
        Instant renegeDate = dispatchDate.plusMillis(1000);

        List<Instant> instants = Arrays.asList(creationDate, handleDate, submitDate, dispatchDate, renegeDate);

        Clock dummyClock = new DummyArrayClock(instants);

        Request request = new DummyRequest(dummyClock);

        CompletableFuture<HandledRequest> handledFuture = activeEngine.handle(request, station.getOwner());

        boolean reneged = activeEngine.abandon(request).get();

        HandledRequest handled = handledFuture.get();

        assertTrue("Request must be reneged.", reneged);
        assertNotNull(((RenegedRequest) handled).getRenegeDate().toEpochMilli());
    }

    @Test
    public void testRenegingOnAcceptRequest() throws Exception {
        ActiveDispatchEngine activeEngine = declareDispatchEngine();
        ActiveStation station = declareFifoStation(activeEngine);

        Referenceable dummyOwner = new DummyActivable(BeanType.SERVER, "dummy-server", "Dummy Server").getRef();

        final CountDownLatch barrier = new CountDownLatch(1);

        final CountDownLatch stepLock = new CountDownLatch(1);

        Processor lockedProcessor = new AbstractDummyProcessor(dummyOwner) {
            @Override
            public Either<Exception, AcceptedRequest> accept(DispatchedRequest request) {
                barrier.countDown();
                // The goal is to lock ad-vitam eternam here.
                try {
                    stepLock.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                throw new RuntimeException("Never thrown");
            }
        };

        activeEngine.declareProcessor(lockedProcessor).thenCompose(activeEngine::activateProcessor).thenCompose(processor -> activeEngine.plug(processor, station, Instant.now()));

        Instant creationDate = Instant.EPOCH.plusMillis(1000);
        Instant handleDate = creationDate.plusMillis(1000);
        Instant submitDate = handleDate.plusMillis(1000);
        Instant dispatchDate = submitDate.plusMillis(1000);
        Instant acceptDate = dispatchDate.plusMillis(1000);
        Instant renegeDate = acceptDate.plusMillis(1000);

        List<Instant> instants = Arrays.asList(creationDate, handleDate, submitDate, dispatchDate, acceptDate, renegeDate);

        Clock dummyClock = new DummyArrayClock(instants);

        Request request = new DummyRequest(dummyClock);

        CompletableFuture<HandledRequest> handledFuture = activeEngine.handle(request, station.getOwner());
        barrier.await();
        boolean reneged = activeEngine.abandon(request).get();

        HandledRequest handled = handledFuture.get();

        assertTrue("Request must be reneged.", reneged);
        assertNotNull(((AbortedRequest) handled).getAbortDate().toEpochMilli());
    }

    @Test
    public void testFifoStation() throws Exception {
        // The goal here is to check that first arrived requests are managed first.
        ActiveDispatchEngine activeEngine = declareDispatchEngine();
        ActiveStation station = declareFifoStation(activeEngine);

        Instant creationDate = Instant.EPOCH.plusMillis(1000);

        Clock dummyClock = new DummyIncrClock(Instant.EPOCH, Duration.ofMillis(1000));

        Request request = new DummyRequest(dummyClock);

        CompletableFuture<TerminatedResponse> responseFuture = activeEngine.handle(request, station.getOwner()).thenApply(handled -> (TerminatedResponse)handled);

        Request request2 = new DummyRequest(dummyClock);

        CompletableFuture<TerminatedResponse> responseFuture2 = activeEngine.handle(request2, station.getOwner()).thenApply(handled -> (TerminatedResponse) handled);

        Request request3 = new DummyRequest(dummyClock);

        CompletableFuture<TerminatedResponse> responseFuture3 = activeEngine.handle(request3, station.getOwner()).thenApply(handled -> (TerminatedResponse) handled);

        PluggedProcessor processor = declareProcessor(activeEngine, station);
        activeEngine.plug(processor, station, Instant.now());
        activeEngine.plug(processor, station, Instant.now());

        TerminatedResponse response = responseFuture.get();
        TerminatedResponse response2 = responseFuture2.get();
        TerminatedResponse response3 = responseFuture3.get();

        assertTrue(response.getCreationDate().toEpochMilli() < response2.getCreationDate().toEpochMilli());
        assertTrue(response2.getCreationDate().toEpochMilli() < response3.getCreationDate().toEpochMilli());

        assertTrue(response.getDispatchDate().toEpochMilli() < response2.getDispatchDate().toEpochMilli());
        assertTrue(response2.getDispatchDate().toEpochMilli() < response3.getDispatchDate().toEpochMilli());
    }
}

