package fi.vm.sade.eperusteet.ylops.dto.tpo;

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
public class TaiteenOpintokokonaisuusKevytDto {
    private Long id;
    private LokalisoituTekstiDto nimi;
    private Long laajuus;
    private List<Long> tavoitealueIdt = new ArrayList<>();
}
