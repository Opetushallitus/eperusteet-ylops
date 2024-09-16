package fi.vm.sade.eperusteet.ylops.dto.ops;

import fi.vm.sade.eperusteet.ylops.domain.Vuosiluokka;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OppiaineenTallennusDto {
    private OppiaineDto oppiaine;
    private Long vuosiluokkakokonaisuusId;
    private Set<Vuosiluokka> vuosiluokat;
    private List<OpetuksenTavoiteDto> tavoitteet;
}
