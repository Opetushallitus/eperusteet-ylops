package fi.vm.sade.eperusteet.ylops.dto.lops2019.Validointi;

import fi.vm.sade.eperusteet.ylops.domain.ValidationCategory;
import fi.vm.sade.eperusteet.ylops.dto.teksti.LokalisoituTekstiDto;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ValidoinninKohdeDto {
    private Long id;
    private LokalisoituTekstiDto nimi;
    private String kuvaus;
    private ValidationCategory targetClass;
    private boolean failed;
    private boolean isFatal;
    private Map<String, Object> meta;

}
