package fi.vm.sade.eperusteet.ylops.dto.ops;

import fi.vm.sade.eperusteet.ylops.dto.Reference;
import fi.vm.sade.eperusteet.ylops.dto.ReferenceableDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OppiaineenVuosiluokkakokonaisuusSuppeaDto implements ReferenceableDto {
    private Long id;
    private Reference vuosiluokkakokonaisuus;
    private Integer jnro;
    private Set<OppiaineenVuosiluokkaKevytDto> vuosiluokat;
    private Boolean piilotettu;
}
