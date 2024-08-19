package fi.vm.sade.eperusteet.ylops.dto.ops;

import java.util.Date;

public interface OpetussuunnitelmaWithLatestTilaUpdateTime {
    Long getId();
    Date getViimeisinTilaMuutosAika();
}