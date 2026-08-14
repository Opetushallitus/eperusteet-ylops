package fi.vm.sade.eperusteet.ylops.dto.aipe.export;

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
public class AIPEOppiaineExportDto {
    private Long id;
    private Long perusteenOppiaineId;
    private LokalisoituTekstiDto paikallinenTarkennus;
    private List<AIPEOppiaineExportDto> oppimaarat = new ArrayList<>();
    private List<AIPEKurssiExportDto> kurssit = new ArrayList<>();
    private PerusteAIPEOppiaineSisaltoDto perusteSisalto;

    public LokalisoituTekstiDto getNimi() {
        return perusteSisalto != null ? perusteSisalto.getNimi() : null;
    }
}
