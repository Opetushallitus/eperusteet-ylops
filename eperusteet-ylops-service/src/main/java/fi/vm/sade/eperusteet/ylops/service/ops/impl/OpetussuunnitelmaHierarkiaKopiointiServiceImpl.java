package fi.vm.sade.eperusteet.ylops.service.ops.impl;

import fi.vm.sade.eperusteet.ylops.domain.ops.Opetussuunnitelma;
import fi.vm.sade.eperusteet.ylops.domain.teksti.Omistussuhde;
import fi.vm.sade.eperusteet.ylops.domain.teksti.TekstiKappale;
import fi.vm.sade.eperusteet.ylops.domain.teksti.TekstiKappaleViite;
import fi.vm.sade.eperusteet.ylops.repository.ops.OpetussuunnitelmaRepository;
import fi.vm.sade.eperusteet.ylops.repository.teksti.TekstiKappaleRepository;
import fi.vm.sade.eperusteet.ylops.repository.teksti.TekstikappaleviiteRepository;
import fi.vm.sade.eperusteet.ylops.service.ops.OpetussuunnitelmaHierarkiaKopiointiService;
import fi.vm.sade.eperusteet.ylops.service.util.CollectionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class OpetussuunnitelmaHierarkiaKopiointiServiceImpl implements OpetussuunnitelmaHierarkiaKopiointiService {

    @Autowired
    private TekstikappaleviiteRepository tekstikappaleviiteRepository;

    @Autowired
    private TekstiKappaleRepository tekstiKappaleRepository;

    @Autowired
    private OpetussuunnitelmaRepository opetussuunnitelmaRepository;

    @Override
    public void kopioiPohjanRakenne(Opetussuunnitelma ops, Opetussuunnitelma pohja) {
        Map<Long, List<TekstiKappaleViite>> omat = new HashMap<>();
        Map<Long, TekstiKappaleViite> perusteen = new HashMap<>();
        List<TekstiKappaleViite> perusteettomat = new ArrayList<>();
        Map<UUID, TekstiKappale> uuidOpsTekstikappaleMap = CollectionUtil.treeToStream(ops.getTekstit(), TekstiKappaleViite::getLapset)
                .filter(tkv -> tkv.getTekstiKappale() != null)
                .collect(Collectors.toMap(tkv -> tkv.getTekstiKappale().getTunniste(), TekstiKappaleViite::getTekstiKappale, (o1, o2) -> o1));
        Set<UUID> pohjanTekstikappaleTunnisteet = CollectionUtil.treeToStream(pohja.getTekstit(), TekstiKappaleViite::getLapset)
                .filter(tkv -> tkv.getTekstiKappale() != null)
                .map(tkv -> tkv.getTekstiKappale().getTunniste()).collect(Collectors.toSet());
        collectTekstit(ops.getTekstit(), omat, perusteen, perusteettomat, pohjanTekstikappaleTunnisteet);

        ops.setTekstit(tekstikappaleviiteRepository.save(new TekstiKappaleViite()));
        kopioiHierarkia(pohja.getTekstit(), ops.getTekstit(), omat, perusteen, uuidOpsTekstikappaleMap);

        perusteettomat.addAll(omat.values().stream().flatMap(x -> x.stream()).collect(Collectors.toList()));
        for (TekstiKappaleViite vanhaOma : perusteettomat) {
            TekstiKappaleViite oma = tekstiKappaleViiteRec(vanhaOma);
            oma.setVanhempi(ops.getTekstit());
            ops.getTekstit().getLapset().add(oma);
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
                                Map<Long, List<TekstiKappaleViite>> omat,
                                Map<Long, TekstiKappaleViite> perusteen,
                                List<TekstiKappaleViite> perusteettomat,
                                Set<UUID> pohjanTekstikappaleTunnisteet) {
        if (viite.getTekstiKappale() != null) {
            if (viite.getPerusteTekstikappaleId() == null && !pohjanTekstikappaleTunnisteet.contains(viite.getTekstiKappale().getTunniste())) {
                Long perusteenTekstiId = findPerusteenTekstiId(viite);
                if (perusteenTekstiId != null) {
                    omat.computeIfAbsent(perusteenTekstiId, k -> new ArrayList<>());
                    omat.get(perusteenTekstiId).add(viite);
                } else {
                    perusteettomat.add(viite);
                }

                return;
            } else {
                perusteen.put(viite.getPerusteTekstikappaleId(), viite);
            }
        }

        for (TekstiKappaleViite lapsi : viite.getLapset()) {
            collectTekstit(lapsi, omat, perusteen, perusteettomat, pohjanTekstikappaleTunnisteet);
        }
    }

    private void kopioiHierarkia(TekstiKappaleViite pohjanViite,
                                 TekstiKappaleViite opsViite,
                                 Map<Long, List<TekstiKappaleViite>> omat,
                                 Map<Long, TekstiKappaleViite> perusteen,
                                 Map<UUID, TekstiKappale> uuidOpsTekstikappaleMap) {
        List<TekstiKappaleViite> pohjanLapset = pohjanViite.getLapset();
        if (pohjanLapset != null) {
            pohjanLapset.stream()
                    .filter(pohjanTekstikappaleViite -> pohjanTekstikappaleViite.getTekstiKappale() != null)
                    .forEach(pohjanTekstikappaleViite -> {
                        TekstiKappaleViite tkv = tekstikappaleviiteRepository.save(TekstiKappaleViite.copy(pohjanTekstikappaleViite));
                        tkv.setVanhempi(opsViite);
                        tkv.setOmistussuhde(pohjanTekstikappaleViite.getOmistussuhde());
                        tkv.setTekstiKappale(tekstiKappaleRepository.save(tkv.getTekstiKappale()));
                        opsViite.getLapset().add(tkv);
                        kopioiHierarkia(pohjanTekstikappaleViite, tkv, omat, perusteen, uuidOpsTekstikappaleMap);

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
                                omat.get(tkv.getPerusteTekstikappaleId()).forEach(vanhaOma -> {
                                    TekstiKappaleViite oma = tekstiKappaleViiteRec(vanhaOma);
                                    oma.setVanhempi(tkv);
                                    tkv.getLapset().add(oma);
                                });
                                omat.remove(tkv.getPerusteTekstikappaleId());
                            }
                        } else if (uuidOpsTekstikappaleMap.containsKey(tkv.getTekstiKappale().getTunniste())) {
                            tkv.setTekstiKappale(tekstiKappaleRepository.save(uuidOpsTekstikappaleMap.get(tkv.getTekstiKappale().getTunniste()).copy()));
                        }
                    });
        }
    }

    private TekstiKappaleViite tekstiKappaleViiteRec(TekstiKappaleViite vanhaOma) {
        TekstiKappaleViite oma = tekstikappaleviiteRepository.save(TekstiKappaleViite.copy(vanhaOma));
        oma.setOmistussuhde(Omistussuhde.OMA);
        oma.setLapset(new ArrayList<>());
        oma.getLapset().addAll(vanhaOma.getLapset().stream().map(vanhaLapsi -> tekstiKappaleViiteRec(vanhaLapsi)).collect(Collectors.toList()));
        oma.getLapset().forEach(lapsi -> lapsi.setVanhempi(oma));
        oma.setTekstiKappale(tekstiKappaleRepository.save(vanhaOma.getTekstiKappale()));
        return oma;
    }

}
