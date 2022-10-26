package fi.vm.sade.eperusteet.ylops.dto;

import fi.vm.sade.eperusteet.ylops.dto.kayttaja.KayttajanTietoDto;
import lombok.Data;

@Data
public class RevisionKayttajaDto extends RevisionDto {
    private KayttajanTietoDto kayttajanTieto;
}
