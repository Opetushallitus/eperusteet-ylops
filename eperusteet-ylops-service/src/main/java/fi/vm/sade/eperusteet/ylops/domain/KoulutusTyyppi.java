package fi.vm.sade.eperusteet.ylops.domain;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum KoulutusTyyppi {
    PERUSTUTKINTO("koulutustyyppi_1"),
    AMMATTITUTKINTO("koulutustyyppi_11"),
    ERIKOISAMMATTITUTKINTO("koulutustyyppi_12"),
    AIKUISTENPERUSOPETUS("koulutustyyppi_17"),
    LISAOPETUS("koulutustyyppi_6"),
    ESIOPETUS("koulutustyyppi_15"),
    VARHAISKASVATUS("koulutustyyppi_20"),
    PERUSOPETUS("koulutustyyppi_16"),
    LUKIOKOULUTUS("koulutustyyppi_2"),
    LUKIOVALMISTAVAKOULUTUS("koulutustyyppi_23"),
    PERUSOPETUSVALMISTAVA("koulutustyyppi_22"),
    AIKUISLUKIOKOULUTUS("koulutustyyppi_14"),
    TPO("koulutustyyppi_999907");

    private final String tyyppi;

    private KoulutusTyyppi(String tyyppi) {
        this.tyyppi = tyyppi;
    }

    @Override
    public String toString() {
        return tyyppi;
    }

    @JsonCreator
    public static KoulutusTyyppi of(String tila) {
        for (KoulutusTyyppi s : values()) {
            if (s.tyyppi.equalsIgnoreCase(tila)) {
                return s;
            }
        }
        throw new IllegalArgumentException(tila + " ei ole kelvollinen koulutustyyppi");
    }

    public boolean isOneOf(KoulutusTyyppi[] tyypit) {
        for (KoulutusTyyppi toinen : tyypit) {
            if (toinen.toString().equals(this.tyyppi)) {
                return true;
            }
        }
        return false;
    }

    public boolean isYksinkertainen() {
        return tyyppi != null
                && (tyyppi.equals(AIKUISTENPERUSOPETUS.toString())
                || tyyppi.equals(LISAOPETUS.toString())
                || tyyppi.equals(ESIOPETUS.toString())
                || tyyppi.equals(VARHAISKASVATUS.toString())
                || tyyppi.equals(TPO.toString())
                || tyyppi.equals(PERUSOPETUSVALMISTAVA.toString()));
    }

    public boolean isAmmatillinen() {
        return tyyppi != null && (tyyppi.equals(AMMATTITUTKINTO.toString())
                || tyyppi.equals(ERIKOISAMMATTITUTKINTO.toString())
                || tyyppi.equals(PERUSTUTKINTO.toString()));
    }

    public boolean isLukio() {
        return tyyppi != null && (tyyppi.equals(LUKIOKOULUTUS.toString())
                || tyyppi.equals(LUKIOVALMISTAVAKOULUTUS.toString())
                || tyyppi.equals(AIKUISLUKIOKOULUTUS.toString()));
    }

}
