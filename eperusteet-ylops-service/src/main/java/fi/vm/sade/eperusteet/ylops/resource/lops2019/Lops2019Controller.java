package fi.vm.sade.eperusteet.ylops.resource.lops2019;

import fi.vm.sade.eperusteet.ylops.dto.lops2019.Validointi.Lops2019ValidointiDto;
import fi.vm.sade.eperusteet.ylops.resource.util.AuditLogged;
import fi.vm.sade.eperusteet.ylops.service.lops2019.Lops2019Service;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/opetussuunnitelmat/{opsId}/lops2019")
@Api("Lops2019Oppiaineet")
public class Lops2019Controller {
    @Autowired
    private Lops2019Service lops2019Service;

    @RequestMapping(value = "/validointi", method = RequestMethod.GET)
    @AuditLogged
    public Lops2019ValidointiDto getValidointi(
            @PathVariable final Long opsId) {
        return lops2019Service.getValidointi(opsId);
    }
}
