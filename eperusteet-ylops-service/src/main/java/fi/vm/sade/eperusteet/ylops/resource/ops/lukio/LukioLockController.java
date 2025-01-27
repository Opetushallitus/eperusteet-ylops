package fi.vm.sade.eperusteet.ylops.resource.ops.lukio;

import fi.vm.sade.eperusteet.ylops.resource.util.AbstractLockController;
import fi.vm.sade.eperusteet.ylops.service.locking.LockService;
import fi.vm.sade.eperusteet.ylops.service.ops.lukio.LukioLockCtx;
import fi.vm.sade.eperusteet.ylops.service.ops.lukio.LukioLockService;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/opetussuunnitelmat/lukio/{opsId}/lukko")
@Hidden
public class LukioLockController extends AbstractLockController<LukioLockCtx> {
    @Autowired
    private LukioLockService lockService;

    @Override
    protected LockService<LukioLockCtx> service() {
        return lockService;
    }
}
