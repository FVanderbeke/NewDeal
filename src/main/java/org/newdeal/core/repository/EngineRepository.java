package org.newdeal.core.repository;

import org.newdeal.core.bean.BeanType;
import org.newdeal.core.engine.DeclaredEngine;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * @author Frederick Vanderbeke
 * @since 23/06/2015.
 */
public interface EngineRepository {

    CompletableFuture<DeclaredEngine> persist(Function<Long, DeclaredEngine> constructor);

    CompletableFuture<Optional<DeclaredEngine>> find(BeanType type, String engineName);
}
