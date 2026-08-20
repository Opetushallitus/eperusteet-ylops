package fi.vm.sade.eperusteet.ylops.service.ops.impl.navigation;

import com.google.common.collect.Sets;
import fi.vm.sade.eperusteet.ylops.domain.KoulutustyyppiToteutus;
import fi.vm.sade.eperusteet.ylops.dto.aipe.AIPEKurssiKevytDto;
import fi.vm.sade.eperusteet.ylops.dto.aipe.AIPEOppiaineKevytDto;
import fi.vm.sade.eperusteet.ylops.dto.aipe.AIPESisaltoDto;
import fi.vm.sade.eperusteet.ylops.dto.aipe.AIPEVaiheKevytDto;
import fi.vm.sade.eperusteet.ylops.dto.navigation.NavigationNodeDto;
import fi.vm.sade.eperusteet.ylops.dto.navigation.NavigationType;
import fi.vm.sade.eperusteet.ylops.service.aipe.AIPEService;
import fi.vm.sade.eperusteet.ylops.service.ops.NavigationBuilder;
import fi.vm.sade.eperusteet.ylops.service.ops.OpsDispatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
@Transactional
public class NavigationBuilderAikuistenPerusopetusImpl implements NavigationBuilder {

    @Autowired
    protected OpsDispatcher dispatcher;

    @Autowired
    private AIPEService aipeService;

    @Override
    public Set<KoulutustyyppiToteutus> getTyypit() {
        return Sets.newHashSet(KoulutustyyppiToteutus.AIPE);
    }

    @Override
    public NavigationNodeDto buildNavigation(Long opsId) {
        return NavigationNodeDto.of(NavigationType.root)
                .addAll(dispatcher.get(getNavigationBuilderClass()).buildNavigation(opsId).getChildren())
                .addAll(vaiheet(opsId));
    }

    protected Class<? extends NavigationBuilder> getNavigationBuilderClass() {
        return NavigationBuilder.class;
    }

    private List<NavigationNodeDto> vaiheet(Long opsId) {
        AIPESisaltoDto sisalto = aipeService.getSisalto(opsId);
        List<NavigationNodeDto> nodes = new ArrayList<>();
        if (sisalto.getVaiheet() != null) {
            sisalto.getVaiheet().forEach(vaihe -> nodes.add(mapVaihe(vaihe)));
        }
        nodes.add(NavigationNodeDto.of(NavigationType.uusi_vaihe)
                .meta("navigation-sub-type", "add"));
        return nodes;
    }

    private NavigationNodeDto mapVaihe(AIPEVaiheKevytDto vaihe) {
        NavigationNodeDto node = NavigationNodeDto.of(NavigationType.aipevaihe, vaihe.getNimi(), vaihe.getId())
                .meta("piilotettu", vaihe.isPiilotettu());
        if (!ObjectUtils.isEmpty(vaihe.getOppiaineet())) {
            vaihe.getOppiaineet().forEach(oa -> node.add(mapOppiaine(oa, vaihe.getId())));
        }
        return node;
    }

    private NavigationNodeDto mapOppiaine(AIPEOppiaineKevytDto oppiaine, Long vaiheId) {
        NavigationNodeDto node = NavigationNodeDto.of(NavigationType.aipeoppiaine, oppiaine.getNimi(), oppiaine.getId())
                .meta("piilotettu", oppiaine.isPiilotettu())
                .meta("vaiheId", vaiheId);
        if (!ObjectUtils.isEmpty(oppiaine.getOppimaarat())) {
            oppiaine.getOppimaarat().forEach(om -> node.add(mapOppimaara(om, vaiheId, oppiaine.getId())));
        } else if (!ObjectUtils.isEmpty(oppiaine.getKurssit())) {
            oppiaine.getKurssit().forEach(k -> node.add(mapKurssi(k, vaiheId, oppiaine.getId())));
        }
        return node;
    }

    private NavigationNodeDto mapOppimaara(AIPEOppiaineKevytDto oppimaara, Long vaiheId, Long oppiaineId) {
        NavigationNodeDto node = NavigationNodeDto.of(NavigationType.aipeoppimaara, oppimaara.getNimi(), oppimaara.getId())
                .meta("piilotettu", oppimaara.isPiilotettu())
                .meta("vaiheId", vaiheId)
                .meta("oppiaineId", oppiaineId);
        if (!ObjectUtils.isEmpty(oppimaara.getKurssit())) {
            oppimaara.getKurssit().forEach(k -> node.add(mapKurssi(k, vaiheId, oppimaara.getId())));
        }
        return node;
    }

    private NavigationNodeDto mapKurssi(AIPEKurssiKevytDto kurssi, Long vaiheId, Long oppiaineId) {
        return NavigationNodeDto.of(NavigationType.aipekurssi, kurssi.getNimi(), kurssi.getId())
                .meta("piilotettu", kurssi.isPiilotettu())
                .meta("vaiheId", vaiheId)
                .meta("oppiaineId", oppiaineId);
    }
}
