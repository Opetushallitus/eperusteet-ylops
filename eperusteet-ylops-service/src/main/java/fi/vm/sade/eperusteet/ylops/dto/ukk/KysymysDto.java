package fi.vm.sade.eperusteet.ylops.dto.ukk;

import fi.vm.sade.eperusteet.ylops.dto.koodisto.OrganisaatioDto;
import fi.vm.sade.eperusteet.ylops.dto.teksti.LokalisoituTekstiDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KysymysDto implements Serializable {
    private Long id;
    private LokalisoituTekstiDto kysymys;
    private LokalisoituTekstiDto vastaus;
    private Set<OrganisaatioDto> organisaatiot;
    private Date luotu;
}
