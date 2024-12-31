package fi.vm.sade.eperusteet.ylops.resource.ops;

import fi.vm.sade.eperusteet.ylops.resource.util.AbstractLockController;
import fi.vm.sade.eperusteet.ylops.service.locking.LockService;
import fi.vm.sade.eperusteet.ylops.service.ops.OppiaineService;
import fi.vm.sade.eperusteet.ylops.service.ops.OpsOppiaineCtx;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = {
        "/api/opetussuunnitelmat/{opsId}/oppiaineet/{oppiaineId}/lukko",
        "/api/opetussuunnitelmat/{opsId}/oppiaineet/{oppiaineId}/vuosiluokkakokonaisuudet/{kokonaisuusId}/lukko",
        "/api/opetussuunnitelmat/{opsId}/oppiaineet/{oppiaineId}/vuosiluokkakokonaisuudet/{kokonaisuusId}/vuosiluokat/{vuosiluokkaId}/lukko"
})
@Hidden
@Tag(name = "OppiaieenLukot")
public class OppiaineLockController extends AbstractLockController<OpsOppiaineCtx> {
    @Autowired
    private OppiaineService service;

    @Override
    protected LockService<OpsOppiaineCtx> service() {
        return service;
    }

}
