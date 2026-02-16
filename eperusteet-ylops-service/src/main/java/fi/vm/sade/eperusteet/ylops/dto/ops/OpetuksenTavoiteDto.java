package fi.vm.sade.eperusteet.ylops.dto.ops;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import fi.vm.sade.eperusteet.ylops.dto.Reference;
import fi.vm.sade.eperusteet.ylops.dto.ReferenceableDto;
import fi.vm.sade.eperusteet.ylops.dto.teksti.LokalisoituTekstiDto;
import fi.vm.sade.eperusteet.ylops.service.external.impl.perustedto.OppiaineenTavoitteenOpetuksenTavoiteDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class OpetuksenTavoiteDto implements ReferenceableDto {
    private Long id;
    private UUID tunniste;
    private LokalisoituTekstiDto tavoite;
    private Set<Reference> laajattavoitteet;
    private Set<Reference> kohdealueet;
    private Set<OpetuksenKeskeinensisaltoalueDto> sisaltoalueet;
}
