package fi.vm.sade.eperusteet.ylops.service.ops.impl;

import fi.vm.sade.eperusteet.ylops.dto.lops2019.Lops2019OpintojaksoDto;
import fi.vm.sade.eperusteet.ylops.dto.lops2019.Lops2019OppiaineKevytDto;
import fi.vm.sade.eperusteet.ylops.dto.lops2019.Lops2019PaikallinenOppiaineKevytDto;
import fi.vm.sade.eperusteet.ylops.service.ops.NavigationBuilder;
import fi.vm.sade.eperusteet.ylops.service.ops.NavigationBuilderPublic;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class NavigationBuilderLops2019PublicImpl extends NavigationBuilderLops2019Impl implements NavigationBuilderPublic {

    @Override
    protected Class<? extends NavigationBuilder> getNavigationBuilderClass() {
        return NavigationBuilderPublic.class;
    }

    @Override
    protected List<Lops2019OppiaineKevytDto> getOppiaineet(Long opsId, Map<String, Set<Lops2019OpintojaksoDto>> opintojaksotMap) {
        List<Lops2019OppiaineKevytDto> perusteOppiaineet = mapper.mapAsList(lopsService.getPerusteOppiaineet(opsId), Lops2019OppiaineKevytDto.class);
        List<Lops2019PaikallinenOppiaineKevytDto> paikallisetOppiaineet = oppiaineService.getAll(opsId, Lops2019PaikallinenOppiaineKevytDto.class);
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
                                Optional<Lops2019OppiaineKevytDto> perusteOppiaine = perusteOppiaineet.stream()
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

    @Override
    protected List<Lops2019PaikallinenOppiaineKevytDto> getPaikallisetOppiaineet(Long opsId, Map<String, Set<Lops2019OpintojaksoDto>> opintojaksotMap) {
        return oppiaineService.getAll(opsId, Lops2019PaikallinenOppiaineKevytDto.class).stream()
                .filter(poa -> opintojaksotMap.containsKey(poa.getKoodi()))
                .filter(poa -> StringUtils.isEmpty(poa.getPerusteenOppiaineUri()))
                .collect(Collectors.toList());
    }

    @Override
    protected Predicate<Lops2019PaikallinenOppiaineKevytDto> getPaikallinenFilter(Map<String, Set<Lops2019OpintojaksoDto>> opintojaksotMap) {
        return poa -> opintojaksotMap.get(poa.getKoodi()) != null;
    }

}
