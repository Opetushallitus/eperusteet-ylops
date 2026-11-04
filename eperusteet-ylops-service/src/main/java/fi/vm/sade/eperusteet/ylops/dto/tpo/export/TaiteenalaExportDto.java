package fi.vm.sade.eperusteet.ylops.dto.tpo.export;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
public class TaiteenalaExportDto {
    private Long id;
    private Long perusteenTaiteenalaId;
    private LokalisoituTekstiDto nimi;
    private LokalisoituTekstiDto paikallinenTarkennus;
    private TpoPerusteenTaiteenalaDto perusteenTaiteenala;
    private List<TaiteenalanVaiheExportDto> vaiheet = new ArrayList<>();
}
