package fi.vm.sade.eperusteet.ylops.dto.peruste.lops2019.oppiaineet;

import fi.vm.sade.eperusteet.ylops.dto.teksti.LokalisoituTekstiDto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Lops2019OppiaineTavoitteetDto {
    private LokalisoituTekstiDto kuvaus;
    private List<Lops2019OppiaineTavoitealueDto> tavoitealueet;
}
