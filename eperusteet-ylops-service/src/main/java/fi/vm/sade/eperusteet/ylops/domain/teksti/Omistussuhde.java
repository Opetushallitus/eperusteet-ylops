package fi.vm.sade.eperusteet.ylops.domain.teksti;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Omistussuhde {
    OMA("oma"),
    LAINATTU("lainattu");

    private String omistussuhde;

    private Omistussuhde(String omistussuhde) {
        this.omistussuhde = omistussuhde;
    }

    @Override
    public String toString() {
        return omistussuhde;
    }

    @JsonCreator
    public static Omistussuhde of(String omistussuhde) {
        for (Omistussuhde s : values()) {
            if (s.omistussuhde.equalsIgnoreCase(omistussuhde)) {
                return s;
            }
        }
        throw new IllegalArgumentException(omistussuhde + " ei ole kelvollinen omistussuhde");
    }
}
