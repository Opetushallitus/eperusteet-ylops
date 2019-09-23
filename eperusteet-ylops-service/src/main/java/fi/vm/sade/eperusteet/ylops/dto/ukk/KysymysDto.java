package fi.vm.sade.eperusteet.ylops.dto.ukk;

import fi.vm.sade.eperusteet.ylops.dto.koodisto.OrganisaatioDto;
import fi.vm.sade.eperusteet.ylops.dto.teksti.LokalisoituTekstiDto;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

@Getter
@Setter
public class KysymysDto implements Serializable {
    private Long id;
    private LokalisoituTekstiDto kysymys;
    private LokalisoituTekstiDto vastaus;
    private Set<OrganisaatioDto> organisaatiot;
    private Date luotu;
}
