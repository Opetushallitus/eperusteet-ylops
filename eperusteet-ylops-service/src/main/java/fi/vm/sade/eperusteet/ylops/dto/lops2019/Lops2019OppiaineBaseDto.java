package fi.vm.sade.eperusteet.ylops.dto.lops2019;

import fi.vm.sade.eperusteet.ylops.dto.KoodiDto;
import fi.vm.sade.eperusteet.ylops.dto.teksti.LokalisoituTekstiDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Lops2019OppiaineBaseDto {
    private Long id;
    private LokalisoituTekstiDto nimi;
    private LokalisoituTekstiDto kuvaus;
    private LokalisoituTekstiDto pakollistenModuulienKuvaus;
    private LokalisoituTekstiDto valinnaistenModuulienKuvaus;
    private Lops2019OppiaineenArviointi arviointi;
    private Lops2019OppiaineenTehtava tehtava;
    private Lops2019OppimaaranLaajaAlaisetOsaamisetDto laajaAlaisetOsaamiset;
    private Lops2019OppiaineenTavoitteetDto tavoitteet;

//    @ApiModelProperty(required = true)
    private KoodiDto koodi;
}
