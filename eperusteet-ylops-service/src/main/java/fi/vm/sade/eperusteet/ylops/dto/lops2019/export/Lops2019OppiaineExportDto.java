package fi.vm.sade.eperusteet.ylops.dto.lops2019.export;

import fi.vm.sade.eperusteet.ylops.dto.lops2019.Lops2019OppiaineGenericDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpsSisaltoViite;
import fi.vm.sade.eperusteet.ylops.dto.peruste.lops2019.oppiaineet.moduuli.Lops2019ModuuliDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class Lops2019OppiaineExportDto extends Lops2019OppiaineGenericDto<Lops2019OppiaineExportDto, Lops2019ModuuliDto> {
    List<OpsSisaltoViite.Opintojakso> opintojaksot = new ArrayList<>();
}
