package fi.vm.sade.eperusteet.ylops.dto.kayttaja;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class KayttajanTietoDto {
    public KayttajanTietoDto(String oidHenkilo) {
        this.oidHenkilo = oidHenkilo;
    }

    String kayttajanimi;
    String kutsumanimi;
    String etunimet;
    String sukunimi;
    String oidHenkilo;
    String kieliKoodi;
    JsonNode yhteystiedot;
}
