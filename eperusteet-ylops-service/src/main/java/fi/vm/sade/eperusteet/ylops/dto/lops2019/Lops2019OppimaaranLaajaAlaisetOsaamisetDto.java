package fi.vm.sade.eperusteet.ylops.dto.lops2019;

import fi.vm.sade.eperusteet.ylops.dto.teksti.LokalisoituTekstiDto;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
public class Lops2019OppimaaranLaajaAlaisetOsaamisetDto {
    private LokalisoituTekstiDto kuvaus;
    private List<Lops2019OppimaaranLaajaAlainenOsaaminenDto> laajaAlaisetOsaamiset = new ArrayList<>();
}
