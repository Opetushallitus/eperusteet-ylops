package fi.vm.sade.eperusteet.ylops.dto.ops;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import fi.vm.sade.eperusteet.ylops.dto.OpetussuunnitelmaExportDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class OpetussuunnitelmaLaajaDto extends OpetussuunnitelmaExportDto {
    private Set<OpsVuosiluokkakokonaisuusDto> vuosiluokkakokonaisuudet;
    private Set<OpsOppiaineExportDto> oppiaineet;
}
