package fi.vm.sade.eperusteet.ylops.dto.lukio;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * User: tommiratamaa
 * Date: 12.1.2016
 * Time: 14.34
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LukioOppiaineTiedotExportDto {
    private LukioOppiaineTiedotDto tiedot;
    private List<LukioOppiaineTiedotDto> oppimaarat;
}
