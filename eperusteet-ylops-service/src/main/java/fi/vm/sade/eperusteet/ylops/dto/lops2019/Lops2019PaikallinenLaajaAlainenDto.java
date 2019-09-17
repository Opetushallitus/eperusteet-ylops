package fi.vm.sade.eperusteet.ylops.dto.lops2019;

import fi.vm.sade.eperusteet.ylops.dto.teksti.LokalisoituTekstiDto;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Lops2019PaikallinenLaajaAlainenDto {
    private String koodi;
    private LokalisoituTekstiDto kuvaus;
}
