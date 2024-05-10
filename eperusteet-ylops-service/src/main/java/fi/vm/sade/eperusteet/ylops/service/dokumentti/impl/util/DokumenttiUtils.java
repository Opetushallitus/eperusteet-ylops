package fi.vm.sade.eperusteet.ylops.service.dokumentti.impl.util;

import fi.vm.sade.eperusteet.ylops.domain.dokumentti.Dokumentti;
import fi.vm.sade.eperusteet.ylops.domain.dokumentti.DokumenttiTila;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.time.DateUtils;

import java.util.Date;

@Slf4j
public class DokumenttiUtils {
    public static final int MAX_TIME_IN_MINUTES = 60;

    public static boolean isTimePass(Dokumentti dokumentti) {
        return (dokumentti.getTila().equals(DokumenttiTila.LUODAAN) || dokumentti.getTila().equals(DokumenttiTila.JONOSSA)) && isTimePass(dokumentti.getAloitusaika());
    }

    public static boolean isTimePass(Date date) {
        if (date == null) {
            return true;
        }

        Date newDate = DateUtils.addMinutes(date, MAX_TIME_IN_MINUTES);
        return newDate.before(new Date());
    }
}
