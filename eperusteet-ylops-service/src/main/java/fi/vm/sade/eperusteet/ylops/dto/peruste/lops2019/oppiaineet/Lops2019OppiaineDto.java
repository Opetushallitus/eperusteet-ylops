package fi.vm.sade.eperusteet.ylops.dto.peruste.lops2019.oppiaineet;

import fi.vm.sade.eperusteet.ylops.dto.lops2019.Lops2019OppiaineGenericDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.lops2019.oppiaineet.moduuli.Lops2019ModuuliBaseDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Lops2019OppiaineDto extends Lops2019OppiaineGenericDto<Lops2019OppiaineDto, Lops2019ModuuliBaseDto> {
}
