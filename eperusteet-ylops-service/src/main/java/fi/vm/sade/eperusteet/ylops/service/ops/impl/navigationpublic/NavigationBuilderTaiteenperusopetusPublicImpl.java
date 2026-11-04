package fi.vm.sade.eperusteet.ylops.service.ops.impl.navigationpublic;

import com.google.common.collect.Sets;
import fi.vm.sade.eperusteet.ylops.domain.KoulutustyyppiToteutus;
import fi.vm.sade.eperusteet.ylops.dto.export.OpetussuunnitelmaExportTpoDto;
import fi.vm.sade.eperusteet.ylops.dto.navigation.NavigationNodeDto;
import fi.vm.sade.eperusteet.ylops.dto.navigation.NavigationType;
import fi.vm.sade.eperusteet.ylops.dto.tpo.export.TaiteenalaExportDto;
import fi.vm.sade.eperusteet.ylops.dto.tpo.export.TaiteenalanVaiheExportDto;
import fi.vm.sade.eperusteet.ylops.dto.tpo.export.TpoSisaltoExportDto;
import fi.vm.sade.eperusteet.ylops.service.ops.NavigationBuilderPublic;
import fi.vm.sade.eperusteet.ylops.service.ops.OpetussuunnitelmaService;
import fi.vm.sade.eperusteet.ylops.service.ops.OpsDispatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Transactional
public class NavigationBuilderTaiteenperusopetusPublicImpl implements NavigationBuilderPublic {

    @Autowired
    private OpsDispatcher dispatcher;

    @Autowired
    private OpetussuunnitelmaService opetussuunnitelmaService;

    @Override
    public Set<KoulutustyyppiToteutus> getTyypit() {
        return Sets.newHashSet(KoulutustyyppiToteutus.TPO);
    }

    @Override
    public NavigationNodeDto buildNavigation(Long opsId, Integer revision) {
        OpetussuunnitelmaExportTpoDto export = (OpetussuunnitelmaExportTpoDto) opetussuunnitelmaService
                .getOpetussuunnitelmaJulkaistuSisalto(opsId, revision);
        TpoSisaltoExportDto sisalto = export.getTpo() != null ? export.getTpo() : new TpoSisaltoExportDto();

        return dispatcher.get(NavigationBuilderPublic.class).buildNavigation(opsId, revision)
                .addAll(taiteenalat(sisalto));
    }

    private List<NavigationNodeDto> taiteenalat(TpoSisaltoExportDto sisalto) {
        return sisalto.getTaiteenalat().stream()
                .map(taiteenala -> mapTaiteenala(taiteenala))
                .collect(Collectors.toList());
    }

    private NavigationNodeDto mapTaiteenala(TaiteenalaExportDto taiteenala) {
        NavigationNodeDto node = NavigationNodeDto.of(NavigationType.taiteenala, taiteenala.getNimi(), taiteenala.getId());
        taiteenala.getVaiheet().forEach(vaihe -> node.add(mapVaihe(vaihe, taiteenala.getId())));
        return node;
    }

    private NavigationNodeDto mapVaihe(TaiteenalanVaiheExportDto vaihe, Long taiteenalaId) {
        NavigationNodeDto node = NavigationNodeDto.of(NavigationType.taiteenalanvaihe, vaihe.getNimi(), vaihe.getId())
                .meta("taiteenalaId", taiteenalaId);
        vaihe.getOpintokokonaisuudet().forEach(ok -> node.add(
                NavigationNodeDto.of(NavigationType.taiteenopintokokonaisuus, ok.getNimi(), ok.getId())
                        .meta("taiteenalaId", taiteenalaId)
                        .meta("vaiheId", vaihe.getId())));
        return node;
    }
}
