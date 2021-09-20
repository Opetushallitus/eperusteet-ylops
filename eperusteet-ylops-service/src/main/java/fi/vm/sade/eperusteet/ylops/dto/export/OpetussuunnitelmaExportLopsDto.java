package fi.vm.sade.eperusteet.ylops.dto.export;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import fi.vm.sade.eperusteet.ylops.dto.OpetussuunnitelmaExportDto;
import fi.vm.sade.eperusteet.ylops.dto.lukio.LukioOppiaineTiedotExportDto;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class OpetussuunnitelmaExportLopsDto extends OpetussuunnitelmaExportDto {
    private List<LukioOppiaineTiedotExportDto> oppiaineet;
}
