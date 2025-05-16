package net.minecraftforge.common.util;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public final class LazyOptional<T> {
    private Supplier<T> supplier;
    private T cachedValue;
    private boolean resolved = false;

    private LazyOptional(Supplier<T> supplier) {
        this.supplier = Objects.requireNonNull(supplier);
    }

    public static <T> LazyOptional<T> of(Supplier<T> supplier) {
        return new LazyOptional<>(supplier);
    }

    public static <T> LazyOptional<T> empty() {
        return new LazyOptional<>(() -> null);
    }
    
    public Optional<T> resolve() {
        return Optional.ofNullable(get());
    }

    public T get() {
        if (!resolved) {
            cachedValue = supplier.get();
            resolved = true;
            supplier = null;
        }
        return cachedValue;
    }

    public boolean isPresent() {
        return get() != null;
    }

    public void ifPresent(Consumer<? super T> consumer) {
        T value = get();
        if (value != null) {
            consumer.accept(value);
        }
    }

    public LazyOptional<T> filter(Predicate<? super T> predicate) {
        Objects.requireNonNull(predicate);
        return isPresent() && predicate.test(get()) ? this : empty();
    }

    public <U> LazyOptional<U> map(Function<? super T, ? extends U> mapper) {
        Objects.requireNonNull(mapper);
        return isPresent() ? of(() -> mapper.apply(get())) : empty();
    }

    public T orElse(T other) {
        T value = get();
        return value != null ? value : other;
    }

    public T orElseGet(Supplier<? extends T> other) {
        T value = get();
        return value != null ? value : other.get();
    }

    public <X extends Throwable> T orElseThrow(Supplier<? extends X> exceptionSupplier) throws X {
        T value = get();
        if (value != null) {
            return value;
        } else {
            throw exceptionSupplier.get();
        }
    }
}
