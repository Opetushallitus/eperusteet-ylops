package fi.vm.sade.eperusteet.ylops.dto.ops;

import java.util.Date;

public interface OpetussuunnitelmaJulkaisuKevyt {
    Long getId();
    int getRevision();
    Date getLuotu();
    Date getPerusteJulkaisuAika();
}
