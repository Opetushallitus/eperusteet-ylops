package fi.vm.sade.eperusteet.ylops.domain.oppiaine;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum OppiaineTyyppi {
    /**
     * Kaikille yhteinen aine
     */
    YHTEINEN("yhteinen"),
    /**
     * Taide- ja/tai taitoaineen valinnainen tunti
     */
    TAIDE_TAITOAINE("taide_taitoaine"),
    /**
     * Muu valinnainen aine
     */
    MUU_VALINNAINEN("muu_valinnainen"),
    /**
     * Lukion oppiaine
     */
    LUKIO("lukio");

    private final String tyyppi;

    private OppiaineTyyppi(String tyyppi) {
        this.tyyppi = tyyppi;
    }

    @Override
    public String toString() {
        return tyyppi;
    }

    @JsonCreator
    public static OppiaineTyyppi of(String tyyppi) {
        for (OppiaineTyyppi s : values()) {
            if (s.tyyppi.equalsIgnoreCase(tyyppi)) {
                return s;
            }
        }
        throw new IllegalArgumentException(tyyppi + " ei ole kelvollinen OppiaineTyyppi");
    }
}
