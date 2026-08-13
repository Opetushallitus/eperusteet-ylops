package fi.vm.sade.eperusteet.ylops.dto.tpo.export;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import fi.vm.sade.eperusteet.ylops.dto.OpetussuunnitelmaExportDto;
import fi.vm.sade.eperusteet.ylops.dto.tpo.TaiteenalaDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "Julkaistun taiteen perusopetuksen opetussuunnitelman sisältö julkiseen rajapintaan.")
public class OpetussuunnitelmaExportTpoDto extends OpetussuunnitelmaExportDto {

    @Schema(description = "Opetussuunnitelmaan tallennetut taiteenalat.")
    private List<TaiteenalaDto> taiteenalat = new ArrayList<>();
}
