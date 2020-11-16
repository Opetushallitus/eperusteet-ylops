package fi.vm.sade.eperusteet.ylops.service.ops.impl;

import fi.vm.sade.eperusteet.ylops.dto.lops2019.Lops2019OpintojaksoDto;
import fi.vm.sade.eperusteet.ylops.dto.lops2019.Lops2019OppiaineKevytDto;
import fi.vm.sade.eperusteet.ylops.dto.lops2019.Lops2019PaikallinenOppiaineDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.lops2019.oppiaineet.Lops2019OppiaineKaikkiDto;
import fi.vm.sade.eperusteet.ylops.service.mapping.DtoMapper;
import fi.vm.sade.eperusteet.ylops.service.ops.NavigationBuilderPublic;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class NavigationBuilderLops2019PublicImpl extends NavigationBuilderLops2019Impl implements NavigationBuilderPublic {

    @Autowired
    private DtoMapper mapper;

    @Override
    protected List<Lops2019OppiaineKevytDto> getOppiaineet(Long opsId, Map<String, Set<Lops2019OpintojaksoDto>> opintojaksotMap) {
        List<Lops2019OppiaineKevytDto> copyList = mapper.mapAsList(lopsService.getPerusteOppiaineet(opsId), Lops2019OppiaineKevytDto.class);
        return copyList.stream()
                .peek(oppiaine -> {
                    if (!CollectionUtils.isEmpty(oppiaine.getOppimaarat())) {
                        // Piilotetaan oppimäärät, joilla ei ole opintojaksoja
                        oppiaine.setOppimaarat(oppiaine.getOppimaarat().stream()
                                .filter(oppimaara -> opintojaksotMap.containsKey(oppimaara.getKoodi().getUri()))
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

                    return oppiaineService.getAll(opsId).stream()
                            .anyMatch(poa -> {
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
                            });

                })
                .collect(Collectors.toList());
    }

    @Override
    protected List<Lops2019PaikallinenOppiaineDto> getPaikallisetOppiaineet(Long opsId, Map<String, Set<Lops2019OpintojaksoDto>> opintojaksotMap) {
        return oppiaineService.getAll(opsId).stream()
                .filter(poa -> opintojaksotMap.containsKey(poa.getKoodi()))
                .filter(poa -> StringUtils.isEmpty(poa.getPerusteenOppiaineUri()))
                .collect(Collectors.toList());
    }

    @Override
    protected Predicate<Lops2019PaikallinenOppiaineDto> getPaikallinenFilter(Map<String, Set<Lops2019OpintojaksoDto>> opintojaksotMap) {
        return poa -> opintojaksotMap.get(poa.getKoodi()) != null;
    }
}
