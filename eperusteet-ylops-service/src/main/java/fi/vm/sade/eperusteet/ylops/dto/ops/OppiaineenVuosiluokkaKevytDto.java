package fi.vm.sade.eperusteet.ylops.dto.ops;

import fi.vm.sade.eperusteet.ylops.domain.Vuosiluokka;
import fi.vm.sade.eperusteet.ylops.dto.ReferenceableDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OppiaineenVuosiluokkaKevytDto implements ReferenceableDto {
    private Long id;
    private Vuosiluokka vuosiluokka;
}
