package fi.vm.sade.eperusteet.ylops.dto.ops;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UnwrappedOpsOppiaineDto {
    private boolean oma;
    @JsonUnwrapped
    private OppiaineDto oppiaine;

    public UnwrappedOpsOppiaineDto(OpsOppiaineDto dto) {
        oma = dto.isOma();
        oppiaine = dto.getOppiaine();
    }
}
