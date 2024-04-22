package fi.vm.sade.eperusteet.ylops.dto.ops;

import fi.vm.sade.eperusteet.ylops.domain.ops.Opetussuunnitelma;
import java.util.Date;

public interface OpetussuunnitelmaJulkaisuKevyt {
    Opetussuunnitelma getOpetussuunnitelma();
    Date getLuotu();
}
