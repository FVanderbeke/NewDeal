package org.newdeal.core.system.monad;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author Frederick Vanderbeke
 * @since 05/09/2015.
 */
public final class Either<L, R> {
    private final Optional<L> left;
    private final Optional<R> right;

    private Either(Optional<L> l, Optional<R> r) {
        left = l;
        right = r;
    }

    public static <L, R> Either<L, R> left(L value) {
        return new Either<>(Optional.of(value), Optional.empty());
    }

    public static <L, R> Either<L, R> right(R value) {
        return new Either<>(Optional.empty(), Optional.of(value));
    }

    public <T> T map(Function<? super L, ? extends T> lf, Function<? super R, ? extends T> rf) {
        return left.map(lf).orElseGet(() -> right.map(rf).get());
    }

    public <T> Either<T, R> mapLeft(Function<? super L, ? extends T> lf) {
        return new Either<>(left.map(lf), right);
    }

    public <T> Either<L, T> mapRight(Function<? super R, ? extends T> rf) {
        return new Either<>(left, right.map(rf));
    }

    public void fold(Consumer<? super L> lf, Consumer<? super R> rf) {
        left.ifPresent(lf);
        right.ifPresent(rf);
    }

    public Either<R, L> swap() {
        return new Either<>(right, left);
    }

    public boolean isLeft() {
        return left.isPresent();
    }

    public boolean isRight() {
        return  right.isPresent();
    }

    public void ifLeft(Consumer<L> consumer) {
        left.ifPresent(consumer);
    }

    public void ifRight(Consumer<R> consumer) {
        right.ifPresent(consumer);
    }

}
