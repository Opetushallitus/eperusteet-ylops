package fi.vm.sade.eperusteet.ylops.domain.teksti;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Kieli {
    // käytetään ISO-639-1 kielikoodistoa

    FI("fi"), // suomi
    SV("sv"), // svenska
    SE("se"), // davvisámegiella (sámi), pohjoissaame (saame)
    RU("ru"), // русский язык, venäjä
    EN("en"); // english

    private final String koodi;

    private Kieli(String koodi) {
        this.koodi = koodi;
    }

    @Override
    public String toString() {
        return koodi;
    }

    @JsonCreator
    public static Kieli of(String koodi) {
        // FIXME: tämä pitää pystyä ottamaan pois
        if (koodi.equals("_tunniste")) {
            return null;
        }
        for (Kieli k : values()) {
            if (k.koodi.equalsIgnoreCase(koodi)) {
                return k;
            }
        }
        throw new IllegalArgumentException(koodi + " ei ole kelvollinen kielikoodi");
    }
}
