package fi.vm.sade.eperusteet.ylops.service.ops.impl;

import com.google.common.collect.Sets;
import fi.vm.sade.eperusteet.utils.dto.peruste.lops2019.tutkinnonrakenne.KoodiDto;
import fi.vm.sade.eperusteet.ylops.domain.KoulutustyyppiToteutus;
import fi.vm.sade.eperusteet.ylops.domain.lops2019.Lops2019OppiaineJarjestys;
import fi.vm.sade.eperusteet.ylops.domain.ops.Opetussuunnitelma;
import fi.vm.sade.eperusteet.ylops.dto.lops2019.Lops2019OpintojaksoDto;
import fi.vm.sade.eperusteet.ylops.dto.lops2019.Lops2019OpintojaksonOppiaineDto;
import fi.vm.sade.eperusteet.ylops.dto.lops2019.Lops2019OppiaineKevytDto;
import fi.vm.sade.eperusteet.ylops.dto.lops2019.Lops2019PaikallinenOppiaineDto;
import fi.vm.sade.eperusteet.ylops.dto.navigation.NavigationNodeDto;
import fi.vm.sade.eperusteet.ylops.dto.navigation.NavigationType;
import fi.vm.sade.eperusteet.ylops.dto.peruste.lops2019.oppiaineet.Lops2019OppiaineKaikkiDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.lops2019.oppiaineet.moduuli.Lops2019ModuuliDto;
import fi.vm.sade.eperusteet.ylops.dto.teksti.LokalisoituTekstiDto;
import fi.vm.sade.eperusteet.ylops.repository.lops2019.Lops2019SisaltoRepository;
import fi.vm.sade.eperusteet.ylops.repository.ops.OpetussuunnitelmaRepository;
import fi.vm.sade.eperusteet.ylops.service.external.EperusteetService;
import fi.vm.sade.eperusteet.ylops.service.lops2019.Lops2019OpintojaksoService;
import fi.vm.sade.eperusteet.ylops.service.lops2019.Lops2019OppiaineService;
import fi.vm.sade.eperusteet.ylops.service.lops2019.Lops2019Service;
import fi.vm.sade.eperusteet.ylops.service.lops2019.impl.Lops2019Utils;
import fi.vm.sade.eperusteet.ylops.service.mapping.DtoMapper;
import fi.vm.sade.eperusteet.ylops.service.ops.NavigationBuilder;
import fi.vm.sade.eperusteet.ylops.service.ops.OpsDispatcher;
import fi.vm.sade.eperusteet.ylops.service.util.Pair;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import static fi.vm.sade.eperusteet.ylops.service.util.Nulls.assertExists;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toSet;

@Component
@Transactional
public class NavigationBuilderLops2019Impl implements NavigationBuilder {

    @Autowired
    protected OpsDispatcher dispatcher;

    @Autowired
    private EperusteetService eperusteetService;

    @Autowired
    private Lops2019OpintojaksoService opintojaksoService;

    @Autowired
    private Lops2019SisaltoRepository lops2019SisaltoRepository;

    @Autowired
    private OpetussuunnitelmaRepository opetussuunnitelmaRepository;

    @Autowired
    protected Lops2019Service lopsService;

    @Autowired
    protected Lops2019OppiaineService oppiaineService;

    @Autowired
    private OpetussuunnitelmaRepository opsRepository;

    @Autowired
    protected DtoMapper mapper;

    @Override
    public Set<KoulutustyyppiToteutus> getTyypit() {
        return Sets.newHashSet(KoulutustyyppiToteutus.LOPS2019);
    }

    @Override
    public NavigationNodeDto buildNavigation(Long opsId) {
        return NavigationNodeDto.of(NavigationType.root)
                .addAll(dispatcher.get(getNavigationBuilderClass()).buildNavigation(opsId).getChildren())
                .add(oppiaineet(opsId));
    }

    protected Class<? extends NavigationBuilder> getNavigationBuilderClass() {
        return NavigationBuilder.class;
    }

    protected NavigationNodeDto oppiaineet(Long opsId) {
        // Järjestetään oppiaineen koodilla opintojaksot
        Map<String, Set<Lops2019OpintojaksoDto>> opintojaksotMap = opintojaksoService.getAllTuodut(opsId).stream()
                .flatMap(oj -> oj.getOppiaineet().stream()
                        .map(oa -> new AbstractMap.SimpleImmutableEntry<>(oa.getKoodi(), oj)))
                .collect(Collectors.groupingBy(
                        Map.Entry::getKey,
                        Collectors.mapping(Map.Entry::getValue, toSet())
                ));

        List<Lops2019OppiaineKevytDto> oppiaineet = getOppiaineet(opsId, opintojaksotMap);
        List<Lops2019PaikallinenOppiaineDto> paikallisetOppiaineet = getPaikallisetOppiaineet(opsId, opintojaksotMap);

        Opetussuunnitelma ops = opsRepository.findOne(opsId);
        assertExists(ops, "Pyydettyä opetussuunnitelmaa ei ole olemassa");
        Set<Lops2019OppiaineJarjestys> oppiaineJarjestykset = ops.getLops2019().getOppiaineJarjestykset();

        List<NavigationNodeDto> navigationOppiaineet = new ArrayList<>();

        Lops2019Utils.sortOppiaineet(
                oppiaineJarjestykset,
                oppiaineet,
                paikallisetOppiaineet,
                oa -> navigationOppiaineet.add(mapOppiaine((Lops2019OppiaineKevytDto) oa, opintojaksotMap, opsId, oppiaineJarjestykset)),
                poa -> navigationOppiaineet.add(mapPaikallinenOppiaine(poa, opintojaksotMap))
        );

        return NavigationNodeDto.of(NavigationType.oppiaineet)
                .addAll(navigationOppiaineet);
    }

    protected List<Lops2019OppiaineKevytDto> getOppiaineet(Long opsId, Map<String, Set<Lops2019OpintojaksoDto>> opintojaksotMap) {
        return mapper.mapAsList(lopsService.getPerusteOppiaineet(opsId), Lops2019OppiaineKevytDto.class);
    }

    protected List<Lops2019PaikallinenOppiaineDto> getPaikallisetOppiaineet(Long opsId, Map<String, Set<Lops2019OpintojaksoDto>> opintojaksotMap) {
        return oppiaineService.getAll(opsId);
    }

    protected Predicate<Lops2019PaikallinenOppiaineDto> getPaikallinenFilter(Map<String, Set<Lops2019OpintojaksoDto>> opintojaksotMap) {
        return pao -> true;
    }

    private NavigationNodeDto mapOppiaine(
            Lops2019OppiaineKevytDto oa,
            Map<String, Set<Lops2019OpintojaksoDto>> opintojaksotMap,
            Long opsId,
            Set<Lops2019OppiaineJarjestys> oppiaineJarjestykset
    ) {
        NavigationNodeDto result = NavigationNodeDto
                .of(NavigationType.oppiaine, mapper.map(oa.getNimi(), LokalisoituTekstiDto.class), oa.getId())
                .meta("koodi", mapper.map(oa.getKoodi(), KoodiDto.class));

        List<Lops2019PaikallinenOppiaineDto> paikallisetOppimaarat = oppiaineService.getAll(opsId).stream()
                .filter(poa -> {
                    String parentKoodi = poa.getPerusteenOppiaineUri();
                    Optional<Lops2019OppiaineKaikkiDto> orgOaOpt = lopsService.getPerusteOppiaineet(opsId).stream()
                            .filter(oaOrg -> oaOrg.getId().equals(oa.getId()))
                            .findAny();
                    if (parentKoodi != null) {
                        return (oa.getKoodi() != null
                                && oa.getKoodi().getUri() != null
                                && oa.getKoodi().getUri().equals(parentKoodi))
                                || (orgOaOpt.isPresent() && orgOaOpt.get().getOppimaarat().stream()
                                .filter(om -> om.getKoodi() != null)
                                .filter(om -> om.getKoodi().getUri() != null)
                                .anyMatch(om -> om.getKoodi().getUri().equals(parentKoodi)));
                    }
                    return false;
                })
                .collect(Collectors.toList());

        if (!ObjectUtils.isEmpty(oa.getOppimaarat()) || !ObjectUtils.isEmpty(paikallisetOppimaarat)) {
            NavigationNodeDto oppimaaratNode = NavigationNodeDto.of(NavigationType.oppimaarat).meta("navigation-subtype", true)
                    .meta("navigation-subtype", true);

            Lops2019Utils.sortOppiaineet(
                    oppiaineJarjestykset,
                    oa.getOppimaarat(),
                    paikallisetOppimaarat.stream().filter(getPaikallinenFilter(opintojaksotMap)).collect(Collectors.toList()),
                    om -> oppimaaratNode.add(mapOppiaine((Lops2019OppiaineKevytDto) om, opintojaksotMap, opsId, oppiaineJarjestykset)) != null,
                    pom -> oppimaaratNode.add(mapPaikallinenOppiaine(pom, opintojaksotMap)) != null
            );

            result.add(oppimaaratNode);
        }

        if (oa.getKoodi() != null && oa.getKoodi().getUri() != null
                && opintojaksotMap.containsKey(oa.getKoodi().getUri())
                && !ObjectUtils.isEmpty(opintojaksotMap.get(oa.getKoodi().getUri()))) {
            Set<Lops2019OpintojaksoDto> oaOpintojaksot = opintojaksotMap.get(oa.getKoodi().getUri());
            result.add(NavigationNodeDto.of(NavigationType.opintojaksot).meta("navigation-subtype", true)
                    .addAll(oaOpintojaksot.stream()
                            .map(oj -> {
                                Optional<Lops2019OpintojaksonOppiaineDto> ojOaOpt = oj.getOppiaineet().stream()
                                        .filter(ojOa -> ojOa.getKoodi() != null)
                                        .filter(ojOa -> ojOa.getKoodi().equals(oa.getKoodi().getUri()))
                                        .findAny();
                                return new Pair<>(oj, ojOaOpt);
                            })
                            // Ensisijaisesti järjestetään opintojakson oppiaineen järjestyksen mukaan.
                            // Toissijaisesti opintojakson koodin mukaan.
                            .sorted(comparing(p -> p.getFirst().getKoodi()))
                            .sorted(comparing((Pair<Lops2019OpintojaksoDto, Optional<Lops2019OpintojaksonOppiaineDto>> p)
                                    -> Optional.ofNullable(p.getSecond().isPresent()
                                    ? p.getSecond().get().getJarjestys()
                                    : null).orElse(Integer.MAX_VALUE)))
                            .map(p -> NavigationNodeDto.of(
                                    NavigationType.opintojakso,
                                    mapper.map(p.getFirst().getNimi(), LokalisoituTekstiDto.class),
                                    p.getFirst().getId())
                                    .meta("koodi", p.getFirst().getKoodi()))));
        }

        List<Lops2019ModuuliDto> moduulit = oa.getModuulit();
        if (!ObjectUtils.isEmpty(moduulit)) {
            result.add(NavigationNodeDto.of(NavigationType.moduulit).meta("navigation-subtype", true)
                    .addAll(moduulit.stream()
                            .map(m -> NavigationNodeDto.of(
                                    NavigationType.moduuli,
                                    mapper.map(m.getNimi(), LokalisoituTekstiDto.class),
                                    m.getId())
                                    .meta("oppiaine", m.getOppiaine() != null ? m.getOppiaine().getId() : null)
                                    .meta("koodi", mapper.map(m.getKoodi(), KoodiDto.class))
                                    .meta("laajuus", m.getLaajuus())
                                    .meta("pakollinen", m.isPakollinen()))));
        }

        return result;
    }

    private NavigationNodeDto mapPaikallinenOppiaine(
            Lops2019PaikallinenOppiaineDto poa,
            Map<String, Set<Lops2019OpintojaksoDto>> opintojaksotMap
    ) {
        NavigationNodeDto result = NavigationNodeDto
                .of(NavigationType.poppiaine, mapper.map(poa.getNimi(), LokalisoituTekstiDto.class), poa.getId())
                .meta("koodi", poa.getKoodi());

        if (poa.getKoodi() != null
                && opintojaksotMap.containsKey(poa.getKoodi())
                && !ObjectUtils.isEmpty(opintojaksotMap.get(poa.getKoodi()))) {
            Set<Lops2019OpintojaksoDto> poaOpintojaksot = opintojaksotMap.get(poa.getKoodi());
            result.add(NavigationNodeDto.of(NavigationType.opintojaksot).meta("navigation-subtype", true)
                    .addAll(poaOpintojaksot.stream()
                            .map(oj -> {
                                Optional<Lops2019OpintojaksonOppiaineDto> ojOaOpt = oj.getOppiaineet().stream()
                                        .filter(ojOa -> ojOa.getKoodi() != null)
                                        .filter(ojOa -> ojOa.getKoodi().equals(poa.getKoodi()))
                                        .findAny();
                                return new Pair<>(oj, ojOaOpt);
                            })
                            // Ensisijaisesti järjestetään opintojakson oppiaineen järjestyksen mukaan.
                            // Toissijaisesti opintojakson koodin mukaan.
                            .sorted(comparing(p -> p.getFirst().getKoodi()))
                            .sorted(comparing((Pair<Lops2019OpintojaksoDto, Optional<Lops2019OpintojaksonOppiaineDto>> p)
                                    -> Optional.ofNullable(p.getSecond().isPresent()
                                    ? p.getSecond().get().getJarjestys()
                                    : null).orElse(Integer.MAX_VALUE)))
                            .map(p -> NavigationNodeDto.of(
                                    NavigationType.opintojakso,
                                    mapper.map(p.getFirst().getNimi(), LokalisoituTekstiDto.class),
                                    p.getFirst().getId())
                                    .meta("koodi", p.getFirst().getKoodi()))));
        }

        return result;
    }

}
