package fi.vm.sade.eperusteet.ylops.resource.ops;

import fi.vm.sade.eperusteet.ylops.resource.util.AbstractLockController;
import fi.vm.sade.eperusteet.ylops.service.locking.LockService;
import fi.vm.sade.eperusteet.ylops.service.ops.OppiaineService;
import fi.vm.sade.eperusteet.ylops.service.ops.OpsOppiaineCtx;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@RequestMapping(value = {
        "/opetussuunnitelmat/{opsId}/oppiaineet/{oppiaineId}/lukko",
        "/opetussuunnitelmat/{opsId}/oppiaineet/{oppiaineId}/vuosiluokkakokonaisuudet/{kokonaisuusId}/lukko",
        "/opetussuunnitelmat/{opsId}/oppiaineet/{oppiaineId}/vuosiluokkakokonaisuudet/{kokonaisuusId}/vuosiluokat/{vuosiluokkaId}/lukko"
})
@ApiIgnore
@Api(value = "OppiaieenLukot")
public class OppiaineLockController extends AbstractLockController<OpsOppiaineCtx> {
    @Autowired
    private OppiaineService service;

    @Override
    protected LockService<OpsOppiaineCtx> service() {
        return service;
    }

}
