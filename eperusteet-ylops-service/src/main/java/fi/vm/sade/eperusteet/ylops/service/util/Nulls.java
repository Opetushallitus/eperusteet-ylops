package fi.vm.sade.eperusteet.ylops.service.util;

import fi.vm.sade.eperusteet.ylops.service.exception.NotExistsException;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.function.Function;

/**
 * Apumetodeja null-arvojen kanssa temppuiluun
 */
public final class Nulls {

    private Nulls() {
        //Apuluokka
    }

    public static <T> Collection<T> nullToEmpty(Collection<T> s) {
        if (s == null) {
            return Collections.emptySet();
        }
        return s;
    }

    public static String nullToEmpty(String s) {
        if (s == null) {
            return "";
        }
        return s;
    }

    public static <T> T assertExists(T o, String msg) {
        if (o == null) {
            throw new NotExistsException(msg);
        }
        return o;
    }

    public static <F, T> Function<F, Optional<T>> ofNullable(Function<F, T> target) {
        return f -> {
            T value = target.apply(f);
            return Optional.ofNullable(value);
        };
    }
}
