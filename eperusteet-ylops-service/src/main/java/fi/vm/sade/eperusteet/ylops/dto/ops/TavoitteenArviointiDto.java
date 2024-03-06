package fi.vm.sade.eperusteet.ylops.dto.ops;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import fi.vm.sade.eperusteet.ylops.dto.ReferenceableDto;
import fi.vm.sade.eperusteet.ylops.dto.teksti.LokalisoituTekstiDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TavoitteenArviointiDto implements ReferenceableDto {
    private Long id;
    private LokalisoituTekstiDto arvioinninKohde;
    private LokalisoituTekstiDto osaamisenKuvaus;
    private Integer arvosana;

    public LokalisoituTekstiDto getHyvanOsaamisenKuvaus() {
        if(arvosana == null || arvosana == 8) {
            return osaamisenKuvaus;
        }

        return null;
    }
}
