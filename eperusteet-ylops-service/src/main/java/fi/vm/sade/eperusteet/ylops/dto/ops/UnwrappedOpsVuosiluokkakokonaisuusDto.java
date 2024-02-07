package fi.vm.sade.eperusteet.ylops.dto.ops;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UnwrappedOpsVuosiluokkakokonaisuusDto {

    private boolean oma;
    @JsonUnwrapped
    private VuosiluokkakokonaisuusDto vuosiluokkakokonaisuus;

    public UnwrappedOpsVuosiluokkakokonaisuusDto(OpsVuosiluokkakokonaisuusDto dto) {
        oma = dto.isOma();
        vuosiluokkakokonaisuus = dto.getVuosiluokkakokonaisuus();
    }
}
