package fi.vm.sade.eperusteet.ylops.service.ops.impl;

import fi.vm.sade.eperusteet.ylops.domain.KoulutustyyppiToteutus;
import fi.vm.sade.eperusteet.ylops.domain.ops.Opetussuunnitelma;
import fi.vm.sade.eperusteet.ylops.domain.teksti.TekstiKappaleViite;
import fi.vm.sade.eperusteet.ylops.dto.navigation.NavigationNodeDto;
import fi.vm.sade.eperusteet.ylops.dto.navigation.NavigationType;
import fi.vm.sade.eperusteet.ylops.dto.teksti.LokalisoituTekstiDto;
import fi.vm.sade.eperusteet.ylops.repository.ops.OpetussuunnitelmaRepository;
import fi.vm.sade.eperusteet.ylops.service.mapping.DtoMapper;
import fi.vm.sade.eperusteet.ylops.service.ops.NavigationBuilder;
import fi.vm.sade.eperusteet.ylops.service.ops.TekstiKappaleViiteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Transactional
public class NavigationBuilderDefaultImpl implements NavigationBuilder {

    @Autowired
    private DtoMapper mapper;

    @Autowired
    private OpetussuunnitelmaRepository opetussuunnitelmaRepository;

    @Autowired
    private TekstiKappaleViiteService tekstiKappaleViiteService;

    @Override
    public Set<KoulutustyyppiToteutus> getTyypit() {
        return Collections.emptySet();
    }

    public NavigationNodeDto buildTekstinavi(TekstiKappaleViite root) {
        LokalisoituTekstiDto nimi = root.getTekstiKappale() != null
                ? mapper.map(root.getTekstiKappale().getNimi(), LokalisoituTekstiDto.class)
                : null;
        return NavigationNodeDto
                .of(NavigationType.viite, nimi, root.getId())
                .addAll(Optional.ofNullable(root.getLapset())
                    .map(lapset -> lapset.stream()
                            .map(this::buildTekstinavi)
                            .collect(Collectors.toList()))
                    .orElse(new ArrayList<>()));
    }

    @Override
    public NavigationNodeDto buildNavigation(Long perusteId) {
        Opetussuunnitelma ops = opetussuunnitelmaRepository.getOne(perusteId);
        return NavigationNodeDto.of(NavigationType.root)
                .addAll(buildTekstinavi(ops.getTekstit()).getChildren());
    }
}
