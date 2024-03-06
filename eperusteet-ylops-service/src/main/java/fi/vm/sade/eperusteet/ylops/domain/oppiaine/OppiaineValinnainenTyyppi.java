package fi.vm.sade.eperusteet.ylops.domain.oppiaine;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum OppiaineValinnainenTyyppi {
    SYVENTAVA("syventava"),
    SOVELTAVA("soveltava"),
    EI_MAARITETTY("ei_maaritetty");

    private final String tyyppi;

    private OppiaineValinnainenTyyppi(String tyyppi) {
        this.tyyppi = tyyppi;
    }

    @Override
    public String toString() {
        return tyyppi;
    }

    @JsonCreator
    public static OppiaineValinnainenTyyppi of(String tyyppi) {
        for (OppiaineValinnainenTyyppi s : values()) {
            if (s.tyyppi.equalsIgnoreCase(tyyppi)) {
                return s;
            }
        }
        throw new IllegalArgumentException(tyyppi + " ei ole kelvollinen OppiaineValinnainenTyyppi");
    }
}
