package fi.vm.sade.eperusteet.ylops.resource.util;

import fi.vm.sade.eperusteet.ylops.resource.config.InternalApi;
import fi.vm.sade.eperusteet.ylops.service.util.E2EService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.EntityManager;


@Profile("e2e")
@RequestMapping("/e2e")
@RestController
@InternalApi
public class E2EController {

    @Autowired
    private E2EService service;

    @RequestMapping(method = RequestMethod.POST)
    public void reset() {
        service.reset();
    }

}
