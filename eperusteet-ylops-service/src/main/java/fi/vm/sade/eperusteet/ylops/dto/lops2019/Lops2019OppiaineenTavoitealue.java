package fi.vm.sade.eperusteet.ylops.dto.lops2019;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import fi.vm.sade.eperusteet.ylops.domain.teksti.LokalisoituTeksti;
import fi.vm.sade.eperusteet.ylops.dto.teksti.LokalisoituTekstiDto;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class Lops2019OppiaineenTavoitealue {
    private LokalisoituTekstiDto nimi;
//    private LokalisoituTekstiDto kohde;
//    private List<LokalisoituTekstiDto> tavoitteet = new ArrayList<>();
}
