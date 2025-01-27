package fi.vm.sade.eperusteet.ylops.dto.lops2019;

import fi.vm.sade.eperusteet.ylops.dto.peruste.lops2019.oppiaineet.Lops2019OppiaineBaseDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.lops2019.oppiaineet.moduuli.Lops2019ModuuliBaseDto;
import fi.vm.sade.eperusteet.ylops.dto.teksti.LokalisoituTekstiDto;
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
public class Lops2019OppiaineGenericDto<
        OppimaaraTyyppi extends Lops2019OppiaineBaseDto,
        ModuuliTyyppi extends Lops2019ModuuliBaseDto> extends Lops2019OppiaineBaseDto {
    private LokalisoituTekstiDto pakollisetModuulitKuvaus;
    private LokalisoituTekstiDto valinnaisetModuulitKuvaus;
    @Schema(name = "moduulit")
    private List<ModuuliTyyppi> moduulit = new ArrayList<>();
    @Schema(name = "oppimaarat")
    private List<OppimaaraTyyppi> oppimaarat = new ArrayList<>();
}
