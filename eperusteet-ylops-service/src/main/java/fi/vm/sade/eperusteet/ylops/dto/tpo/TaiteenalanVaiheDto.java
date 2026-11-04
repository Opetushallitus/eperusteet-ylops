package fi.vm.sade.eperusteet.ylops.dto.tpo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
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
public class TaiteenalanVaiheDto {
    private Long id;
    private Long perusteenTaiteenalaVaiheId;
    private List<VapaatekstiPaikallinentarkennusDto> vapaatTekstit = new ArrayList<>();

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LokalisoituTekstiDto nimi;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private TpoPerusteenTaiteenalaVaiheDto perusteenVaihe;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private List<TaiteenOpintokokonaisuusKevytDto> opintokokonaisuudet = new ArrayList<>();
}
