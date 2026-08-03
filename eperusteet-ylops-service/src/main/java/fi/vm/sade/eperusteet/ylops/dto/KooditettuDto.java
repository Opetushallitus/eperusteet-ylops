package fi.vm.sade.eperusteet.ylops.dto;

import fi.vm.sade.eperusteet.ylops.dto.koodisto.KoodistoKoodiDto;
import fi.vm.sade.eperusteet.ylops.dto.teksti.LokalisoituTekstiDto;

public interface KooditettuDto {

    default void setKooditettu(LokalisoituTekstiDto kooditettu) {}

    default void setKooditettu(LokalisoituTekstiDto kooditettu, String voimassaAlkuPvm, String voimassaLoppuPvm) {
        setKooditettu(kooditettu);
    }

    default void setKoodistoKoodi(KoodistoKoodiDto koodi) {
        setKooditettu(koodi.getNimi(), koodi.getVoimassaAlkuPvm(), koodi.getVoimassaLoppuPvm());
    }
}
