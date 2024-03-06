package fi.vm.sade.eperusteet.ylops.dto.lukio;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class LukioOppiaineRakenneListausDto extends LukioOppiaineRakenneDto<LukioOppiaineRakenneListausDto, LukiokurssiListausOpsDto> {
}
