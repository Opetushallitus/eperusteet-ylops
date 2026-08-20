package fi.vm.sade.eperusteet.ylops.service.ops.impl.navigationpublic;

import com.google.common.collect.Sets;
import fi.vm.sade.eperusteet.ylops.domain.KoulutustyyppiToteutus;
import fi.vm.sade.eperusteet.ylops.dto.aipe.export.AIPEKurssiExportDto;
import fi.vm.sade.eperusteet.ylops.dto.aipe.export.AIPEOppiaineExportDto;
import fi.vm.sade.eperusteet.ylops.dto.aipe.export.AIPESisaltoExportDto;
import fi.vm.sade.eperusteet.ylops.dto.aipe.export.AIPEVaiheExportDto;
import fi.vm.sade.eperusteet.ylops.dto.export.OpetussuunnitelmaExportAipeDto;
import fi.vm.sade.eperusteet.ylops.dto.navigation.NavigationNodeDto;
import fi.vm.sade.eperusteet.ylops.dto.navigation.NavigationType;
import fi.vm.sade.eperusteet.ylops.service.ops.NavigationBuilderPublic;
import fi.vm.sade.eperusteet.ylops.service.ops.OpetussuunnitelmaService;
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
public class NavigationBuilderAikuistenPerusopetusPublicImpl implements NavigationBuilderPublic {

    @Autowired
    private OpsDispatcher dispatcher;

    @Autowired
    private OpetussuunnitelmaService opetussuunnitelmaService;

    @Override
    public Set<KoulutustyyppiToteutus> getTyypit() {
        return Sets.newHashSet(KoulutustyyppiToteutus.AIPE);
    }

    @Override
    public NavigationNodeDto buildNavigation(Long opsId, Integer revision) {
        OpetussuunnitelmaExportAipeDto export = (OpetussuunnitelmaExportAipeDto) opetussuunnitelmaService
                .getOpetussuunnitelmaJulkaistuSisalto(opsId, revision);
        AIPESisaltoExportDto sisalto = export.getAipe() != null ? export.getAipe() : new AIPESisaltoExportDto();

        return dispatcher.get(NavigationBuilderPublic.class).buildNavigation(opsId, revision)
                .addAll(vaiheet(sisalto));
    }

    private List<NavigationNodeDto> vaiheet(AIPESisaltoExportDto sisalto) {
        List<NavigationNodeDto> nodes = new ArrayList<>();
        if (sisalto.getVaiheet() != null) {
            sisalto.getVaiheet().forEach(vaihe -> nodes.add(mapVaihe(vaihe)));
        }
        return nodes;
    }

    private NavigationNodeDto mapVaihe(AIPEVaiheExportDto vaihe) {
        NavigationNodeDto node = NavigationNodeDto.of(NavigationType.aipevaihe, vaihe.getNimi(), vaihe.getId());
        if (!ObjectUtils.isEmpty(vaihe.getOppiaineet())) {
            vaihe.getOppiaineet().forEach(oa -> node.add(mapOppiaine(oa, vaihe.getId())));
        }
        return node;
    }

    private NavigationNodeDto mapOppiaine(AIPEOppiaineExportDto oppiaine, Long vaiheId) {
        NavigationNodeDto node = NavigationNodeDto.of(NavigationType.aipeoppiaine, oppiaine.getNimi(), oppiaine.getId())
                .meta("vaiheId", vaiheId);
        if (!ObjectUtils.isEmpty(oppiaine.getOppimaarat())) {
            oppiaine.getOppimaarat().forEach(om -> node.add(mapOppimaara(om, vaiheId, oppiaine.getId())));
        } else if (!ObjectUtils.isEmpty(oppiaine.getKurssit())) {
            oppiaine.getKurssit().forEach(k -> node.add(mapKurssi(k, vaiheId, oppiaine.getId())));
        }
        return node;
    }

    private NavigationNodeDto mapOppimaara(AIPEOppiaineExportDto oppimaara, Long vaiheId, Long oppiaineId) {
        NavigationNodeDto node = NavigationNodeDto.of(NavigationType.aipeoppimaara, oppimaara.getNimi(), oppimaara.getId())
                .meta("vaiheId", vaiheId)
                .meta("oppiaineId", oppiaineId);
        if (!ObjectUtils.isEmpty(oppimaara.getKurssit())) {
            oppimaara.getKurssit().forEach(k -> node.add(mapKurssi(k, vaiheId, oppimaara.getId())));
        }
        return node;
    }

    private NavigationNodeDto mapKurssi(AIPEKurssiExportDto kurssi, Long vaiheId, Long oppiaineId) {
        return NavigationNodeDto.of(NavigationType.aipekurssi, kurssi.getNimi(), kurssi.getId())
                .meta("vaiheId", vaiheId)
                .meta("oppiaineId", oppiaineId);
    }
}
