package fi.vm.sade.eperusteet.ylops.resource.ops;

import fi.vm.sade.eperusteet.ylops.resource.util.AbstractLockController;
import fi.vm.sade.eperusteet.ylops.service.locking.LockService;
import fi.vm.sade.eperusteet.ylops.service.locking.OpsCtx;
import fi.vm.sade.eperusteet.ylops.service.ops.OpsSisaltoLockService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@RequestMapping(value = "/api/opetussuunnitelmat/{opsId}/tekstit/lukko")
@ApiIgnore
@Api(value = "OpsSisaltoLukot")
public class OpsSisaltoLockController extends AbstractLockController<OpsCtx> {
    @Autowired
    private OpsSisaltoLockService service;

    @Override
    protected LockService<OpsCtx> service() {
        return service;
    }

}
