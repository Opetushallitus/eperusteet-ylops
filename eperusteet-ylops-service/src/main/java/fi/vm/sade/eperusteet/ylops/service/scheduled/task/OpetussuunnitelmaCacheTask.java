package fi.vm.sade.eperusteet.ylops.service.scheduled.task;

import fi.vm.sade.eperusteet.ylops.dto.util.CacheArvot;
import fi.vm.sade.eperusteet.ylops.service.util.MaintenanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class OpetussuunnitelmaCacheTask extends AbstractScheduledTask{

    @Autowired
    private MaintenanceService maintenanceService;

    @Override
    public int getPriority() {
        return 10;
    }

    @Override
    public void executeTask(Date viimeisinajoaika) {
        maintenanceService.clearCache(CacheArvot.OPETUSSUUNNITELMA_NAVIGAATIO_JULKINEN);
        maintenanceService.cacheOpetussuunnitelmaNavigaatiot();
    }
}
