package fi.vm.sade.eperusteet.ylops.service.lops2019.impl;

import lombok.experimental.UtilityClass;

import java.util.List;

@UtilityClass
public class Lops2019PaikallisetLaajennuksetUtil {

    private static final List<String> SALLITTU_OPPIAINE_KOODIT = List.of(
            "oppiaineetjaoppimaaratlops2021_ai3",
            "oppiaineetjaoppimaaratlops2021_ai12",
            "oppiaineetjaoppimaaratlops2021_ux",
            "oppiaineetjaoppimaaratlops2021_vka1",
            "oppiaineetjaoppimaaratlops2021_vkaaa",
            "oppiaineetjaoppimaaratlops2021_vkaab3",
            "oppiaineetjaoppimaaratlops2021_vkb",
            "oppiaineetjaoppimaaratlops2021_vksk");

    public static List<String> getSallitutOppiaineKoodit() {
        return SALLITTU_OPPIAINE_KOODIT;
    }

    public static boolean isPaikallisestiLaajennettava(String oppiaineKoodiUri) {
        return oppiaineKoodiUri != null
                && SALLITTU_OPPIAINE_KOODIT.stream()
                .anyMatch(sallittuLaajennusKoodi -> oppiaineKoodiUri.startsWith(sallittuLaajennusKoodi));
    }
}
