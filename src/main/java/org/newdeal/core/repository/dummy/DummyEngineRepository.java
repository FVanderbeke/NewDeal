package org.newdeal.core.repository.dummy;

import org.newdeal.core.bean.BeanType;
import org.newdeal.core.engine.DeclaredEngine;
import org.newdeal.core.repository.EngineRepository;
import org.newdeal.core.repository.exception.EngineAlreadyExistsException;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

/**
 * @author Frederick Vanderbeke
 * @since 23/06/2015.
 */
public class DummyEngineRepository implements EngineRepository {

    private final AtomicLong sequence = new AtomicLong(1);

    private final CopyOnWriteArrayList<DeclaredEngine> engines = new CopyOnWriteArrayList<>();

    @Override
    public CompletableFuture<DeclaredEngine> persist(Function<Long, DeclaredEngine> constructor) {
        return CompletableFuture.supplyAsync(() -> {
            DeclaredEngine engine = constructor.apply(sequence.getAndIncrement());
            if (!engines.addIfAbsent(engine)) {
                throw new EngineAlreadyExistsException(engine);
            }
            return engine;
        });
    }

    @Override
    public CompletableFuture<Optional<DeclaredEngine>> find(BeanType type, String engineName) {
        return CompletableFuture.supplyAsync(() -> engines.stream().filter(e -> Objects.equals(e.getName(), engineName) && Objects.equals(e.getType(), type)).findFirst());
    }


}
