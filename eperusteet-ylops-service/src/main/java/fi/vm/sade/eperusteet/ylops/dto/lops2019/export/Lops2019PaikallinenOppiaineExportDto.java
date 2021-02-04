package fi.vm.sade.eperusteet.ylops.dto.lops2019.export;


import fi.vm.sade.eperusteet.ylops.dto.lops2019.Lops2019PaikallinenOppiaineDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OrganisaationKoodi;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Lops2019PaikallinenOppiaineExportDto extends Lops2019PaikallinenOppiaineDto {
    List<OrganisaationKoodi.Opintojakso> opintojaksot = new ArrayList<>();
}
