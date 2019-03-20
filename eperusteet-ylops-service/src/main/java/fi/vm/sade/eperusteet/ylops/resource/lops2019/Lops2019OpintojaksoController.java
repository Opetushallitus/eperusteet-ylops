package fi.vm.sade.eperusteet.ylops.resource.lops2019;

import fi.vm.sade.eperusteet.ylops.dto.PoistettuDto;
import fi.vm.sade.eperusteet.ylops.dto.RevisionDto;
import fi.vm.sade.eperusteet.ylops.dto.lops2019.Lops2019OpintojaksoDto;
import fi.vm.sade.eperusteet.ylops.service.lops2019.Lops2019OpintojaksoService;
import fi.vm.sade.eperusteet.ylops.service.util.UpdateWrapperDto;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/opetussuunnitelmat/{opsId}/opintojaksot")
@Api("Lops2019OpintojaksoController")
public class Lops2019OpintojaksoController {

    @Autowired
    private Lops2019OpintojaksoService opintojaksoService;

    @RequestMapping(method = RequestMethod.GET)
    public List<Lops2019OpintojaksoDto> getAllOpintojaksot(
            @PathVariable final Long opsId) {
        return opintojaksoService.getAll(opsId);
    }

    @RequestMapping(value = "/{opintojaksoId}", method = RequestMethod.GET)
    public Lops2019OpintojaksoDto getOpintojakso(
            @PathVariable final Long opsId,
            @PathVariable final Long opintojaksoId) {
        return opintojaksoService.getOne(opsId, opintojaksoId);
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

    @RequestMapping(value = "/poistetut", method = RequestMethod.GET)
    public List<PoistettuDto> getRemoved(
            @PathVariable final Long opsId) {
        return opintojaksoService.getRemoved(opsId);
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
