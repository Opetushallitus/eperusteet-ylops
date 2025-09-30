package fi.vm.sade.eperusteet.ylops.service.scheduled.task;

import fi.vm.sade.eperusteet.ylops.domain.SkeduloituAjo;
import fi.vm.sade.eperusteet.ylops.domain.SkeduloituAjoStatus;
import fi.vm.sade.eperusteet.ylops.service.exception.ServiceException;
import fi.vm.sade.eperusteet.ylops.service.scheduled.SkeduloituajoService;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;

import java.util.Date;

public abstract class AbstractScheduledTask implements ScheduledTask {

    @Autowired
    private SkeduloituajoService skeduloituajoService;

    public abstract void executeTask(Date viimeisinajoaika);

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public void execute() {
        SkeduloituAjo skeduloituajo = skeduloituajoService.haeTaiLisaaAjo(getName());

        if (!skeduloituajo.isKaytossa()) {
            return;
        }

        if (skeduloituajo.getStatus().equals(SkeduloituAjoStatus.AJOSSA) && skeduloituajo.getViimeisinAjoKaynnistys().compareTo(DateTime.now().minusHours(6).toDate()) > 0) {
            throw new ServiceException("ajo-kaynnissa");
        }

        try {
            skeduloituajoService.kaynnistaAjo(skeduloituajo);
            executeTask(skeduloituajo.getViimeisinAjoLopetus());
            skeduloituajoService.pysaytaAjo(skeduloituajo);
        } catch (Exception e) {
            skeduloituajoService.paivitaAjoStatus(skeduloituajo, SkeduloituAjoStatus.AJOVIRHE);
            throw e;
        }
    }

    @Async
    
    @Override
    public void executeAsync() {
        execute();
    }
}
