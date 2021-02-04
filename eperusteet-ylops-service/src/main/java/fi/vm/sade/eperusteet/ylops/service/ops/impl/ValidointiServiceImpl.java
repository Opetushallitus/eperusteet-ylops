package fi.vm.sade.eperusteet.ylops.service.ops.impl;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import fi.vm.sade.eperusteet.ylops.domain.Tila;
import fi.vm.sade.eperusteet.ylops.domain.Tyyppi;
import fi.vm.sade.eperusteet.ylops.domain.ValidationCategory;
import fi.vm.sade.eperusteet.ylops.domain.cache.PerusteCache;
import fi.vm.sade.eperusteet.ylops.domain.ops.Opetussuunnitelma;
import fi.vm.sade.eperusteet.ylops.dto.koodisto.KoodistoKoodiDto;
import fi.vm.sade.eperusteet.ylops.dto.lops2019.Lops2019OpintojaksoDto;
import fi.vm.sade.eperusteet.ylops.dto.lops2019.Lops2019OpintojaksonOppiaineDto;
import fi.vm.sade.eperusteet.ylops.dto.lops2019.Validointi.Lops2019ValidointiDto;
import fi.vm.sade.eperusteet.ylops.dto.lops2019.Validointi.ValidointiContext;
import fi.vm.sade.eperusteet.ylops.dto.navigation.NavigationNodeDto;
import fi.vm.sade.eperusteet.ylops.dto.navigation.NavigationType;
import fi.vm.sade.eperusteet.ylops.dto.peruste.PerusteDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.lops2019.oppiaineet.Lops2019OppiaineKaikkiDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.lops2019.oppiaineet.moduuli.Lops2019ModuuliDto;
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
import java.util.Collection;
import java.util.Collections;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

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
    public Lops2019ValidointiDto getValidointi(Long opsId) {
        Opetussuunnitelma ops = opetussuunnitelmaRepository.findOne(opsId);
        if (ops == null) {
            throw new BusinessRuleViolationException("opetussuunnitelmaa-ei-ole");
        }

        Lops2019ValidointiDto validointi = new Lops2019ValidointiDto(mapper);
        ValidointiContext ctx = new ValidointiContext();
        ctx.setKielet(ops.getJulkaisukielet());

        List<Lops2019OpintojaksoDto> opintojaksot = opintojaksoService.getAll(opsId);
        List<Lops2019ModuuliDto> moduulit = haeValidoitavatModuulit(opsId);
        Map<String, Lops2019ModuuliDto> moduulitMap = moduulit.stream().collect(Collectors.toMap(m -> m.getKoodi().getUri(), Function.identity()));
        Map<String, List<Lops2019OpintojaksoDto>> liitokset = lops2019Service.getModuuliToOpintojaksoMap(opintojaksot);

        ops.validate(validointi, ctx);

        if (!ops.isAinepainoitteinen()) {
            moduulit.forEach(moduuli -> {
                List<Lops2019OpintojaksoDto> moduulinOpintojaksot = liitokset.getOrDefault(
                        moduuli.getKoodi().getUri(),
                        new ArrayList<>());

                // - Moduuli vähintään yhdessä opintojaksossa
                validointi.varoitus(ValidationCategory.MODUULI, "moduuli-kuuluttava-vahintaan-yhteen-opintojaksoon", moduuli.getId(), moduuli.getNimi(),
                        moduulinOpintojaksot.isEmpty()
                        , ImmutableMap.of("route", moduuliNavigationNode(moduuli)));


                // - Pakollinen moduuli vähintään yhdessä opintojaksossa missä on vain muita saman oppiaineen pakollisia
                validointi.varoitus(ValidationCategory.MODUULI, "pakollinen-moduuli-mahdollista-suorittaa-erillaan", moduuli.getId(), moduuli.getNimi(),
                        moduulinOpintojaksot.stream()
                                .anyMatch(oj -> oj.getOppiaineet().size() == 1
                                        && oj.getModuulit().stream().anyMatch(ojm -> !moduulitMap.get(ojm.getKoodiUri()).isPakollinen())
                                        && oj.getModuulit().stream().anyMatch(ojm -> moduulitMap.get(ojm.getKoodiUri()).isPakollinen())
                                )
                        , ImmutableMap.of("route", moduuliNavigationNode(moduuli)));

                // - Valinnainen moduuli vähintään yhdessä opintojaksossa suoritettavissa kahden opintopisteen kokonaisuutena
                validointi.virhe(ValidationCategory.MODUULI, "valinnainen-moduuli-suoritettavissa-kahden-opintopisteen-kokonaisuutena", moduuli.getId(), moduuli.getNimi(),
                        !moduuli.isPakollinen() && !moduulinOpintojaksot.isEmpty() && moduulinOpintojaksot.stream()
                                .noneMatch(oj -> oj.getLaajuus() == 2L)
                        , ImmutableMap.of("route", moduuliNavigationNode(moduuli)));
            });
        }

        { // Opintojaksot
            ops.getLops2019().getOpintojaksot().forEach(oj -> oj.validate(validointi, ctx));

            // Opintojaksojen laajuus
            opintojaksot.forEach(oj -> {
                validointi.virhe(ValidationCategory.OPINTOJAKSO, "opintojakson-laajuus-vahintaan-1", oj.getId(), oj.getNimi(),
                        oj.getLaajuus() < 1L);
            });
        }

        // Onko paikallinen oppiaine vähintään yhdessä opintojaksossa
        oppiaineRepository.findAllBySisalto(ops.getLops2019()).forEach(oa -> {
            oa.validate(validointi, ctx);
            validointi.virhe("oppiaineesta-opintojakso", oa,
                    !opintojaksot.stream().anyMatch(oj -> oj.getOppiaineet().stream()
                            .map(Lops2019OpintojaksonOppiaineDto::getKoodi)
                            .collect(Collectors.toSet())
                            .contains(oa.getKoodi())));
        });

        if (ops.getPohja() != null &&
                ((ops.getPohja().getTyyppi().equals(Tyyppi.POHJA) && !ops.getPohja().getTila().equals(Tila.VALMIS))
                    || (ops.getPohja().getTyyppi().equals(Tyyppi.OPS) && julkaisuRepository.findAllByOpetussuunnitelma(ops.getPohja()).isEmpty()))) {
            validointi.virhe("opetussuunnitelma-pohja-julkaisematon", ops.getPohja(), true);
        }

        return validointi;
    }

    private NavigationNodeDto moduuliNavigationNode(Lops2019ModuuliDto moduuli) {
        return NavigationNodeDto.of(NavigationType.oppiaine, null, null)
                .meta("oppiaineId", Long.valueOf(moduuli.getOppiaine().getId()));
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
}
