package fi.vm.sade.eperusteet.ylops.resource.lops2019;

import fi.vm.sade.eperusteet.ylops.dto.lops2019.Lops2019OpintojaksoDto;
import fi.vm.sade.eperusteet.ylops.dto.lops2019.Lops2019PoistettuDto;
import fi.vm.sade.eperusteet.ylops.dto.lops2019.Validointi.Lops2019ValidointiDto;
import fi.vm.sade.eperusteet.ylops.resource.config.InternalApi;
import fi.vm.sade.eperusteet.ylops.resource.util.AuditLogged;
import fi.vm.sade.eperusteet.ylops.service.lops2019.Lops2019Service;
import fi.vm.sade.eperusteet.ylops.service.ops.ValidointiService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/opetussuunnitelmat/{opsId}/lops2019")
@Api("Lops2019")
@InternalApi
public class Lops2019Controller {

    @Autowired
    private ValidointiService validointiService;

    @Autowired
    private Lops2019Service service;

    @RequestMapping(value = "/validointi", method = RequestMethod.GET)
    @AuditLogged
    public Lops2019ValidointiDto getValidointi(
            @PathVariable final Long opsId) {
        return validointiService.getValidointi(opsId);
    }

    @RequestMapping(value = "/palauta/{poistettuId}", method = RequestMethod.POST)
    @AuditLogged
    public void palauta(
            @PathVariable final Long opsId,
            @PathVariable final Long poistettuId) {
        service.restore(opsId, poistettuId);
    }

    @RequestMapping(value = "/poistetut", method = RequestMethod.GET)
    @AuditLogged
    public List<Lops2019PoistettuDto> getRemoved(
            @PathVariable final Long opsId) {
        return service.getRemoved(opsId);
    }

}
