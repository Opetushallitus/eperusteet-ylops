package fi.vm.sade.eperusteet.ylops.service.ops.impl;

import fi.vm.sade.eperusteet.utils.dto.peruste.lops2019.tutkinnonrakenne.KoodiDto;
import fi.vm.sade.eperusteet.ylops.domain.KoulutustyyppiToteutus;
import fi.vm.sade.eperusteet.ylops.domain.Tila;
import fi.vm.sade.eperusteet.ylops.domain.Tyyppi;
import fi.vm.sade.eperusteet.ylops.domain.ValidationCategory;
import fi.vm.sade.eperusteet.ylops.domain.cache.PerusteCache;
import fi.vm.sade.eperusteet.ylops.domain.oppiaine.Oppiaine;
import fi.vm.sade.eperusteet.ylops.domain.ops.Opetussuunnitelma;
import fi.vm.sade.eperusteet.ylops.domain.ops.OpsOppiaine;
import fi.vm.sade.eperusteet.ylops.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.ylops.domain.teksti.LokalisoituTeksti;
import fi.vm.sade.eperusteet.ylops.domain.teksti.TekstiKappale;
import fi.vm.sade.eperusteet.ylops.domain.teksti.TekstiKappaleViite;
import fi.vm.sade.eperusteet.ylops.dto.Reference;
import fi.vm.sade.eperusteet.ylops.dto.koodisto.KoodistoKoodiDto;
import fi.vm.sade.eperusteet.ylops.dto.lops2019.Lops2019OpintojaksoBaseDto;
import fi.vm.sade.eperusteet.ylops.dto.lops2019.Lops2019OpintojaksoDto;
import fi.vm.sade.eperusteet.ylops.dto.lops2019.Lops2019OpintojaksonModuuliDto;
import fi.vm.sade.eperusteet.ylops.dto.lops2019.Lops2019OpintojaksonOppiaineDto;
import fi.vm.sade.eperusteet.ylops.dto.navigation.NavigationNodeDto;
import fi.vm.sade.eperusteet.ylops.dto.navigation.NavigationType;
import fi.vm.sade.eperusteet.ylops.dto.peruste.PerusteDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.lops2019.oppiaineet.Lops2019OppiaineKaikkiDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.lops2019.oppiaineet.moduuli.Lops2019ModuuliDto;
import fi.vm.sade.eperusteet.ylops.dto.teksti.LokalisoituTekstiDto;
import fi.vm.sade.eperusteet.ylops.repository.lops2019.Lops2019OpintojaksoRepository;
import fi.vm.sade.eperusteet.ylops.repository.lops2019.Lops2019OppiaineRepository;
import fi.vm.sade.eperusteet.ylops.repository.ops.JulkaisuRepository;
import fi.vm.sade.eperusteet.ylops.repository.ops.OpetussuunnitelmaRepository;
import fi.vm.sade.eperusteet.ylops.service.exception.BusinessRuleViolationException;
import fi.vm.sade.eperusteet.ylops.service.external.EperusteetService;
import fi.vm.sade.eperusteet.ylops.service.external.KoodistoService;
import fi.vm.sade.eperusteet.ylops.service.lops2019.Lops2019OpintojaksoService;
import fi.vm.sade.eperusteet.ylops.service.lops2019.Lops2019OppiaineService;
import fi.vm.sade.eperusteet.ylops.service.lops2019.Lops2019Service;
import fi.vm.sade.eperusteet.ylops.service.mapping.DtoMapper;
import fi.vm.sade.eperusteet.ylops.service.ops.ValidointiService;
import fi.vm.sade.eperusteet.ylops.service.util.KoodiValidator;
import fi.vm.sade.eperusteet.ylops.service.util.Validointi;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static fi.vm.sade.eperusteet.ylops.service.util.Nulls.assertExists;

@Service
@Transactional
@Slf4j
public class ValidointiServiceImpl implements ValidointiService {

    @Autowired
    private OpetussuunnitelmaRepository opetussuunnitelmaRepository;

    @Autowired
    private Lops2019OppiaineService oppiaineService;

    @Autowired
    private EperusteetService eperusteetService;

    @Autowired
    private Lops2019OppiaineRepository oppiaineRepository;

    @Autowired
    private Lops2019OpintojaksoService opintojaksoService;

    @Autowired
    private Lops2019Service lops2019Service;

    @Autowired
    private Lops2019OpintojaksoRepository opintojaksoRepository;

    @Autowired
    private DtoMapper mapper;

    @Autowired
    private KoodistoService koodistoService;

    @Autowired
    private JulkaisuRepository julkaisuRepository;

    private Opetussuunnitelma getOpetussuunnitelma(Long opsId) {
        Opetussuunnitelma ops = opetussuunnitelmaRepository.findOne(opsId);
        if (ops == null) {
            throw new BusinessRuleViolationException("ops-ei-loydy");
        }
        return ops;
    }

    private PerusteDto getPerusteImpl(Long opsId) {
        Opetussuunnitelma ops = getOpetussuunnitelma(opsId);
        PerusteCache perusteCached = ops.getCachedPeruste();
        return eperusteetService.getPerusteById(perusteCached.getId());
    }

    @Override
    public List<Validointi> validoiOpetussuunnitelma(Long id) {
        Opetussuunnitelma ops = opetussuunnitelmaRepository.findOne(id);
        assertExists(ops, "Opetussuunnitelmaa ei ole olemassa");

        List<Validointi> result = new ArrayList<>();

        if (ops.getTyyppi().equals(Tyyppi.OPS)) {
            result.addAll(validoiOpetussuunnitelma(ops));

            if (ops.getToteutus().equals(KoulutustyyppiToteutus.LOPS2019)) {
                result.addAll(validoiLukio2019(id));
            }
        }

        if (ops.getTyyppi().equals(Tyyppi.POHJA)) {
            result.add(validoiPohja(ops));
        }

        return result.stream()
                .filter(validointi -> validointi.getKategoria() != null)
                .filter(validointi -> CollectionUtils.isNotEmpty(validointi.getVirheet()) || CollectionUtils.isNotEmpty(validointi.getHuomiot()) || CollectionUtils.isNotEmpty(validointi.getHuomautukset()))
                .collect(Collectors.toList());
    }

    private List<Validointi> validoiOpetussuunnitelma(Opetussuunnitelma ops) {
        Validointi validointi = new Validointi(ValidationCategory.OPETUSSUUNNITELMA);

        Set<Kieli> julkaisukielet = ops.getJulkaisukielet();

        NavigationNodeDto navigationNodeDto = NavigationNodeDto.of(NavigationType.tiedot);

        if (CollectionUtils.isEmpty(julkaisukielet)) {
            validointi.virhe("vahintaan-yksi-julkaisukieli", navigationNodeDto);
        }

        validateOpetussuunnitelmaTiedot(ops, validointi);
        validoiPaikallisetOpintojaksot(ops, validointi);
        validateTextHierarchy(ops, julkaisukielet, validointi);

        if (ObjectUtils.isEmpty(ops.getPerusteenDiaarinumero())) {
            validointi.virhe("perusteen-diaarinumero-puuttuu", navigationNodeDto);
        }

        if (ops.getNimi() == null || !ops.getNimi().hasKielet(julkaisukielet)) {
            validointi.virhe("nimi-oltava-kaikilla-julkaisukielilla", navigationNodeDto);
        }

        if (ops.getKuvaus() == null || !ops.getKuvaus().hasKielet(julkaisukielet)) {
            validointi.addHuomautus(new Validointi.Virhe("kuvausta-ei-ole-kirjoitettu-kaikilla-julkaisukielilla", navigationNodeDto));
        }

        if (ObjectUtils.isEmpty(ops.getHyvaksyjataho())) {
            validointi.virhe("hyvaksyjataho-puuttuu", navigationNodeDto);
        }

        if (ObjectUtils.isEmpty(ops.getPaatospaivamaara())) {
            validointi.virhe("paatospaivamaaraa-ei-ole-asetettu", navigationNodeDto);
        }

        //TODO Should we use same version of Peruste for with the Opetuusuunnitelma was based on if available?
        PerusteDto peruste = eperusteetService.getPeruste(ops.getPerusteenDiaarinumero());
        if (peruste.getPerusopetus() != null) {
            ops.getOppiaineet().stream()
                    .filter(OpsOppiaine::isOma)
                    .map(OpsOppiaine::getOppiaine)
                    .forEach(oa -> peruste.getPerusopetus().getOppiaine(oa.getTunniste()).ifPresent(poppiaine -> {
                        validoiOppiaine(validointi, oa, julkaisukielet);
                    }));
        }

        return Collections.singletonList(validointi);
    }

    private void validoiOppiaine(Validointi validointi, Oppiaine oa, Set<Kieli> kielet) {
        validoiLokalisoituTeksti(validointi, kielet, oa.getNimi(), NavigationNodeDto.of(NavigationType.oppiaine, new LokalisoituTekstiDto(null, oa.getNimi().getTeksti()), oa.getId()));

        if (oa.getOppimaarat() != null) {
            for (Oppiaine om : oa.getOppimaarat()) {
                validoiOppiaine(validointi, om, kielet);
            }
        }
    }

    private void validoiPaikallisetOpintojaksot(Opetussuunnitelma ops, Validointi validointi) {
        if(KoulutustyyppiToteutus.LOPS2019.equals(ops.getToteutus())) {
            validointi.addAll(tarkistaOpintojaksot(ops.getId()));
        }
    }

    private void validateOpetussuunnitelmaTiedot(Opetussuunnitelma ops, Validointi validointi) {
        if (ops.getPerusteenDiaarinumero().isEmpty()) {
            validointi.virhe("opsilla-ei-perusteen-diaarinumeroa", NavigationNodeDto.of(NavigationType.tiedot));
        }
    }

    private void validateTextHierarchy(Opetussuunnitelma ops, Set<Kieli> julkaisukielet, Validointi validointi) {
        if (ops.getTekstit() != null && ops.getTekstit().getLapset() != null) {
            validoiTekstikappaleViite(validointi, ops.getTekstit().getLapset(), julkaisukielet);
        }
    }

    private void validoiTekstikappaleViite(Validointi validointi, List<TekstiKappaleViite> viitteet, Set<Kieli> julkaisukielet) {
        if (viitteet == null || viitteet.isEmpty()) {
            return;
        }

        for (TekstiKappaleViite lapsi : viitteet) {
            Optional<LokalisoituTekstiDto> tekstiKappaleNimi = Optional.ofNullable(lapsi)
                    .map(TekstiKappaleViite::getTekstiKappale)
                    .map(TekstiKappale::getNimi)
                    .map(LokalisoituTeksti::getTeksti)
                    .map(teksti -> new LokalisoituTekstiDto(null, teksti));
            NavigationNodeDto navigationNodeDto = NavigationNodeDto.builder()
                    .type(NavigationType.viite)
                    .label(tekstiKappaleNimi.orElse(null))
                    .id(lapsi.getId())
                    .build();

            if (lapsi.getTekstiKappale() != null) {
                validoiLokalisoituTeksti(validointi, julkaisukielet, lapsi.getTekstiKappale().getNimi(), navigationNodeDto);
                if (lapsi.getTekstiKappale().getTeksti() != null) {
                    validoiLokalisoituTeksti(validointi, julkaisukielet, lapsi.getTekstiKappale().getTeksti(), navigationNodeDto);
                }
            } else {
                validointi.virhe("tekstikappaleella-ei-lainkaan-sisaltoa", navigationNodeDto);
            }

            validoiTekstikappaleViite(validointi, lapsi.getLapset(), julkaisukielet);
        }
    }

    private void validoiLokalisoituTeksti(Validointi validointi, Set<Kieli> kielet, LokalisoituTeksti teksti, NavigationNodeDto navigationNodeDto) {
        validoiLokalisoituTeksti("kielisisaltoa-ei-loytynyt-opsin-kielilla", validointi, kielet, teksti, navigationNodeDto);
    }

    private void validoiLokalisoituTeksti(String kuvaus, Validointi validointi, Set<Kieli> kielet, LokalisoituTeksti teksti, NavigationNodeDto navigationNodeDto) {
        if (teksti == null || !teksti.hasKielet(kielet)) {
            validointi.virhe(kuvaus, navigationNodeDto);
        }
    }

    private Validointi validoiPohja(Opetussuunnitelma ops) {
        return new Validointi();
    }

    @Override
    public List<Validointi> validoiLukio2019(Long opsId) {
        Opetussuunnitelma ops = opetussuunnitelmaRepository.findOne(opsId);
        if (ops == null) {
            throw new BusinessRuleViolationException("opetussuunnitelmaa-ei-ole");
        }

        List<Validointi> validoinnit = new ArrayList<>();

        List<Lops2019OpintojaksoDto> opintojaksot = opintojaksoService.getAll(opsId, Lops2019OpintojaksoDto.class);
        List<Lops2019ModuuliDto> moduulit = haeValidoitavatModuulit(opsId);
        Map<String, Lops2019ModuuliDto> moduulitMap = moduulit.stream().collect(Collectors.toMap(m -> m.getKoodi().getUri(), Function.identity()));
        Map<String, List<Lops2019OpintojaksoDto>> liitokset = lops2019Service.getModuuliToOpintojaksoMap(opintojaksot);

        if (!ops.isAinepainoitteinen()) {
            Validointi validointi = new Validointi(ValidationCategory.MODUULI);

            moduulit.forEach(moduuli -> {
                List<Lops2019OpintojaksoDto> moduulinOpintojaksot = liitokset.getOrDefault(
                        moduuli.getKoodi().getUri(),
                        new ArrayList<>());

                // - Moduuli vähintään yhdessä opintojaksossa
                validoiVirhe(validointi, "moduuli-kuuluttava-vahintaan-yhteen-opintojaksoon", moduuliNavigationNode(moduuli), moduulinOpintojaksot.isEmpty());

                // - Pakollinen moduuli vähintään yhdessä opintojaksossa missä on vain muita saman oppiaineen pakollisia
                validoiHuomautus(validointi, "pakollinen-moduuli-mahdollista-suorittaa-erillaan", moduuliNavigationNode(moduuli), moduulinOpintojaksot.stream()
                        .anyMatch(oj -> oj.getOppiaineet().size() == 1
                                && oj.getModuulit().stream().anyMatch(ojm -> !moduulitMap.get(ojm.getKoodiUri()).isPakollinen())
                                && oj.getModuulit().stream().anyMatch(ojm -> moduulitMap.get(ojm.getKoodiUri()).isPakollinen())
                        ));
            });

            validoinnit.add(validointi);
        }

        { // Opintojaksot
            Validointi validointi = new Validointi(ValidationCategory.OPINTOJAKSO);
            // Opintojaksojen laajuus
            opintojaksot.forEach(oj -> {

                NavigationNodeDto ojNavigationNode = NavigationNodeDto.of(
                                NavigationType.opintojakso,
                                mapper.map(oj.getNimi(), LokalisoituTekstiDto.class),
                                oj.getId())
                        .meta("koodi", oj.getKoodi());

                validoiVirhe(validointi, "koodi-puuttuu", ojNavigationNode, ObjectUtils.isEmpty(oj.getKoodi()));
                validoiVirhe(validointi, "nimi-oltava-kaikilla-julkaisukielilla", ojNavigationNode, oj.getNimi() == null || !oj.getNimi().hasKielet(ops.getJulkaisukielet()));
                validoiHuomautus(validointi, "kuvausta-ei-ole-kirjoitettu-kaikilla-julkaisukielilla", ojNavigationNode, oj.getKuvaus() == null || !oj.getKuvaus().hasKielet(ops.getJulkaisukielet()));
                validoiVirhe(validointi, "opintojakson-laajuus-vahintaan-1", ojNavigationNode, oj.getLaajuus() < 1L);
            });

            validoinnit.add(validointi);
        }

        {
        // Onko paikallinen oppiaine vähintään yhdessä opintojaksossa
            Validointi validointi = new Validointi(ValidationCategory.OPPIAINE);
            oppiaineRepository.findAllBySisalto(ops.getLops2019()).forEach(oa -> {
                NavigationNodeDto oppiaineNavigationNode = NavigationNodeDto
                        .of(NavigationType.oppiaine, mapper.map(oa.getNimi(), LokalisoituTekstiDto.class), oa.getId())
                        .meta("koodi", mapper.map(oa.getKoodi(), KoodiDto.class));
                validoiVirhe(validointi, "oppiaineesta-opintojakso", oppiaineNavigationNode, !opintojaksot.stream().anyMatch(oj -> oj.getOppiaineet().stream()
                        .map(Lops2019OpintojaksonOppiaineDto::getKoodi)
                        .collect(Collectors.toSet())
                        .contains(oa.getKoodi())));
            });
            validoinnit.add(validointi);
        }

        if (ops.getPohja() != null &&
                ((ops.getPohja().getTyyppi().equals(Tyyppi.POHJA) && !ops.getPohja().getTila().equals(Tila.VALMIS))
                    || (ops.getPohja().getTyyppi().equals(Tyyppi.OPS) && julkaisuRepository.findAllByOpetussuunnitelma(ops.getPohja()).isEmpty()))) {
            Validointi validointi = new Validointi(ValidationCategory.OPETUSSUUNNITELMA);
            validoiVirhe(validointi, "opetussuunnitelma-pohja-julkaisematon", NavigationNodeDto.of(NavigationType.tiedot), true);
            validoinnit.add(validointi);
        }

        return validoinnit;
    }

    private void validoiVirhe(Validointi validointi, String kuvaus, NavigationNodeDto navigationNodeDto, boolean failed) {
        Optional<Validointi.Virhe> validoitu = validoi(kuvaus, navigationNodeDto, failed);
        validoitu.ifPresent(validointi::addVirhe);
    }

    private void validoiHuomautus(Validointi validointi, String kuvaus, NavigationNodeDto navigationNodeDto, boolean failed) {
        Optional<Validointi.Virhe> validoitu = validoi(kuvaus, navigationNodeDto, failed);
        validoitu.ifPresent(validointi::addHuomautus);
    }

    private Optional<Validointi.Virhe> validoi(String kuvaus, NavigationNodeDto navigationNodeDto, boolean failed) {
        if (failed) {
            Validointi.Virhe virhe = Validointi.Virhe.builder()
                    .kuvaus(kuvaus)
                    .navigationNode(navigationNodeDto)
                    .build();
            return Optional.of(virhe);
        }

        return Optional.empty();
    }

    private NavigationNodeDto moduuliNavigationNode(Lops2019ModuuliDto moduuli) {
        return NavigationNodeDto.of(NavigationType.moduuli, moduuli.getNimi(), moduuli.getId())
                .meta("oppiaine", Long.valueOf(Optional.ofNullable(moduuli.getOppiaine()).orElse(Reference.of(0L)).getId()));
    }

    private List<Lops2019ModuuliDto> haeValidoitavatModuulit(Long opsId) {
        List<Lops2019OppiaineKaikkiDto> oppiaineetAndOppimaarat = lops2019Service.getPerusteOppiaineetAndOppimaarat(opsId);
        oppiaineetAndOppimaarat.addAll(oppiaineetAndOppimaarat.stream().map(oppiaine -> oppiaine.getOppimaarat()).flatMap(Collection::stream).collect(Collectors.toList()));
        List<KoodistoKoodiDto> opintojaksottomatOppiaineetKoodit = koodistoService.getAll("opsvalidointiopintojaksottomatoppiaineet");
        if (!CollectionUtils.isEmpty(opintojaksottomatOppiaineetKoodit)) {
            List<String> opintojaksottomatOppiaineet = opintojaksottomatOppiaineetKoodit.stream().map(KoodistoKoodiDto::getKoodiArvo).collect(Collectors.toList());
            oppiaineetAndOppimaarat = oppiaineetAndOppimaarat.stream()
                    .filter(oppiaine -> !opintojaksottomatOppiaineet.contains(oppiaine.getKoodi().getUri()))
                    .collect(Collectors.toList());
        }

        List<String> moduuliKoodiUrit = oppiaineetAndOppimaarat.stream().flatMap(oppiaine -> oppiaine.getModuulit().stream()).map(moduuli -> moduuli.getKoodi().getUri()).collect(Collectors.toList());
        return lops2019Service.getPerusteModuulit(opsId).stream().filter(moduuli -> moduuliKoodiUrit.contains((moduuli.getKoodi().getUri()))).collect(Collectors.toList());
    }

    @Override
    public List<Validointi.Virhe> tarkistaOpintojaksot(Long opsId) {

        Opetussuunnitelma ops = getOpetussuunnitelma(opsId);
        List<Lops2019OpintojaksoDto> opsOpintojaksot = mapper.mapAsList(ops.getLops2019().getOpintojaksot(), Lops2019OpintojaksoDto.class);

        return opsOpintojaksot.stream().map(opintojakso -> {
                    try {
                        tarkistaOpintojakso(opsId, opintojakso);
                        return null;
                    } catch(BusinessRuleViolationException ex) {
                        return Validointi.Virhe.builder()
                                .kuvaus("ops-paikallinen-opintojakso-rakennevirhe")
                                .navigationNode(NavigationNodeDto.of(NavigationType.opintojakso, opintojakso.getNimi(), opintojakso.getId()))
                                .build();
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

    }

    @Override
    public void tarkistaOpintojakso(Long opsId, Lops2019OpintojaksoDto opintojaksoDto) {
        Opetussuunnitelma ops = getOpetussuunnitelma(opsId);
        List<Lops2019OpintojaksoDto> opsOpintojaksot = mapper.mapAsList(ops.getLops2019().getOpintojaksot(), Lops2019OpintojaksoDto.class);
        List<String> oppiaineKoodit = opintojaksoDto.getOppiaineet().stream().map(Lops2019OpintojaksonOppiaineDto::getKoodi).collect(Collectors.toList());
        List<String> moduuliKoodit = opintojaksoDto.getModuulit().stream().map(Lops2019OpintojaksonModuuliDto::getKoodiUri).collect(Collectors.toList());

        Set<Long> opintojaksotPaikallisillaopintojaksoilla = opsOpintojaksot.stream()
                .filter(opintojakso -> !CollectionUtils.isEmpty(opintojakso.getPaikallisetOpintojaksot()))
                .map(Lops2019OpintojaksoBaseDto::getId)
                .collect(Collectors.toSet());

        if (!CollectionUtils.isEmpty(opintojaksoDto.getPaikallisetOpintojaksot())) {
            opintojaksoDto.getPaikallisetOpintojaksot().forEach(paikallinenOpintojakso -> {
                if (opintojaksotPaikallisillaopintojaksoilla.contains(paikallinenOpintojakso.getId())) {
                    throw new BusinessRuleViolationException("paikalliseen-opintojaksoon-on-jo-lisatty-opintojaksoja");
                }
            });

            opintojaksoDto.getPaikallisetOpintojaksot().forEach(paikallinenOpintojakso -> {
                List<String> paikallisenOpintojaksonOppiaineKoodit = paikallinenOpintojakso.getOppiaineet().stream().map(Lops2019OpintojaksonOppiaineDto::getKoodi).collect(Collectors.toList());
                List<String> paikallisenOpintojaksonModuuliKoodit = paikallinenOpintojakso.getModuulit().stream().map(Lops2019OpintojaksonModuuliDto::getKoodiUri).collect(Collectors.toList());

                if (CollectionUtils.intersection(oppiaineKoodit, paikallisenOpintojaksonOppiaineKoodit).isEmpty()) {
                    throw new BusinessRuleViolationException("opintojaksoon-lisatty-paikallinen-opintojakso-vaaralla-oppiaineella");
                }

                if (!CollectionUtils.intersection(moduuliKoodit, paikallisenOpintojaksonModuuliKoodit).isEmpty()) {
                    throw new BusinessRuleViolationException("opintojaksoon-lisatty-paikallisen-opintojakson-moduuleita");
                }
            });
        }

        if (!CollectionUtils.isEmpty(opintojaksoDto.getOppiaineet())) {
            opintojaksoDto.getOppiaineet().forEach((oppiaine -> {
                if (oppiaine.getLaajuus() != null && oppiaine.getLaajuus() < 0) {
                    throw new BusinessRuleViolationException("opintojakson-oppiaineen-laajuus-virheellinen");
                }
            }));
        }

        KoodiValidator.validate(opintojaksoDto.getKoodi());
    }
}
