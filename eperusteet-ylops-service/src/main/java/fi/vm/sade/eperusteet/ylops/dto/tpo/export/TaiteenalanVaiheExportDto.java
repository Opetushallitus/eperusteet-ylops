package fi.vm.sade.eperusteet.ylops.dto.tpo.export;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import fi.vm.sade.eperusteet.ylops.dto.ops.VapaatekstiPaikallinentarkennusDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.tpo.TpoPerusteenTaiteenalaVaiheDto;
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
public class TaiteenalanVaiheExportDto {
    private Long id;
    private Long perusteenTaiteenalaVaiheId;
    private LokalisoituTekstiDto nimi;
    private TpoPerusteenTaiteenalaVaiheDto perusteenVaihe;
    private List<VapaatekstiPaikallinentarkennusDto> vapaatTekstit = new ArrayList<>();
    private List<TaiteenOpintokokonaisuusExportDto> opintokokonaisuudet = new ArrayList<>();
}
