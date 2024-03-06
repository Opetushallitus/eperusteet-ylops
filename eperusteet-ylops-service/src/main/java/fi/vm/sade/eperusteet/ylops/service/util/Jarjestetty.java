package fi.vm.sade.eperusteet.ylops.service.util;

import lombok.Getter;

public class Jarjestetty<T> {
    @Getter
    private final T obj;
    @Getter
    private final Integer jarjestys;

    public Jarjestetty(T obj, Integer jarjestys) {
        this.obj = obj;
        this.jarjestys = jarjestys;
    }
}
