package fi.vm.sade.eperusteet.ylops.service.ops.impl;

import fi.vm.sade.eperusteet.ylops.dto.lops2019.Lops2019OpintojaksoDto;
import fi.vm.sade.eperusteet.ylops.dto.lops2019.Lops2019PaikallinenOppiaineDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.lops2019.oppiaineet.Lops2019OppiaineKaikkiDto;
import fi.vm.sade.eperusteet.ylops.service.mapping.DtoMapper;
import fi.vm.sade.eperusteet.ylops.service.ops.NavigationBuilderPublic;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class NavigationBuilderLops2019PublicImpl extends NavigationBuilderLops2019Impl implements NavigationBuilderPublic {

    @Autowired
    private DtoMapper mapper;

    @Override
    protected List<Lops2019OppiaineKaikkiDto> getOppiaineet(Long opsId, Map<String, Set<Lops2019OpintojaksoDto>> opintojaksotMap) {
        List<Lops2019OppiaineKaikkiDto> copyList = mapper.mapAsList(lopsService.getPerusteOppiaineet(opsId), Lops2019OppiaineKaikkiDto.class);
        return copyList.stream()
                .map(oppiaine -> {
                    if (!CollectionUtils.isEmpty(oppiaine.getOppimaarat())) {
                        oppiaine.setOppimaarat(oppiaine.getOppimaarat().stream()
                                .filter(oppimaara -> opintojaksotMap.keySet().contains(oppimaara.getKoodi().getUri()))
                                .collect(Collectors.toList()));
                    }
                    return oppiaine;
                })
                .filter(oppiaine -> opintojaksotMap.keySet().contains(oppiaine.getKoodi().getUri()) || !CollectionUtils.isEmpty(oppiaine.getOppimaarat()))
                .collect(Collectors.toList());
    }

    @Override
    protected List<Lops2019PaikallinenOppiaineDto> getPaikallisetOppiaineet(Long opsId, Map<String, Set<Lops2019OpintojaksoDto>> opintojaksotMap) {
        return oppiaineService.getAll(opsId).stream().filter(oppiaine -> opintojaksotMap.keySet().contains(oppiaine.getKoodi())).collect(Collectors.toList());
    }
}
