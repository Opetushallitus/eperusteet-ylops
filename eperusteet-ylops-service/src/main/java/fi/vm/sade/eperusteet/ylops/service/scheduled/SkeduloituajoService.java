package fi.vm.sade.eperusteet.ylops.service.scheduled;

import java.util.Date;

import fi.vm.sade.eperusteet.ylops.domain.SkeduloituAjo;
import fi.vm.sade.eperusteet.ylops.domain.SkeduloituAjoStatus;
import fi.vm.sade.eperusteet.ylops.repository.SkeduloituajoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class SkeduloituajoService {

    @Autowired
    private SkeduloituajoRepository skeduloituajoRepository;

    public SkeduloituAjo haeTaiLisaaAjo(String nimi) {
        SkeduloituAjo skeduloituajo = skeduloituajoRepository.findByNimi(nimi);
        if (skeduloituajo == null) {
            skeduloituajo = lisaaUusiAjo(nimi);
        }

        return skeduloituajo;
    }

    public SkeduloituAjo lisaaUusiAjo(String nimi) {
        return skeduloituajoRepository.save(SkeduloituAjo.builder()
                .nimi(nimi)
                .status(SkeduloituAjoStatus.PYSAYTETTY)
                .build());
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public SkeduloituAjo paivitaAjoStatus(SkeduloituAjo skeduloituAjo, SkeduloituAjoStatus status) {
        skeduloituAjo.setStatus(status);
        return skeduloituajoRepository.save(skeduloituAjo);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public SkeduloituAjo pysaytaAjo(SkeduloituAjo skeduloituAjo) {
        skeduloituAjo.setStatus(SkeduloituAjoStatus.PYSAYTETTY);
        skeduloituAjo.setViimeisinAjoLopetus(new Date());
        return skeduloituajoRepository.save(skeduloituAjo);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public SkeduloituAjo kaynnistaAjo(SkeduloituAjo skeduloituAjo) {
        skeduloituAjo.setStatus(SkeduloituAjoStatus.AJOSSA);
        skeduloituAjo.setViimeisinAjoKaynnistys(new Date());
        return skeduloituajoRepository.save(skeduloituAjo);
    }

}
