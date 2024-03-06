package fi.vm.sade.eperusteet.ylops.dto.peruste.lops2019.oppiaineet;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import fi.vm.sade.eperusteet.ylops.dto.teksti.LokalisoituTekstiDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Lops2019OppiaineTavoitealueDto {
    private LokalisoituTekstiDto nimi;
    private LokalisoituTekstiDto kohde;
    private List<LokalisoituTekstiDto> tavoitteet = new ArrayList<>();
}
