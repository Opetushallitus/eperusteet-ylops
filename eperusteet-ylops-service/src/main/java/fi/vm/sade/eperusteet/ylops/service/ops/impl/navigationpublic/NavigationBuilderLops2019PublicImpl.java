package fi.vm.sade.eperusteet.ylops.service.ops.impl.navigationpublic;

import com.google.common.collect.Sets;
import fi.vm.sade.eperusteet.ylops.domain.KoulutustyyppiToteutus;
import fi.vm.sade.eperusteet.ylops.dto.lops2019.Lops2019OpintojaksonOppiaineDto;
import fi.vm.sade.eperusteet.ylops.dto.lops2019.Lops2019OppiaineJarjestysDto;
import fi.vm.sade.eperusteet.ylops.dto.lops2019.export.Lops2019OpintojaksoExportDto;
import fi.vm.sade.eperusteet.ylops.dto.lops2019.export.Lops2019OppiaineExportDto;
import fi.vm.sade.eperusteet.ylops.dto.lops2019.export.Lops2019PaikallinenOppiaineExportDto;
import fi.vm.sade.eperusteet.ylops.dto.lops2019.export.OpetussuunnitelmaExportLops2019Dto;
import fi.vm.sade.eperusteet.ylops.dto.navigation.NavigationNodeDto;
import fi.vm.sade.eperusteet.ylops.dto.navigation.NavigationType;
import fi.vm.sade.eperusteet.ylops.dto.peruste.lops2019.oppiaineet.Lops2019OppiaineKaikkiDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.lops2019.oppiaineet.moduuli.Lops2019ModuuliDto;
import fi.vm.sade.eperusteet.ylops.service.external.EperusteetService;
import fi.vm.sade.eperusteet.ylops.service.lops2019.impl.Lops2019Utils;
import fi.vm.sade.eperusteet.ylops.service.ops.NavigationBuilderPublic;
import fi.vm.sade.eperusteet.ylops.service.ops.OpetussuunnitelmaService;
import fi.vm.sade.eperusteet.ylops.service.ops.OpsDispatcher;
import fi.vm.sade.eperusteet.ylops.service.util.Pair;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toSet;

@Component
@Transactional
public class NavigationBuilderLops2019PublicImpl implements NavigationBuilderPublic {

    @Autowired
    protected OpsDispatcher dispatcher;

    @Autowired
    private OpetussuunnitelmaService opetussuunnitelmaService;

    @Autowired
    private EperusteetService eperusteetService;

    @Override
    public Set<KoulutustyyppiToteutus> getTyypit() {
        return Sets.newHashSet(KoulutustyyppiToteutus.LOPS2019);
    }

    @Override
    public NavigationNodeDto buildNavigation(Long opsId, boolean esikatselu) {
        return NavigationNodeDto.of(NavigationType.root)
                .addAll(dispatcher.get(NavigationBuilderPublic.class).buildNavigation(opsId, esikatselu).getChildren())
                .add(oppiaineet(opsId, esikatselu));
    }

    protected NavigationNodeDto oppiaineet(Long opsId, boolean esikatselu) {
        // Järjestetään oppiaineen koodilla opintojaksot
        OpetussuunnitelmaExportLops2019Dto opetussuunnitelmaDto = (OpetussuunnitelmaExportLops2019Dto) opetussuunnitelmaService.getOpetussuunnitelmaJulkaistuSisalto(opsId, esikatselu);
        Map<String, Set<Lops2019OpintojaksoExportDto>> opintojaksotMap = opetussuunnitelmaDto.getOpintojaksot().stream()
                .flatMap(oj -> oj.getOppiaineet().stream()
                        .map(oa -> new AbstractMap.SimpleImmutableEntry<>(oa.getKoodi(), oj)))
                .collect(Collectors.groupingBy(
                        Map.Entry::getKey,
                        Collectors.mapping(Map.Entry::getValue, toSet())
                ));

        List<Lops2019OppiaineExportDto> oppiaineet = getOppiaineet(opetussuunnitelmaDto, opintojaksotMap);
        List<Lops2019PaikallinenOppiaineExportDto> paikallisetOppiaineet = getPaikallisetOppiaineet(opetussuunnitelmaDto, opintojaksotMap);
        Set<Lops2019OppiaineJarjestysDto> oppiaineJarjestykset = opetussuunnitelmaDto.getOppiaineJarjestykset();
        List<NavigationNodeDto> navigationOppiaineet = new ArrayList<>();

        Lops2019Utils.sortOppiaineet(
                oppiaineJarjestykset,
                oppiaineet,
                paikallisetOppiaineet,
                oa -> navigationOppiaineet.add(mapOppiaine(opetussuunnitelmaDto, (Lops2019OppiaineExportDto) oa, opintojaksotMap, opsId, oppiaineJarjestykset)),
                poa -> navigationOppiaineet.add(mapPaikallinenOppiaine((Lops2019PaikallinenOppiaineExportDto) poa, opintojaksotMap))
        );

        return NavigationNodeDto.of(NavigationType.oppiaineet)
                .addAll(navigationOppiaineet);
    }

    protected List<Lops2019OppiaineExportDto> getOppiaineet(OpetussuunnitelmaExportLops2019Dto opetussuunnitelmaDto, Map<String, Set<Lops2019OpintojaksoExportDto>> opintojaksotMap) {
        List<Lops2019OppiaineExportDto> perusteOppiaineet = opetussuunnitelmaDto.getValtakunnallisetOppiaineet();
        List<Lops2019PaikallinenOppiaineExportDto> paikallisetOppiaineet = opetussuunnitelmaDto.getPaikallisetOppiaineet();
        Set<String> opintojaksojenModuuliUrit = opintojaksotMap.values().stream()
                .flatMap(x -> x.stream())
                .map(opintojakso -> opintojakso.getModuulit())
                .flatMap(x -> x.stream())
                .map(moduuli -> moduuli.getKoodiUri())
                .collect(Collectors.toSet());
        return perusteOppiaineet.stream()
                .peek(oppiaine -> {
                    if (CollectionUtils.isNotEmpty(oppiaine.getOppimaarat())) {
                        // Piilotetaan oppimäärät, joilla ei ole opintojaksoja
                        oppiaine.setOppimaarat(oppiaine.getOppimaarat().stream()
                                .filter(oppimaara -> opintojaksotMap.containsKey(oppimaara.getKoodi().getUri()))
                                .collect(Collectors.toList()));
                    }
                    if (CollectionUtils.isNotEmpty(oppiaine.getModuulit())) {
                        oppiaine.setModuulit(oppiaine.getModuulit().stream()
                                .filter(lops2019ModuuliDto -> opintojaksojenModuuliUrit.contains(lops2019ModuuliDto.getKoodi().getUri()))
                                .collect(Collectors.toList()));
                    }
                })
                .filter(oa -> {
                    if (opintojaksotMap.containsKey(oa.getKoodi().getUri())
                            || !CollectionUtils.isEmpty(oa.getOppimaarat())) {
                        return true;
                    } else if (!CollectionUtils.isEmpty(oa.getOppimaarat())) {
                        return oa.getOppimaarat().stream()
                                .filter(om -> om.getKoodi() != null)
                                .filter(om -> om.getKoodi().getUri() != null)
                                .anyMatch(om -> opintojaksotMap.containsKey(om.getKoodi().getUri()));
                    }

                    return paikallisetOppiaineet.stream()
                            .anyMatch(poa -> {
                                String parentKoodi = poa.getPerusteenOppiaineUri();
                                Optional<Lops2019OppiaineKaikkiDto> perusteOppiaine = eperusteetService.getPerusteById(opetussuunnitelmaDto.getPerusteenId()).getLops2019().getOppiaineet().stream()
                                        .filter(oaOrg -> oaOrg.getId().equals(oa.getId()))
                                        .findAny();
                                if (parentKoodi != null) {
                                    return (oa.getKoodi() != null
                                            && oa.getKoodi().getUri() != null
                                            && oa.getKoodi().getUri().equals(parentKoodi))
                                            || (perusteOppiaine.isPresent() && perusteOppiaine.get().getOppimaarat().stream()
                                            .filter(om -> om.getKoodi() != null)
                                            .filter(om -> om.getKoodi().getUri() != null)
                                            .anyMatch(om -> om.getKoodi().getUri().equals(parentKoodi)));
                                }
                                return false;
                            });

                })
                .collect(Collectors.toList());
    }

    protected List<Lops2019PaikallinenOppiaineExportDto> getPaikallisetOppiaineet(OpetussuunnitelmaExportLops2019Dto opetussuunnitelmaDto, Map<String, Set<Lops2019OpintojaksoExportDto>> opintojaksotMap) {
        return opetussuunnitelmaDto.getPaikallisetOppiaineet().stream()
                .filter(oppiaine -> opintojaksotMap.containsKey(oppiaine.getKoodi()))
                .filter(poa -> StringUtils.isEmpty(poa.getPerusteenOppiaineUri()))
                .collect(Collectors.toList());
    }

    protected Predicate<Lops2019PaikallinenOppiaineExportDto> getPaikallinenFilter(Map<String, Set<Lops2019OpintojaksoExportDto>> opintojaksotMap) {
        return poa -> opintojaksotMap.get(poa.getKoodi()) != null;
    }

    private NavigationNodeDto mapOppiaine(
            OpetussuunnitelmaExportLops2019Dto opetussuunnitelmaDto,
            Lops2019OppiaineExportDto oa,
            Map<String, Set<Lops2019OpintojaksoExportDto>> opintojaksotMap,
            Long opsId,
            Set<Lops2019OppiaineJarjestysDto> oppiaineJarjestykset
    ) {
        NavigationNodeDto result = NavigationNodeDto
                .of(NavigationType.oppiaine, oa.getNimi(), oa.getId())
                .meta("koodi", oa.getKoodi());

        List<Lops2019PaikallinenOppiaineExportDto> paikallisetOppimaarat = opetussuunnitelmaDto.getPaikallisetOppiaineet().stream()
                .filter(poa -> {
                    String parentKoodi = poa.getPerusteenOppiaineUri();
                    Optional<Lops2019OppiaineKaikkiDto> orgOaOpt = eperusteetService.getPerusteById(opetussuunnitelmaDto.getPerusteenId()).getLops2019().getOppiaineet().stream()
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
                    om -> oppimaaratNode.add(mapOppiaine(opetussuunnitelmaDto, (Lops2019OppiaineExportDto) om, opintojaksotMap, opsId, oppiaineJarjestykset)) != null,
                    pom -> oppimaaratNode.add(mapPaikallinenOppiaine((Lops2019PaikallinenOppiaineExportDto) pom, opintojaksotMap)) != null
            );

            result.add(oppimaaratNode);
        }

        if (oa.getKoodi() != null && oa.getKoodi().getUri() != null
                && opintojaksotMap.containsKey(oa.getKoodi().getUri())
                && !ObjectUtils.isEmpty(opintojaksotMap.get(oa.getKoodi().getUri()))) {
            Set<Lops2019OpintojaksoExportDto> oaOpintojaksot = opintojaksotMap.get(oa.getKoodi().getUri());
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
                            .sorted(comparing((Pair<Lops2019OpintojaksoExportDto, Optional<Lops2019OpintojaksonOppiaineDto>> p)
                                    -> Optional.ofNullable(p.getSecond().isPresent()
                                    ? p.getSecond().get().getJarjestys()
                                    : null).orElse(Integer.MAX_VALUE)))
                            .map(p -> NavigationNodeDto.of(
                                    NavigationType.opintojakso,
                                    p.getFirst().getNimi(),
                                    p.getFirst().getId())
                                    .meta("koodi", p.getFirst().getKoodi()))));
        }

        List<Lops2019ModuuliDto> moduulit = oa.getModuulit();
        if (!ObjectUtils.isEmpty(moduulit)) {
            result.add(NavigationNodeDto.of(NavigationType.moduulit).meta("navigation-subtype", true)
                    .addAll(moduulit.stream()
                            .map(m -> NavigationNodeDto.of(
                                    NavigationType.moduuli,
                                    m.getNimi(),
                                    m.getId())
                                    .meta("oppiaine", m.getOppiaine() != null ? m.getOppiaine().getId() : null)
                                    .meta("koodi", m.getKoodi())
                                    .meta("laajuus", m.getLaajuus())
                                    .meta("pakollinen", m.isPakollinen()))));
        }

        return result;
    }

    private NavigationNodeDto mapPaikallinenOppiaine(
            Lops2019PaikallinenOppiaineExportDto poa,
            Map<String, Set<Lops2019OpintojaksoExportDto>> opintojaksotMap
    ) {
        NavigationNodeDto result = NavigationNodeDto
                .of(NavigationType.poppiaine, poa.getNimi(), poa.getId())
                .meta("koodi", poa.getKoodi());

        if (poa.getKoodi() != null
                && opintojaksotMap.containsKey(poa.getKoodi())
                && !ObjectUtils.isEmpty(opintojaksotMap.get(poa.getKoodi()))) {
            Set<Lops2019OpintojaksoExportDto> poaOpintojaksot = opintojaksotMap.get(poa.getKoodi());
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
                            .sorted(comparing((Pair<Lops2019OpintojaksoExportDto, Optional<Lops2019OpintojaksonOppiaineDto>> p)
                                    -> Optional.ofNullable(p.getSecond().isPresent()
                                    ? p.getSecond().get().getJarjestys()
                                    : null).orElse(Integer.MAX_VALUE)))
                            .map(p -> NavigationNodeDto.of(
                                    NavigationType.opintojakso,
                                    p.getFirst().getNimi(),
                                    p.getFirst().getId())
                                    .meta("koodi", p.getFirst().getKoodi()))));
        }

        return result;
    }

}
