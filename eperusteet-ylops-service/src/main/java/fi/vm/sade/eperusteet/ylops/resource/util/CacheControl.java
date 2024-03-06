package fi.vm.sade.eperusteet.ylops.resource.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface CacheControl {
    /***
     * Expires-aika sekunteina
     */
    int age() default 0;

    boolean nonpublic() default true;

    boolean nocache() default false;

    public static final int ONE_YEAR = 365 * 24 * 3600;
}
