package fi.vm.sade.eperusteet.ylops.dto.export;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import fi.vm.sade.eperusteet.ylops.dto.OpetussuunnitelmaExportDto;
import fi.vm.sade.eperusteet.ylops.dto.lops2019.export.Lops2019OpintojaksoExportDto;
import fi.vm.sade.eperusteet.ylops.dto.lops2019.export.Lops2019OppiaineExportDto;
import fi.vm.sade.eperusteet.ylops.dto.lops2019.export.Lops2019PaikallinenOppiaineExportDto;
import fi.vm.sade.eperusteet.ylops.dto.lukio.LukioOppiaineRakenneListausDto;
import fi.vm.sade.eperusteet.ylops.dto.lukio.LukioOppiaineTiedotDto;
import fi.vm.sade.eperusteet.ylops.dto.lukio.LukioOppiaineTiedotExportDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.lops2019.laajaalainenosaaminen.Lops2019LaajaAlainenOsaaminenDto;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class OpetussuunnitelmaExportLopsDto extends OpetussuunnitelmaExportDto {
    private List<LukioOppiaineTiedotExportDto> oppiaineet;
}
