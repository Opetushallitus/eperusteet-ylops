package fi.vm.sade.eperusteet.ylops.service.util;

import fi.vm.sade.eperusteet.ylops.domain.KoulutusTyyppi;
import fi.vm.sade.eperusteet.ylops.dto.OpetussuunnitelmaExportDto;
import lombok.experimental.UtilityClass;

import java.util.Collection;
import java.util.List;

@UtilityClass
public class JulkaisuUtil {

    private static final Collection<KoulutusTyyppi> PERUSTE_MERGE_TUETTU = List.of(KoulutusTyyppi.ESIOPETUS, KoulutusTyyppi.VARHAISKASVATUS);

    public static boolean opetussuunnitelmanPerusteDataMergeTuettu(OpetussuunnitelmaExportDto exportDto) {
        return PERUSTE_MERGE_TUETTU.contains(exportDto.getKoulutustyyppi());
    }
}
