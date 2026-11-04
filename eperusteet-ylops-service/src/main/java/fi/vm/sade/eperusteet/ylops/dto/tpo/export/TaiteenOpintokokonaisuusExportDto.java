package fi.vm.sade.eperusteet.ylops.dto.tpo.export;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import fi.vm.sade.eperusteet.ylops.dto.peruste.tpo.PerusteTavoiteAlueDto;
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
public class TaiteenOpintokokonaisuusExportDto {
    private Long id;
    private LokalisoituTekstiDto nimi;
    private Long laajuus;
    private LokalisoituTekstiDto osaamisenArviointi;
    private List<Long> tavoitealueIdt = new ArrayList<>();
    private List<PerusteTavoiteAlueDto> tavoitealueet = new ArrayList<>();
}
