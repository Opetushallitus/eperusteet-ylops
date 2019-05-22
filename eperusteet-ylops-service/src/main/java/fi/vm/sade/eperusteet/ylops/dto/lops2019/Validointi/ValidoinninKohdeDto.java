package fi.vm.sade.eperusteet.ylops.dto.lops2019.Validointi;

import fi.vm.sade.eperusteet.ylops.domain.ValidationCategory;
import fi.vm.sade.eperusteet.ylops.dto.teksti.LokalisoituTekstiDto;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ValidoinninKohdeDto {
    private Long id;
    private LokalisoituTekstiDto nimi;
    private String kuvaus;
    private ValidationCategory targetClass;
    private boolean failed;
    private boolean isFatal;

    public ValidoinninKohdeDto(Long id, LokalisoituTekstiDto nimi, String kuvaus, ValidationCategory targetClass, boolean failed, boolean isFatal) {
        this.id = id;
        this.nimi = nimi;
        this.kuvaus = kuvaus;
        this.targetClass = targetClass;
        this.failed = failed;
        this.isFatal = isFatal;
    }
}
