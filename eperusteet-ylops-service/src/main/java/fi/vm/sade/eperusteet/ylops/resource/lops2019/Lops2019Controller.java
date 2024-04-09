package fi.vm.sade.eperusteet.ylops.resource.lops2019;

import fi.vm.sade.eperusteet.ylops.dto.lops2019.Lops2019PoistettuDto;
import fi.vm.sade.eperusteet.ylops.resource.config.InternalApi;
import fi.vm.sade.eperusteet.ylops.service.lops2019.Lops2019Service;
import fi.vm.sade.eperusteet.ylops.service.ops.PoistoService;
import fi.vm.sade.eperusteet.ylops.service.ops.ValidointiService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/api/opetussuunnitelmat/{opsId}/lops2019")
@Api("Lops2019")
@InternalApi
public class Lops2019Controller {

    @Autowired
    private ValidointiService validointiService;

    @Autowired
    private Lops2019Service service;

    @Autowired
    private PoistoService poistoService;

    @RequestMapping(value = "/palauta/{poistettuId}", method = RequestMethod.POST)
    public void palauta(
            @PathVariable final Long opsId,
            @PathVariable final Long poistettuId) {
        poistoService.restore(opsId, poistettuId);
    }

    @RequestMapping(value = "/poistetut", method = RequestMethod.GET)
    public List<Lops2019PoistettuDto> getRemoved(
            @PathVariable final Long opsId) {
        return poistoService.getRemoved(opsId);
    }

}
