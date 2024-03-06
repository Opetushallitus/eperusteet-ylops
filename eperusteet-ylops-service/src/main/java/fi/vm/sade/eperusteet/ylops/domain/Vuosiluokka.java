package fi.vm.sade.eperusteet.ylops.domain;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Vuosiluokka {

    VUOSILUOKKA_1,
    VUOSILUOKKA_2,
    VUOSILUOKKA_3,
    VUOSILUOKKA_4,
    VUOSILUOKKA_5,
    VUOSILUOKKA_6,
    VUOSILUOKKA_7,
    VUOSILUOKKA_8,
    VUOSILUOKKA_9;

    @JsonCreator
    public static Vuosiluokka of(String vuosiluokka) {
        for (Vuosiluokka s : values()) {
            if (s.toString().equalsIgnoreCase(vuosiluokka)) {
                return s;
            }
        }
        throw new IllegalArgumentException(vuosiluokka + " ei ole kelvollinen vuosiluokka");
    }

    @Override
    public String toString() {
        return name().toLowerCase();
    }

}
