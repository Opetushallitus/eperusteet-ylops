package fi.vm.sade.eperusteet.ylops.resource.lops2019;

import fi.vm.sade.eperusteet.ylops.dto.PoistettuDto;
import fi.vm.sade.eperusteet.ylops.dto.RevisionDto;
import fi.vm.sade.eperusteet.ylops.dto.lops2019.Lops2019PaikallinenOppiaineDto;
import fi.vm.sade.eperusteet.ylops.resource.util.AuditLogged;
import fi.vm.sade.eperusteet.ylops.service.lops2019.Lops2019OppiaineService;
import fi.vm.sade.eperusteet.ylops.service.util.UpdateWrapperDto;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/opetussuunnitelmat/{opsId}/lops2019/oppiaineet")
@Api("Lops2019Oppiaineet")
public class Lops2019OppiaineController {

    @Autowired
    private Lops2019OppiaineService oppiaineService;

    @RequestMapping(method = RequestMethod.GET)
    @AuditLogged
    public List<Lops2019PaikallinenOppiaineDto> getAllLops2019PaikallisetOppiainet(
            @PathVariable final Long opsId) {
        return oppiaineService.getAll(opsId);
    }

    @RequestMapping(value = "/{oppiaineId}", method = RequestMethod.GET)
    @AuditLogged
    public Lops2019PaikallinenOppiaineDto getLops2019PaikallinenOppiaine(
            @PathVariable final Long opsId,
            @PathVariable final Long oppiaineId) {
        return oppiaineService.getOne(opsId, oppiaineId);
    }

    @RequestMapping(method = RequestMethod.POST)
    @AuditLogged
    public Lops2019PaikallinenOppiaineDto addLops2019PaikallinenOppiaine(
            @PathVariable final Long opsId,
            @RequestBody final Lops2019PaikallinenOppiaineDto oppiaineDto) {
        return oppiaineService.addOppiaine(opsId, oppiaineDto);
    }

    @RequestMapping(value = "/{oppiaineId}", method = RequestMethod.POST)
    @AuditLogged
    public Lops2019PaikallinenOppiaineDto updateLops2019PaikallinenOppiaine(
            @PathVariable final Long opsId,
            @PathVariable final Long oppiaineId,
            @RequestBody final UpdateWrapperDto<Lops2019PaikallinenOppiaineDto> oppiaineDto) {
        return oppiaineService.updateOppiaine(opsId, oppiaineId, oppiaineDto);
    }


    @RequestMapping(value = "/{oppiaineId}", method = RequestMethod.DELETE)
    @AuditLogged
    public void removeLops2019PaikallinenOppiaine(
            @PathVariable final Long opsId,
            @PathVariable final Long oppiaineId) {
        oppiaineService.removeOne(opsId, oppiaineId);
    }

    @RequestMapping(value = "/{oppiaineId}/versiot", method = RequestMethod.GET)
    @AuditLogged
    public List<RevisionDto> getLops2019PaikallinenVersionHistory(
            @PathVariable final Long opsId,
            @PathVariable final Long oppiaineId) {
        return oppiaineService.getVersions(opsId, oppiaineId);
    }

    @RequestMapping(value = "/poistetut", method = RequestMethod.GET)
    @AuditLogged
    public List<PoistettuDto> getLops2019PaikallinenRemoved(
            @PathVariable final Long opsId) {
        return oppiaineService.getRemoved(opsId);
    }

    @RequestMapping(value = "/{oppiaineId}/versiot/{versio}", method = RequestMethod.GET)
    @AuditLogged
    public Lops2019PaikallinenOppiaineDto getLops2019PaikallinenVersion(
            @PathVariable final Long opsId,
            @PathVariable final Long oppiaineId,
            @PathVariable final Integer versio) {
        return oppiaineService.getVersion(opsId, oppiaineId, versio);
    }

    @RequestMapping(value = "/{oppiaineId}/versiot/{versio}", method = RequestMethod.POST)
    @AuditLogged
    public Lops2019PaikallinenOppiaineDto revertLops2019PaikallinenToVersion(
            @PathVariable final Long opsId,
            @PathVariable final Long oppiaineId,
            @PathVariable final Integer versio) {
        return oppiaineService.revertTo(opsId, oppiaineId, versio);
    }

}
