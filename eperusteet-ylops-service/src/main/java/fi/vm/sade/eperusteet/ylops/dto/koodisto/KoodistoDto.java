package fi.vm.sade.eperusteet.ylops.dto.koodisto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import fi.vm.sade.eperusteet.ylops.dto.teksti.LokalisoituTekstiDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class KoodistoDto {
    private Long id;
    private String koodiUri;
    private String koodiArvo;
    private LokalisoituTekstiDto nimi;

    public KoodistoDto(Long id, String koodiUri, String koodiArvo) {
        this.id = id;
        this.koodiUri = koodiUri;
        this.koodiArvo = koodiArvo;
    }
}
