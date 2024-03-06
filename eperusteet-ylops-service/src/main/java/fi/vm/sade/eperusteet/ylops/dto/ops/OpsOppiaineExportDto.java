package fi.vm.sade.eperusteet.ylops.dto.ops;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class OpsOppiaineExportDto {
    private boolean oma;
    private OppiaineExportDto oppiaine;
    private Integer jnro;
}
