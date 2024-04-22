package fi.vm.sade.eperusteet.ylops.service.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class LambdaUtil {
    private LambdaUtil() {
    }

    public static <K, V> Function<K, List<V>> orEmpty(Function<K, List<V>> f) {
        return k -> Optional.ofNullable(f.apply(k)).orElseGet(ArrayList::new);
    }

    @FunctionalInterface
    public interface Copyable<T extends Copyable<T>> {
        T copyInto(T to);

        default Copier<T> copier() {
            return Copyable::copyInto;
        }
    }

    @FunctionalInterface
    public interface ConstructedCopier<T> {
        T copy(T from);
    }

    public static <K, F, T> Function<K, T> map(Map<K, F> from, Function<F, T> map) {
        return k -> Optional.ofNullable(from.get(k)).map(map).orElse(null);
    }

    @FunctionalInterface
    public interface Copier<T> {
        void copy(T from, T to);

        default T copied(T from, T to) {
            copy(from, to);
            return to;
        }

        default ConstructedCopier<T> construct(Function<T, T> conststructor) {
            return (a) -> copied(a, conststructor.apply(a));
        }

        default Copier<T> and(Copier<T>... also) {
            Copier<T> copier = this;
            for (Copier<T> c : also) {
                copier = copier.and(c);
            }
            return copier;
        }

        default Copier<T> and(Copier<T> also) {
            if (also == null) {
                return this;
            }
            Copier<T> me = this;
            return (a, b) -> {
                me.copy(a, b);
                also.copy(a, b);
            };
        }

        static <T> Copier<T> nothing() {
            return (a, b) -> {
            };
        }

        static <T, E> Copier<T> of(Function<T, E> getter, Setter<T, E> setter) {
            return (a, b) -> setter.set(b, getter.apply(a));
        }
    }

    @FunctionalInterface
    public interface Setter<ClassType, ValueType> {
        void set(ClassType obj, ValueType value);
    }

}
