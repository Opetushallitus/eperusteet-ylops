package fi.vm.sade.eperusteet.ylops.service.ops.impl;

import com.google.common.collect.Sets;
import fi.vm.sade.eperusteet.ylops.domain.KoulutustyyppiToteutus;
import fi.vm.sade.eperusteet.ylops.domain.ops.Opetussuunnitelma;
import fi.vm.sade.eperusteet.ylops.domain.teksti.Omistussuhde;
import fi.vm.sade.eperusteet.ylops.domain.teksti.TekstiKappale;
import fi.vm.sade.eperusteet.ylops.domain.teksti.TekstiKappaleViite;
import fi.vm.sade.eperusteet.ylops.repository.ops.OpetussuunnitelmaRepository;
import fi.vm.sade.eperusteet.ylops.repository.teksti.TekstiKappaleRepository;
import fi.vm.sade.eperusteet.ylops.repository.teksti.TekstikappaleviiteRepository;
import fi.vm.sade.eperusteet.ylops.service.exception.BusinessRuleViolationException;
import fi.vm.sade.eperusteet.ylops.service.ops.OpetussuunnitelmaService;
import fi.vm.sade.eperusteet.ylops.service.ops.OpsPohjanVaihto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Component
@Transactional
public class OpsPohjanVaihtoLops2019Impl implements OpsPohjanVaihto {
    @Autowired
    private OpetussuunnitelmaService opetussuunnitelmaService;

    @Autowired
    private TekstikappaleviiteRepository tekstikappaleviiteRepository;

    @Autowired
    private TekstiKappaleRepository tekstiKappaleRepository;

    @Autowired
    private OpetussuunnitelmaRepository opetussuunnitelmaRepository;

    private void kopioiHierarkia(TekstiKappaleViite vanha,
                                 TekstiKappaleViite parent,
                                 Map<Long, TekstiKappaleViite> omat,
                                 Map<Long, TekstiKappaleViite> perusteen) {
        List<TekstiKappaleViite> vanhaLapset = vanha.getLapset();
        if (vanhaLapset != null) {
            vanhaLapset.stream()
                .filter(original -> original.getTekstiKappale() != null)
                .forEach(original -> {
                    TekstiKappaleViite tkv = tekstikappaleviiteRepository.save(TekstiKappaleViite.copy(original));
                    tkv.setVanhempi(parent);
                    tkv.setTekstiKappale(tekstiKappaleRepository.save(tkv.getTekstiKappale()));
                    parent.getLapset().add(tkv);
                    kopioiHierarkia(original, tkv, omat, perusteen);


                    if (tkv.getPerusteTekstikappaleId() != null) {
                        // Perusteen tekstin paikallinen tarkennus
                        if (perusteen.containsKey(tkv.getPerusteTekstikappaleId())) {
                            TekstiKappaleViite viite = perusteen.get(tkv.getPerusteTekstikappaleId());
                            if (viite.getTekstiKappale() != null) {
                                tkv.getTekstiKappale().setTeksti(viite.getTekstiKappale().getTeksti());
                            }
                        }

                        // Omat alikappaleet
                        if (omat.containsKey(tkv.getPerusteTekstikappaleId())) {
                            TekstiKappaleViite oma = tekstikappaleviiteRepository.save(TekstiKappaleViite.copy(original));
                            oma.setVanhempi(tkv);
                            oma.setLapset(new ArrayList<>());
                            oma.setTekstiKappale(tekstiKappaleRepository.save(oma.getTekstiKappale()));
                            tkv.getLapset().add(oma);
                            omat.remove(tkv.getPerusteTekstikappaleId());
                        }
                    }
                });
        }
    }

    private Long findPerusteenTekstiId(TekstiKappaleViite viite) {
        TekstiKappaleViite vanhempi = viite.getVanhempi();
        while (vanhempi != null) {
            if (vanhempi.getPerusteTekstikappaleId() != null) {
                return vanhempi.getPerusteTekstikappaleId();
            }
            vanhempi = vanhempi.getVanhempi();
        }
        return null;
    }

    private void collectTekstit(TekstiKappaleViite viite,
                                Map<Long, TekstiKappaleViite> omat,
                                Map<Long, TekstiKappaleViite> perusteen) {
        if (viite.getTekstiKappale() != null) {
            if (viite.getPerusteTekstikappaleId() == null) {
                Long perusteenTekstiId = findPerusteenTekstiId(viite);
                omat.put(perusteenTekstiId, viite);
            }
            else {
                perusteen.put(viite.getPerusteTekstikappaleId(), viite);
            }
        }
        for (TekstiKappaleViite lapsi : viite.getLapset()) {
            collectTekstit(lapsi, omat, perusteen);
        }
    }

    @Override
    public void vaihdaPohja(Long opsId, Long pohjaId) {
        Opetussuunnitelma ops = opetussuunnitelmaRepository.getOne(opsId);
        Opetussuunnitelma vanha = opetussuunnitelmaRepository.getOne(ops.getPohja().getId());
        Opetussuunnitelma uusi = opetussuunnitelmaRepository.getOne(pohjaId);
        if (pohjaId.equals(ops.getPohja().getId()) || pohjaId.equals(opsId)) {
            throw new BusinessRuleViolationException("virheellinen-pohja");
        }

        if (!vanha.getCachedPeruste().getPerusteId().equals(uusi.getCachedPeruste().getPerusteId())) {
            throw new BusinessRuleViolationException("pohja-vaihdettavissa-vain-samaan-perusteeseen");
        }

        TekstiKappaleViite tekstit = ops.getTekstit();
        Map<Long, TekstiKappaleViite> omat = new HashMap<>();
        Map<Long, TekstiKappaleViite> perusteen = new HashMap<>();
        collectTekstit(tekstit, omat, perusteen);

        TekstiKappaleViite uusiHierarkia = uusi.getTekstit();
        ops.setTekstit(tekstikappaleviiteRepository.save(new TekstiKappaleViite()));
        kopioiHierarkia(uusiHierarkia, ops.getTekstit(), omat, perusteen);
        for (TekstiKappaleViite oma : omat.values()) {
            if (uusiHierarkia.getLapset().size() > 0) {
                oma.setVanhempi(uusiHierarkia.getLapset().get(0));
                oma.setLapset(new ArrayList<>());
                uusiHierarkia.getLapset().add(oma);
            }
        }
        ops.setPohja(uusi);
    }

    @Override
    public Set<KoulutustyyppiToteutus> getTyypit() {
        return Sets.newHashSet(KoulutustyyppiToteutus.LOPS2019);
    }
}
