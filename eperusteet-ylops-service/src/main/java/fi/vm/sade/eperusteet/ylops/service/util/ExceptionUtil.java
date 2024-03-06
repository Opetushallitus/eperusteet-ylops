package fi.vm.sade.eperusteet.ylops.service.util;

import java.util.function.Function;
import java.util.function.Supplier;

public class ExceptionUtil {
    private ExceptionUtil() {
    }

    @FunctionalInterface
    public interface ThrowingSupplier<T, Ex extends Exception> {
        T get() throws Ex;
    }

    @FunctionalInterface
    public interface ThrowingFunction<F, T, Ex extends Exception> {
        T apply(F f) throws Ex;
    }

    private static Function<Exception, IllegalStateException> DEFAULT_RUNTIME_EX = IllegalStateException::new;

    public static <F, T, Ex extends Exception> Function<F, T> wrapRuntime(ThrowingFunction<F, T, Ex> target)
            throws IllegalStateException {
        return wrapRuntime(target, DEFAULT_RUNTIME_EX);
    }

    public static <F, T, Ex extends Exception, RtEx extends RuntimeException> Function<F, T>
    wrapRuntime(ThrowingFunction<F, T, Ex> target, Function<? super Ex, ? extends RtEx> wrapper)
            throws RtEx {
        return (F f) -> {
            try {
                return target.apply(f);
            } catch (Exception e) {
                if (RuntimeException.class.isAssignableFrom(e.getClass())) {
                    throw (RuntimeException) e;
                }
                throw wrapper.apply((Ex) e);
            }
        };
    }

    public static <T, Ex extends Exception> Supplier<T> wrapRuntime(ThrowingSupplier<T, Ex> target)
            throws IllegalStateException {
        return wrapRuntime(target, DEFAULT_RUNTIME_EX);
    }

    public static <T, Ex extends Exception, RtEx extends RuntimeException> Supplier<T>
    wrapRuntime(ThrowingSupplier<T, Ex> target, Function<? super Ex, ? extends RtEx> wrapper)
            throws RtEx {
        return () -> {
            try {
                return target.get();
            } catch (Exception e) {
                if (RuntimeException.class.isAssignableFrom(e.getClass())) {
                    throw (RuntimeException) e;
                }
                throw wrapper.apply((Ex) e);
            }
        };
    }
}
