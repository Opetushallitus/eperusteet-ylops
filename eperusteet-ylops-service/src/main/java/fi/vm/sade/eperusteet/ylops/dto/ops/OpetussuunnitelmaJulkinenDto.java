package fi.vm.sade.eperusteet.ylops.dto.ops;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import fi.vm.sade.eperusteet.ylops.domain.KoulutusTyyppi;
import fi.vm.sade.eperusteet.ylops.domain.KoulutustyyppiToteutus;
import fi.vm.sade.eperusteet.ylops.domain.Tila;
import fi.vm.sade.eperusteet.ylops.domain.Tyyppi;
import fi.vm.sade.eperusteet.ylops.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.ylops.dto.koodisto.KoodistoDto;
import fi.vm.sade.eperusteet.ylops.dto.koodisto.OrganisaatioDto;
import fi.vm.sade.eperusteet.ylops.dto.teksti.LokalisoituTekstiDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class OpetussuunnitelmaJulkinenDto implements Serializable {
    private Long id;
    private Set<Kieli> julkaisukielet;
    private Set<OrganisaatioDto> organisaatiot;
    private Set<KoodistoDto> kunnat;
    private LokalisoituTekstiDto nimi;
    private KoulutusTyyppi koulutustyyppi;
    private Tila tila;
    private Tyyppi tyyppi;
    private boolean esikatseltavissa;
    private KoulutustyyppiToteutus toteutus;
}
