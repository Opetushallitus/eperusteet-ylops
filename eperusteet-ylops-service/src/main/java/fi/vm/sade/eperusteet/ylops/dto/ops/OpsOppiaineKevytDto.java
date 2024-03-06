package fi.vm.sade.eperusteet.ylops.dto.ops;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OpsOppiaineKevytDto {
    private boolean oma;
    private Integer jnro;
    private OppiaineSuppeaDto oppiaine;
}
