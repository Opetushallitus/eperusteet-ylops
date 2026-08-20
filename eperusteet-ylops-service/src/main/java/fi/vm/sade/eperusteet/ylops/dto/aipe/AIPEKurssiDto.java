package fi.vm.sade.eperusteet.ylops.dto.aipe;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import fi.vm.sade.eperusteet.ylops.dto.aipe.export.PerusteAIPEKurssiSisaltoDto;
import fi.vm.sade.eperusteet.ylops.dto.teksti.LokalisoituTekstiDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AIPEKurssiDto {
    private Long id;
    private Long perusteenKurssiId;
    private LokalisoituTekstiDto paikallinenTarkennus;
    private boolean piilotettu;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private PerusteAIPEKurssiSisaltoDto perusteSisalto;

    public LokalisoituTekstiDto getNimi() {
      return perusteSisalto != null ? perusteSisalto.getNimi() : null;
  }
}
