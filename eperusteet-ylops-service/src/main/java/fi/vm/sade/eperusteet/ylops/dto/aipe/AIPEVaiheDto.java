package fi.vm.sade.eperusteet.ylops.dto.aipe;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import fi.vm.sade.eperusteet.ylops.dto.aipe.export.PerusteAIPEVaiheSisaltoDto;
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
public class AIPEVaiheDto {
    private Long id;
    private Long perusteenVaiheId;
    private LokalisoituTekstiDto paikallinenTarkennus;
    private boolean piilotettu;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private List<AIPEOppiaineKevytDto> oppiaineet = new ArrayList<>();
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private PerusteAIPEVaiheSisaltoDto perusteSisalto;

    public LokalisoituTekstiDto getNimi() {
        return perusteSisalto != null ? perusteSisalto.getNimi() : null;
    }
}
