package fi.vm.sade.eperusteet.ylops.dto.ops;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import fi.vm.sade.eperusteet.ylops.dto.Reference;
import fi.vm.sade.eperusteet.ylops.dto.ReferenceableDto;
import fi.vm.sade.eperusteet.ylops.dto.teksti.TekstiosaDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class OppiaineenVuosiluokkakokonaisuusDto implements ReferenceableDto {
    private Long id;
    private Reference vuosiluokkakokonaisuus;
    private TekstiosaDto tehtava;
    private TekstiosaDto yleistavoitteet;
    private TekstiosaDto tyotavat;
    private TekstiosaDto ohjaus;
    private TekstiosaDto arviointi;
    private TekstiosaDto tavoitteistaJohdetutOppimisenTavoitteet;
    private Integer jnro;
    private Boolean piilotettu;

    private Set<OppiaineenVuosiluokkaDto> vuosiluokat;
}
