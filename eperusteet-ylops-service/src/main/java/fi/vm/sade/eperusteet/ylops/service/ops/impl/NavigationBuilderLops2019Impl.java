package fi.vm.sade.eperusteet.ylops.service.ops.impl;

import com.google.common.collect.Sets;
import fi.vm.sade.eperusteet.utils.dto.peruste.lops2019.tutkinnonrakenne.KoodiDto;
import fi.vm.sade.eperusteet.ylops.domain.KoulutustyyppiToteutus;
import fi.vm.sade.eperusteet.ylops.domain.lops2019.Lops2019OppiaineJarjestys;
import fi.vm.sade.eperusteet.ylops.domain.ops.Opetussuunnitelma;
import fi.vm.sade.eperusteet.ylops.dto.lops2019.*;
import fi.vm.sade.eperusteet.ylops.dto.navigation.NavigationNodeDto;
import fi.vm.sade.eperusteet.ylops.dto.navigation.NavigationType;
import fi.vm.sade.eperusteet.ylops.dto.peruste.lops2019.oppiaineet.moduuli.Lops2019ModuuliDto;
import fi.vm.sade.eperusteet.ylops.dto.teksti.LokalisoituTekstiDto;
import fi.vm.sade.eperusteet.ylops.repository.lops2019.Lops2019SisaltoRepository;
import fi.vm.sade.eperusteet.ylops.repository.ops.OpetussuunnitelmaRepository;
import fi.vm.sade.eperusteet.ylops.service.external.EperusteetService;
import fi.vm.sade.eperusteet.ylops.service.lops2019.Lops2019OpintojaksoService;
import fi.vm.sade.eperusteet.ylops.service.lops2019.Lops2019OppiaineService;
import fi.vm.sade.eperusteet.ylops.service.lops2019.Lops2019Service;
import fi.vm.sade.eperusteet.ylops.service.mapping.DtoMapper;
import fi.vm.sade.eperusteet.ylops.service.ops.NavigationBuilder;
import fi.vm.sade.eperusteet.ylops.service.ops.OpsDispatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.stream.Collectors;

import static fi.vm.sade.eperusteet.ylops.service.util.Nulls.assertExists;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toSet;

@Component
@Transactional
public class NavigationBuilderLops2019Impl implements NavigationBuilder {

    @Autowired
    private OpsDispatcher dispatcher;

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
    private DtoMapper mapper;

    @Override
    public Set<KoulutustyyppiToteutus> getTyypit() {
        return Sets.newHashSet(KoulutustyyppiToteutus.LOPS2019);
    }

    @Override
    public NavigationNodeDto buildNavigation(Long opsId) {
        return NavigationNodeDto.of(NavigationType.root)
                .addAll(dispatcher.get(NavigationBuilder.class).buildNavigation(opsId).getChildren())
                .add(oppiaineet(opsId));
    }

    private NavigationNodeDto oppiaineet(Long opsId) {
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

        Map<String, Lops2019PerusteKevytPaikallinenOppiaineDto> oppiaineJarjestyksetMap = new HashMap<>();

        for (Lops2019OppiaineJarjestys oppiaineJarjestys : ops.getLops2019().getOppiaineJarjestykset()) {
            String koodi = oppiaineJarjestys.getKoodi();
            Integer jarjestys = oppiaineJarjestys.getJarjestys();
            if (oppiaineJarjestyksetMap.containsKey(koodi)) {
                Lops2019PerusteKevytPaikallinenOppiaineDto dto = oppiaineJarjestyksetMap.get(koodi);
                dto.setJarjestys(jarjestys);
            } else {
                Lops2019PerusteKevytPaikallinenOppiaineDto dto = new Lops2019PerusteKevytPaikallinenOppiaineDto();
                dto.setJarjestys(jarjestys);
                oppiaineJarjestyksetMap.put(koodi, dto);
            }
        }

        // Perusteen oppiaineet
        oppiaineet.forEach(oa -> {
            String koodi = oa.getKoodi().getUri();
            if (oppiaineJarjestyksetMap.containsKey(koodi)) {
                Lops2019PerusteKevytPaikallinenOppiaineDto dto = oppiaineJarjestyksetMap.get(koodi);
                dto.setOa(oa);
            } else {
                Lops2019PerusteKevytPaikallinenOppiaineDto dto = new Lops2019PerusteKevytPaikallinenOppiaineDto();
                dto.setOa(oa);
                oppiaineJarjestyksetMap.put(koodi, dto);
            }
        });


        // Paikalliset oppiaineet
        paikallisetOppiaineet.forEach(poa -> {
            String koodi = poa.getKoodi();
            if (oppiaineJarjestyksetMap.containsKey(koodi)) {
                Lops2019PerusteKevytPaikallinenOppiaineDto dto = oppiaineJarjestyksetMap.get(koodi);
                dto.setPoa(poa);
            } else {
                Lops2019PerusteKevytPaikallinenOppiaineDto dto = new Lops2019PerusteKevytPaikallinenOppiaineDto();
                dto.setPoa(poa);
                oppiaineJarjestyksetMap.put(koodi, dto);
            }
        });

        List<NavigationNodeDto> navigationOppiaineet = new ArrayList<>();

        // Paikalliset ja perusteen oppiaineet
        oppiaineJarjestyksetMap.values().stream()
                .sorted(comparing((Lops2019PerusteKevytPaikallinenOppiaineDto dto) -> Optional
                        .ofNullable(dto.getJarjestys()).orElse(0)))
                .forEach(dto -> {
                    Lops2019OppiaineKevytDto oa = dto.getOa();
                    Lops2019PaikallinenOppiaineDto poa = dto.getPoa();
                    if (oa != null) {
                        navigationOppiaineet.add(mapOppiaine(oa, opintojaksotMap));
                    } else if (poa != null) {
                        navigationOppiaineet.add(mapPaikallinenOppiaine(poa, opintojaksotMap));
                    }
                });

        return NavigationNodeDto.of(NavigationType.oppiaineet)
                .addAll(navigationOppiaineet);
    }

    protected List<Lops2019OppiaineKevytDto> getOppiaineet(Long opsId, Map<String, Set<Lops2019OpintojaksoDto>> opintojaksotMap) {
        return mapper.mapAsList(lopsService.getPerusteOppiaineet(opsId), Lops2019OppiaineKevytDto.class);
    }

    protected List<Lops2019PaikallinenOppiaineDto> getPaikallisetOppiaineet(Long opsId, Map<String, Set<Lops2019OpintojaksoDto>> opintojaksotMap) {
        return oppiaineService.getAll(opsId);
    }

    private NavigationNodeDto mapOppiaine(
            Lops2019OppiaineKevytDto oa,
            Map<String, Set<Lops2019OpintojaksoDto>> opintojaksotMap
    ) {
        NavigationNodeDto result = NavigationNodeDto
                .of(NavigationType.oppiaine, mapper.map(oa.getNimi(), LokalisoituTekstiDto.class), oa.getId())
                .meta("koodi", mapper.map(oa.getKoodi(), KoodiDto.class));

        if (!ObjectUtils.isEmpty(oa.getOppimaarat())) {
            result.add(NavigationNodeDto.of(NavigationType.oppimaarat).meta("navigation-subtype", true)
                    .meta("navigation-subtype", true)
                    .addAll(oa.getOppimaarat().stream().map(om -> mapOppiaine(om, opintojaksotMap))));
        }

        if (oa.getKoodi() != null && oa.getKoodi().getUri() != null
                && opintojaksotMap.containsKey(oa.getKoodi().getUri())
                && !ObjectUtils.isEmpty(opintojaksotMap.get(oa.getKoodi().getUri()))) {
            Set<Lops2019OpintojaksoDto> oaOpintojaksot = opintojaksotMap.get(oa.getKoodi().getUri());
            result.add(NavigationNodeDto.of(NavigationType.opintojaksot).meta("navigation-subtype", true)
                    .addAll(oaOpintojaksot.stream()
                            .map(ojOa -> NavigationNodeDto.of(
                                    NavigationType.opintojakso,
                                    mapper.map(ojOa.getNimi(), LokalisoituTekstiDto.class),
                                    ojOa.getId())
                                    .meta("koodi", ojOa.getKoodi()))));
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
                            .map(ojPoa -> NavigationNodeDto.of(
                                    NavigationType.opintojakso,
                                    mapper.map(ojPoa.getNimi(), LokalisoituTekstiDto.class),
                                    ojPoa.getId())
                                    .meta("koodi", ojPoa.getKoodi()))));
        }

        return result;
    }
}
