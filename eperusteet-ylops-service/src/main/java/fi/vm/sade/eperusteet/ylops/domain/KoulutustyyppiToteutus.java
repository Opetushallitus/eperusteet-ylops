package fi.vm.sade.eperusteet.ylops.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;

/**
 * Koulutustyyppi ei enää yksilöi toteutusta ja toteutus voi olla jaettu eri koulutustyyppien välillä.
 */
@AllArgsConstructor
public enum KoulutustyyppiToteutus {
    YKSINKERTAINEN("yksinkertainen"), // Sisältää ainoastaan tekstikappaleita
    PERUSOPETUS("perusopetus"),
    TPO("taiteenperusopetus"),
    LOPS("lops"),
    LOPS2019("lops2019");

    private final String tyyppi;

    @JsonCreator
    public static KoulutustyyppiToteutus of(String tila) {
        for (KoulutustyyppiToteutus s : values()) {
            if (s.tyyppi.equalsIgnoreCase(tila)) {
                return s;
            }
        }
        throw new IllegalArgumentException(tila + " ei ole kelvollinen tila");
    }

    @Override
    public String toString() {
        return tyyppi;
    }

}
