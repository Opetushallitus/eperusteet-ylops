package fi.vm.sade.eperusteet.ylops.resource.ops;

import fi.vm.sade.eperusteet.ylops.resource.util.AbstractLockController;
import fi.vm.sade.eperusteet.ylops.service.locking.LockService;
import fi.vm.sade.eperusteet.ylops.service.ops.OpsTekstikappaleCtx;
import fi.vm.sade.eperusteet.ylops.service.ops.OpsTekstikappaleLockService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/opetussuunnitelmat/{opsId}/tekstit/{viiteId}/lukko")
@Hidden
@Tag(name = "OpsTekstikappaleLukot")
public class OpsTekstikappaleLockController extends AbstractLockController<OpsTekstikappaleCtx> {
    @Autowired
    private OpsTekstikappaleLockService service;

    @Override
    protected LockService<OpsTekstikappaleCtx> service() {
        return service;
    }

}
