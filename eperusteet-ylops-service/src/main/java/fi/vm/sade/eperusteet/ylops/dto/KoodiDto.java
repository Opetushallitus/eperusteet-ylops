package fi.vm.sade.eperusteet.ylops.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.util.Map;

/**
 *
 * @author nkala
 */
@Data
@EqualsAndHashCode(of = {"koodisto", "uri", "versio"})
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class KoodiDto {
    Map<String, String> nimi;
    private String arvo;
    private String uri;
    private String koodisto;
    private Long versio;

    static public KoodiDto of(String koodisto, String arvo) {
        KoodiDto result = new KoodiDto();
        result.setUri(koodisto + "_" + arvo);
        result.setKoodisto(koodisto);
        result.setArvo(arvo);
        return result;
    }
}