package fi.vm.sade.eperusteet.ylops.service.ops.impl.navigationpublic;

import com.google.common.collect.Sets;
import fi.vm.sade.eperusteet.ylops.domain.KoulutustyyppiToteutus;
import fi.vm.sade.eperusteet.ylops.dto.navigation.NavigationNodeDto;
import fi.vm.sade.eperusteet.ylops.dto.navigation.NavigationType;
import fi.vm.sade.eperusteet.ylops.dto.peruste.TPOOpetuksenSisaltoDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.tpo.PerusteTaiteenosaDto;
import fi.vm.sade.eperusteet.ylops.dto.teksti.LokalisoituTekstiDto;
import fi.vm.sade.eperusteet.ylops.dto.tpo.TaiteenalaDto;
import fi.vm.sade.eperusteet.ylops.dto.tpo.TaiteenosaDto;
import fi.vm.sade.eperusteet.ylops.dto.tpo.export.OpetussuunnitelmaExportTpoDto;
import fi.vm.sade.eperusteet.ylops.service.mapping.DtoMapper;
import fi.vm.sade.eperusteet.ylops.service.ops.NavigationBuilderPublic;
import fi.vm.sade.eperusteet.ylops.service.ops.OpetussuunnitelmaService;
import fi.vm.sade.eperusteet.ylops.service.ops.OpsDispatcher;
import fi.vm.sade.eperusteet.ylops.service.tpo.TaiteenperusopetusService;
import fi.vm.sade.eperusteet.ylops.service.external.impl.perustedto.PerusteenLokalisoituTekstiDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@Transactional
public class NavigationBuilderTaiteenperusopetusPublicImpl implements NavigationBuilderPublic {

    @Autowired
    protected OpsDispatcher dispatcher;

    @Autowired
    private OpetussuunnitelmaService opetussuunnitelmaService;

    @Autowired
    private TaiteenperusopetusService taiteenperusopetusService;

    @Autowired
    private DtoMapper mapper;

    @Override
    public Set<KoulutustyyppiToteutus> getTyypit() {
        return Sets.newHashSet(KoulutustyyppiToteutus.TPO);
    }

    @Override
    public NavigationNodeDto buildNavigation(Long opsId, Integer revision) {
        return dispatcher.get(NavigationBuilderPublic.class).buildNavigation(opsId, revision)
                .addAll(taiteenalat(opsId, revision));
    }

    private List<NavigationNodeDto> taiteenalat(Long opsId, Integer revision) {
        OpetussuunnitelmaExportTpoDto opetussuunnitelma = (OpetussuunnitelmaExportTpoDto)
                opetussuunnitelmaService.getOpetussuunnitelmaJulkaistuSisalto(opsId, revision);

        if (opetussuunnitelma.getTaiteenalat().isEmpty()) {
            return Collections.emptyList();
        }

        Map<Long, PerusteTaiteenosaDto> perusteenTaiteenosat = perusteenTaiteenosatIdeittain(opsId);
        return opetussuunnitelma.getTaiteenalat().stream()
                .map(taiteenala -> taiteenala(taiteenala, perusteenTaiteenosat))
                .collect(Collectors.toList());
    }

    private NavigationNodeDto taiteenala(TaiteenalaDto taiteenala, Map<Long, PerusteTaiteenosaDto> perusteenTaiteenosat) {
        return NavigationNodeDto
                .of(NavigationType.taiteenala, taiteenala.getNimi(), taiteenala.getId())
                .addAll(taiteenala.getTaiteenosat().stream()
                        .map(taiteenosa -> taiteenosa(taiteenala, taiteenosa,
                                perusteenTaiteenosat.get(taiteenosa.getPerusteenTaiteenosanId())))
                        .collect(Collectors.toList()));
    }

    private NavigationNodeDto taiteenosa(TaiteenalaDto taiteenala, TaiteenosaDto taiteenosa, PerusteTaiteenosaDto perusteenTaiteenosa) {
        return NavigationNodeDto
                .of(NavigationType.taiteenosa, nimi(perusteenTaiteenosa), taiteenosa.getId())
                .meta("taiteenalaId", taiteenala.getId());
    }

    private Map<Long, PerusteTaiteenosaDto> perusteenTaiteenosatIdeittain(Long opsId) {
        TPOOpetuksenSisaltoDto perusteenSisalto = taiteenperusopetusService.getPerusteSisalto(opsId);
        if (perusteenSisalto == null) {
            return Collections.emptyMap();
        }

        return perusteenSisalto.getTaiteenalat().stream()
                .flatMap(taiteenala -> taiteenala.getTaiteenOsat().stream())
                .filter(taiteenosa -> taiteenosa.getId() != null)
                .collect(Collectors.toMap(PerusteTaiteenosaDto::getId, Function.identity(), (a, b) -> a));
    }

    private LokalisoituTekstiDto nimi(PerusteTaiteenosaDto perusteenTaiteenosa) {
        if (perusteenTaiteenosa == null) {
            return null;
        }
        PerusteenLokalisoituTekstiDto nimi = perusteenTaiteenosa.getNimi();
        return nimi != null ? mapper.map(nimi, LokalisoituTekstiDto.class) : null;
    }
}
