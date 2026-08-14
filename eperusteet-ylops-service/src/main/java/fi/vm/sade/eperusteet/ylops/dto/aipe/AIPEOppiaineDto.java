package fi.vm.sade.eperusteet.ylops.dto.aipe;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import fi.vm.sade.eperusteet.ylops.dto.KoodiDto;
import fi.vm.sade.eperusteet.ylops.dto.aipe.export.PerusteAIPEOppiaineSisaltoDto;
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
public class AIPEOppiaineDto {
    private Long id;
    private Long perusteenOppiaineId;
    private LokalisoituTekstiDto paikallinenTarkennus;
    private boolean piilotettu;
    private List<Long> piilotetutTavoitteet = new ArrayList<>();
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private List<AIPEOppiaineKevytDto> oppimaarat = new ArrayList<>();
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private List<AIPEKurssiKevytDto> kurssit = new ArrayList<>();
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private PerusteAIPEOppiaineSisaltoDto perusteSisalto;

    public LokalisoituTekstiDto getNimi() {
      return perusteSisalto != null ? perusteSisalto.getNimi() : null;
  }
}
