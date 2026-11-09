package fi.vm.sade.eperusteet.ylops.dto.tpo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import fi.vm.sade.eperusteet.ylops.dto.peruste.tpo.TpoPerusteenTaiteenalaDto;
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
public class TaiteenalaDto {
    private Long id;
    private Long perusteenTaiteenalaId;
    private LokalisoituTekstiDto paikallinenTarkennus;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LokalisoituTekstiDto nimi;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private TpoPerusteenTaiteenalaDto perusteenTaiteenala;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private List<TaiteenalanVaiheKevytDto> vaiheet = new ArrayList<>();
}
