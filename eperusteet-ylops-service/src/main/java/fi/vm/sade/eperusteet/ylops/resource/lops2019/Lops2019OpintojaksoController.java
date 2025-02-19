package fi.vm.sade.eperusteet.ylops.resource.lops2019;

import fi.vm.sade.eperusteet.ylops.dto.RevisionDto;
import fi.vm.sade.eperusteet.ylops.dto.lops2019.Lops2019OpintojaksoDto;
import fi.vm.sade.eperusteet.ylops.dto.lops2019.Lops2019OpintojaksoPerusteDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpetussuunnitelmaDto;
import fi.vm.sade.eperusteet.ylops.service.lops2019.Lops2019OpintojaksoService;
import fi.vm.sade.eperusteet.ylops.service.util.UpdateWrapperDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/opetussuunnitelmat/{opsId}/lops2019/opintojaksot")
@Tag(name = "Lops2019Opintojaksot")
public class Lops2019OpintojaksoController {

    @Autowired
    private Lops2019OpintojaksoService opintojaksoService;

    @RequestMapping(method = RequestMethod.GET)
    public List<Lops2019OpintojaksoDto> getAllOpintojaksot(
            @PathVariable final Long opsId,
            @RequestParam(required = false) final String moduuliUri) {
        return opintojaksoService.getAll(opsId, Lops2019OpintojaksoDto.class);
    }

    @RequestMapping(value = "/tuodut", method = RequestMethod.GET)
    public List<Lops2019OpintojaksoDto> getTuodutOpintojaksot(
            @PathVariable final Long opsId) {
        return opintojaksoService.getTuodut(opsId, Lops2019OpintojaksoDto.class);
    }

    @RequestMapping(value = "/tuodut/{opintojaksoId}", method = RequestMethod.GET)
    public Lops2019OpintojaksoDto getTuotuOpintojakso(
            @PathVariable final Long opsId,
            @PathVariable final Long opintojaksoId) {
        return opintojaksoService.getTuotu(opsId, opintojaksoId);
    }

    @RequestMapping(value = "/{opintojaksoId}", method = RequestMethod.GET)
    public Lops2019OpintojaksoDto getOpintojakso(
            @PathVariable final Long opsId,
            @PathVariable final Long opintojaksoId) {
        return opintojaksoService.getOne(opsId, opintojaksoId);
    }

    @RequestMapping(value = "/{opintojaksoId}/opetussuunnitelma", method = RequestMethod.GET)
    public OpetussuunnitelmaDto getOpintojaksonOpetussuunnitelma(
            @PathVariable final Long opsId,
            @PathVariable final Long opintojaksoId) {
        return opintojaksoService.getOpintojaksonOpetussuunnitelma(opsId, opintojaksoId);
    }

    @RequestMapping(value = "/{opintojaksoId}/peruste", method = RequestMethod.GET)
    public Lops2019OpintojaksoPerusteDto getOpintojaksonPeruste(
            @PathVariable final Long opsId,
            @PathVariable final Long opintojaksoId) {
        return opintojaksoService.getOpintojaksonPeruste(opsId, opintojaksoId);
    }

    @RequestMapping(method = RequestMethod.POST)
    public Lops2019OpintojaksoDto addOpintojakso(
            @PathVariable final Long opsId,
            @RequestBody final Lops2019OpintojaksoDto opintojaksoDto) {
        return opintojaksoService.addOpintojakso(opsId, opintojaksoDto);
    }

    @RequestMapping(value = "/{opintojaksoId}", method = RequestMethod.POST)
    public Lops2019OpintojaksoDto updateOpintojakso(
            @PathVariable final Long opsId,
            @PathVariable final Long opintojaksoId,
            @RequestBody final UpdateWrapperDto<Lops2019OpintojaksoDto> opintojaksoDto) {
        return opintojaksoService.updateOpintojakso(opsId, opintojaksoId, opintojaksoDto);
    }

    @RequestMapping(value = "/{opintojaksoId}", method = RequestMethod.DELETE)
    public void removeOpintojakso(
            @PathVariable final Long opsId,
            @PathVariable final Long opintojaksoId) {
        opintojaksoService.removeOne(opsId, opintojaksoId);
    }

    @RequestMapping(value = "/{opintojaksoId}/versiot", method = RequestMethod.GET)
    public List<RevisionDto> getVersionHistory(
            @PathVariable final Long opsId,
            @PathVariable final Long opintojaksoId) {
        return opintojaksoService.getVersions(opsId, opintojaksoId);
    }

    @RequestMapping(value = "/{opintojaksoId}/versiot/{versio}", method = RequestMethod.GET)
    public Lops2019OpintojaksoDto getVersion(
            @PathVariable final Long opsId,
            @PathVariable final Long opintojaksoId,
            @PathVariable final Integer versio) {
        return opintojaksoService.getVersion(opsId, opintojaksoId, versio);
    }

    @RequestMapping(value = "/{opintojaksoId}/versiot/{versio}", method = RequestMethod.POST)
    public Lops2019OpintojaksoDto revertToVersion(
            @PathVariable final Long opsId,
            @PathVariable final Long opintojaksoId,
            @PathVariable final Integer versio) {
        return opintojaksoService.revertTo(opsId, opintojaksoId, versio);
    }

}
