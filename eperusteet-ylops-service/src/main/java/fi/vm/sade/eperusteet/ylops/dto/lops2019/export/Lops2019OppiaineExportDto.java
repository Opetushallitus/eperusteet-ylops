package fi.vm.sade.eperusteet.ylops.dto.lops2019.export;

import fi.vm.sade.eperusteet.ylops.dto.lops2019.Lops2019OppiaineGenericDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OrganisaationKoodi;
import fi.vm.sade.eperusteet.ylops.dto.peruste.lops2019.oppiaineet.moduuli.Lops2019ModuuliDto;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Lops2019OppiaineExportDto extends Lops2019OppiaineGenericDto<Lops2019OppiaineExportDto, Lops2019ModuuliDto> {
    List<OrganisaationKoodi.Opintojakso> opintojaksot = new ArrayList<>();
}
