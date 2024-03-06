package fi.vm.sade.eperusteet.ylops.dto.ops;

import fi.vm.sade.eperusteet.ylops.dto.PoistettuDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Deprecated
public class PoistettuOppiaineDto extends PoistettuDto {
    private Long oppiaine;
}
