package fi.vm.sade.eperusteet.ylops.resource.lops2019;

import fi.vm.sade.eperusteet.ylops.dto.lops2019.Lops2019ModuuliDto;
import fi.vm.sade.eperusteet.ylops.dto.lops2019.Lops2019OppiaineDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.PerusteTekstiKappaleViiteMatalaDto;
import fi.vm.sade.eperusteet.ylops.service.external.EperusteetService;
import fi.vm.sade.eperusteet.ylops.service.lops2019.Lops2019Service;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

@RestController
@RequestMapping("/opetussuunnitelmat/{opsId}/peruste")
@Api("Lops2019PerusteController")
public class Lops2019PerusteController {

    @Autowired
    private EperusteetService eperusteetService;

    @Autowired
    private Lops2019Service lopsService;

    @RequestMapping(value = "/oppiaineet", method = RequestMethod.GET)
    public List<Lops2019OppiaineDto> getAllLops2019PerusteOppiaineet(
            @PathVariable final Long opsId) {
        return lopsService.getPerusteOppiaineet(opsId);
    }

    @RequestMapping(value = "/oppiaineet/{oppiaineId}", method = RequestMethod.GET)
    public List<Lops2019OppiaineDto> getAllLops2019PerusteOppiaine(
            @PathVariable final Long opsId,
            @PathVariable final Long oppiaineId) {
        return lopsService.getPerusteOppiaine(opsId, oppiaineId);
    }

    @RequestMapping(value = "/oppiaineet/{oppiaineId}/moduulit/{moduuliId}", method = RequestMethod.GET)
    public Lops2019ModuuliDto getAllLops2019PerusteModuuli(
            @PathVariable final Long opsId,
            @PathVariable final Long oppiaineId,
            @PathVariable final Long moduuliId) {
        return lopsService.getPerusteModuuli(opsId, oppiaineId, moduuliId);
    }
    @RequestMapping(value = "/tekstikappaleet", method = RequestMethod.GET)
    public PerusteTekstiKappaleViiteMatalaDto getAllLops2019PerusteTekstikappaleet(
            @PathVariable final Long opsId) {
        return lopsService.getPerusteTekstikappaleet(opsId);
    }

    @RequestMapping(value = "/tekstikappaleet/{tekstikappaleId}", method = RequestMethod.GET)
    public PerusteTekstiKappaleViiteMatalaDto getAllLops2019PerusteTekstikappale(
            @PathVariable final Long opsId,
            @PathVariable final Long tekstikappaleId) {
        return lopsService.getPerusteTekstikappale(opsId, tekstikappaleId);
    }

}
