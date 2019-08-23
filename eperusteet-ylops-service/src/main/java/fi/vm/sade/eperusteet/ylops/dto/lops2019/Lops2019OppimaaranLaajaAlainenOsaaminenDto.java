package fi.vm.sade.eperusteet.ylops.dto.lops2019;

import fi.vm.sade.eperusteet.ylops.dto.Reference;
import fi.vm.sade.eperusteet.ylops.dto.teksti.LokalisoituTekstiDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Lops2019OppimaaranLaajaAlainenOsaaminenDto {
    private Reference laajaAlaisetOsaamiset;
    private LokalisoituTekstiDto kuvaus;
}
