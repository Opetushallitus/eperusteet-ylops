package fi.vm.sade.eperusteet.ylops.dto.lukio;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LukioOppiaineTiedotExportDto {
    private LukioOppiaineTiedotDto tiedot;
    private List<LukioOppiaineTiedotDto> oppimaarat;
}
