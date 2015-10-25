package org.newdeal.core.engine.local;

import org.newdeal.core.bean.Worker;
import org.newdeal.core.engine.*;
import org.newdeal.core.engine.exception.EngineNotDeclaredException;
import org.newdeal.core.repository.EngineRepository;

import java.time.Instant;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;

/**
 * -- UNSTABLE --
 *
 * @author Frederick Vanderbeke
 * @since 22/06/2015
 * @version 0.0.1
 */
public class LocalSystemEngine implements SystemEngine {
    private EngineRepository engineRepository;

    private final CopyOnWriteArrayList<ActiveEngine> activeEngines = new CopyOnWriteArrayList<>();

    public LocalSystemEngine(EngineRepository engineRepository) {
        this.engineRepository = engineRepository;
    }

    @Override
    public void init() {
    }

    @Override
    public void destroy() {
    }

    private <E extends Engine, D extends DeclaredEngine> CompletableFuture<D> declareEngine(Function<Long, DeclaredEngine> declarableConstructor) {
        CompletableFuture<DeclaredEngine> declaredEngine = engineRepository.persist(declarableConstructor);
        return declaredEngine.thenApply(de -> (D) de);
    }

    private <D extends DeclaredEngine, A extends ActiveEngine> CompletableFuture<A> activateEngine(D engine, Function<D, A> activableConstructor) {
        CompletableFuture<ActiveEngine> activeEngine = engineRepository.find(engine.getType(), engine.getName())
                .thenApply(de -> (D) de.orElseThrow(() -> new EngineNotDeclaredException(engine)))
                .thenCompose(dpe -> {
                    A ape = activableConstructor.apply(dpe);
                    if (activeEngines.addIfAbsent(ape) && ape instanceof Worker) {
                        ((Worker) ape).init();
                        return ((Worker) ape).start(ape.getLastActivationDate());
                    }
                    return CompletableFuture.completedFuture(ape);
                })
                .thenApply(ape -> activeEngines.stream().filter(ae -> ae.equals(ape)).findFirst().get());
        return activeEngine.thenApply(a -> (A) a);
    }

    @Override
    public CompletableFuture<DeclaredDispatchEngine> declareDispatchEngine(DispatchEngine engine, Instant declarationDate) {
        return declareEngine((id) -> new LocalDeclaredDispatchEngine(id, engine, declarationDate));
    }

    @Override
    public CompletableFuture<ActiveDispatchEngine> activateDispatchEngine(DeclaredDispatchEngine engine, Instant activationDate) {
        return activateEngine(engine, (dpe) -> new LocalActiveDispatchEngine(dpe, activationDate));
    }
}
