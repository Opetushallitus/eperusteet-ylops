package fi.vm.sade.eperusteet.ylops.service.ops.impl;

import com.google.common.collect.Sets;
import fi.vm.sade.eperusteet.ylops.domain.KoulutustyyppiToteutus;
import fi.vm.sade.eperusteet.ylops.domain.ops.Opetussuunnitelma;
import fi.vm.sade.eperusteet.ylops.domain.teksti.Omistussuhde;
import fi.vm.sade.eperusteet.ylops.domain.teksti.TekstiKappaleViite;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpetussuunnitelmaBaseDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpetussuunnitelmaInfoDto;
import fi.vm.sade.eperusteet.ylops.repository.ops.OpetussuunnitelmaRepository;
import fi.vm.sade.eperusteet.ylops.repository.teksti.TekstiKappaleRepository;
import fi.vm.sade.eperusteet.ylops.repository.teksti.TekstikappaleviiteRepository;
import fi.vm.sade.eperusteet.ylops.service.exception.BusinessRuleViolationException;
import fi.vm.sade.eperusteet.ylops.service.mapping.DtoMapper;
import fi.vm.sade.eperusteet.ylops.service.ops.OpetussuunnitelmaService;
import fi.vm.sade.eperusteet.ylops.service.ops.OpsPohjanVaihto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

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
    private DtoMapper mapper;

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
                    tkv.setOmistussuhde(original.getOmistussuhde());
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
                            TekstiKappaleViite vanhaOma = omat.get(tkv.getPerusteTekstikappaleId());
                            TekstiKappaleViite oma = tekstikappaleviiteRepository.save(TekstiKappaleViite.copy(vanhaOma));
                            oma.setOmistussuhde(Omistussuhde.OMA);
                            oma.setLapset(new ArrayList<>());
                            oma.updateOriginal(null);
                            oma.setTekstiKappale(tekstiKappaleRepository.save(vanhaOma.getTekstiKappale()));
                            oma.setVanhempi(tkv);
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
            if (viite.getPerusteTekstikappaleId() == null && viite.getOriginal() == null) {
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

        Set<String> a = ops.getOrganisaatiot();
        Set<String> b = uusi.getOrganisaatiot();
        if (!a.containsAll(b)) {
            throw new BusinessRuleViolationException("uuden-pohjan-organisaatiot-vaarat");
        }

        TekstiKappaleViite tekstit = ops.getTekstit();
        Map<Long, TekstiKappaleViite> omat = new HashMap<>();
        Map<Long, TekstiKappaleViite> perusteen = new HashMap<>();
        collectTekstit(tekstit, omat, perusteen);

        TekstiKappaleViite uusiHierarkia = uusi.getTekstit();
        ops.setTekstit(tekstikappaleviiteRepository.save(new TekstiKappaleViite()));
        kopioiHierarkia(uusiHierarkia, ops.getTekstit(), omat, perusteen);
        for (TekstiKappaleViite oma : omat.values()) {
                oma.setVanhempi(uusiHierarkia);
                oma.setLapset(new ArrayList<>());
                uusiHierarkia.getLapset().add(oma);
        }
        ops.setPohja(uusi);
    }

    @Override
    public Set<KoulutustyyppiToteutus> getTyypit() {
        return Sets.newHashSet(KoulutustyyppiToteutus.LOPS2019);
    }

    @Override
    public Set<OpetussuunnitelmaInfoDto> haeVaihtoehdot(Long opsId) {
        Opetussuunnitelma ops = opetussuunnitelmaRepository.getOne(opsId);
        Long perusteId = ops.getCachedPeruste().getPerusteId();
        Set<OpetussuunnitelmaInfoDto> pohjat = opetussuunnitelmaService.getOpetussuunnitelmaOpsPohjat().stream()
                .filter(pohja -> Objects.equals(pohja.getPerusteenId(), perusteId))
                .filter(pohja -> !Objects.equals(pohja.getId(), ops.getPohja().getId()))
                .filter(pohja -> !Objects.equals(pohja.getId(), opsId))
                .collect(Collectors.toSet());
        return pohjat;
    }
}
