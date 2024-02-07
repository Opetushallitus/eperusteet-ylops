package fi.vm.sade.eperusteet.ylops.dto.koodisto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import fi.vm.sade.eperusteet.ylops.dto.teksti.LokalisoituTekstiDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class KoodistoKoodiDto {
    private Long id;
    private String koodiUri;
    private String koodiArvo;
    private String versio;
    private String voimassaAlkuPvm;
    private String voimassaLoppuPvm;
    private KoodistoMetadataDto[] metadata;

    public LokalisoituTekstiDto getNimi() {
        Map<String, String> tekstit = new HashMap<>();
        if (this.getMetadata() != null) {
            for (KoodistoMetadataDto metadata : this.getMetadata()) {
                try {
                    tekstit.put(metadata.getKieli(), metadata.getNimi());
                } catch (IllegalArgumentException ignored) {
                }
            }
            return new LokalisoituTekstiDto(tekstit);
        }

        return null;
    }
}
