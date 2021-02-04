package fi.vm.sade.eperusteet.ylops.dto.lops2019;

import fi.vm.sade.eperusteet.ylops.dto.peruste.lops2019.oppiaineet.Lops2019OppiaineBaseDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.lops2019.oppiaineet.moduuli.Lops2019ModuuliBaseDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.lops2019.oppiaineet.moduuli.Lops2019ModuuliDto;
import fi.vm.sade.eperusteet.ylops.dto.teksti.LokalisoituTekstiDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Lops2019OppiaineGenericDto<
        OppimaaraTyyppi extends Lops2019OppiaineBaseDto,
        ModuuliTyyppi extends Lops2019ModuuliBaseDto> extends Lops2019OppiaineBaseDto {
    private LokalisoituTekstiDto pakollisetModuulitKuvaus;
    private LokalisoituTekstiDto valinnaisetModuulitKuvaus;
    private List<ModuuliTyyppi> moduulit = new ArrayList<>();
    private List<OppimaaraTyyppi> oppimaarat = new ArrayList<>();
}
