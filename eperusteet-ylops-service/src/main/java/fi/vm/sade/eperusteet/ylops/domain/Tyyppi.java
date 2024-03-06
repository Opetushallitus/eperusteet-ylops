package fi.vm.sade.eperusteet.ylops.domain;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Tyyppi {

    OPS("ops"),
    POHJA("pohja");

    private final String tyyppi;

    private Tyyppi(String tyyppi) {
        this.tyyppi = tyyppi;
    }

    @Override
    public String toString() {
        return tyyppi;
    }

    @JsonCreator
    public static Tyyppi of(String tila) {
        for (Tyyppi s : values()) {
            if (s.tyyppi.equalsIgnoreCase(tila)) {
                return s;
            }
        }
        throw new IllegalArgumentException(tila + " ei ole kelvollinen tyyppi");
    }
}
