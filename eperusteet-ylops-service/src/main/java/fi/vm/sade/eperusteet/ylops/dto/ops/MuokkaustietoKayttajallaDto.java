package fi.vm.sade.eperusteet.ylops.dto.ops;

import fi.vm.sade.eperusteet.ylops.dto.kayttaja.KayttajanTietoDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class MuokkaustietoKayttajallaDto extends OpetussuunnitelmanMuokkaustietoDto {
    private KayttajanTietoDto kayttajanTieto;
}
