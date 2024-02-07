package fi.vm.sade.eperusteet.ylops.dto.lops2019;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import fi.vm.sade.eperusteet.ylops.dto.teksti.LokalisoituTekstiDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Lops2019ModuulinTavoitteetDto {
    private LokalisoituTekstiDto kohde;
    private List<LokalisoituTekstiDto> tavoitteet;
}
