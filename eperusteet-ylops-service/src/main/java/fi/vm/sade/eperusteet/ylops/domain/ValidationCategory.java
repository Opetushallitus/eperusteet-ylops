package fi.vm.sade.eperusteet.ylops.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum ValidationCategory {
    OPINTOJAKSO("opintojakso"),
    OPPIAINE("oppiaine"),
    MODUULI("moduuli"),
    OPETUSSUUNNITELMA("opetussuunnitelma");

    private final String tyyppi;

    @Override
    public String toString() {
        return tyyppi;
    }

    @JsonCreator
    public static ValidationCategory of(String tila) {
        for (ValidationCategory s : values()) {
            if (s.tyyppi.equalsIgnoreCase(tila)) {
                return s;
            }
        }
        throw new IllegalArgumentException(tila + " ei ole kelvollinen validointikategoria");
    }
}
