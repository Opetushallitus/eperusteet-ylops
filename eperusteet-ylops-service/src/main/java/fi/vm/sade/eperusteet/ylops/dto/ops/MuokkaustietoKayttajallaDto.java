package fi.vm.sade.eperusteet.ylops.dto.ops;

import fi.vm.sade.eperusteet.ylops.dto.kayttaja.KayttajanTietoDto;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class MuokkaustietoKayttajallaDto extends OpetussuunnitelmanMuokkaustietoDto {
    private KayttajanTietoDto kayttajanTieto;
}
