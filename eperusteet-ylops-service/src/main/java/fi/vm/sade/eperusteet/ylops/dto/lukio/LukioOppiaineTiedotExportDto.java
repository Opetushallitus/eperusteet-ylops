package fi.vm.sade.eperusteet.ylops.dto.lukio;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LukioOppiaineTiedotExportDto {
    private LukioOppiaineTiedotDto tiedot;
    private List<LukioOppiaineTiedotDto> oppimaarat;
}