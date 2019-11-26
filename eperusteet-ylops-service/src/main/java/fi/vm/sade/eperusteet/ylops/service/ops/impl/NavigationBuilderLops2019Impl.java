package fi.vm.sade.eperusteet.ylops.service.ops.impl;

import com.google.common.collect.Sets;
import fi.vm.sade.eperusteet.ylops.domain.KoulutustyyppiToteutus;
import fi.vm.sade.eperusteet.ylops.dto.navigation.NavigationNodeDto;
import fi.vm.sade.eperusteet.ylops.service.ops.NavigationBuilder;
import fi.vm.sade.eperusteet.ylops.service.ops.OpsDispatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Component
@Transactional
public class NavigationBuilderLops2019Impl implements NavigationBuilder {

    @Autowired
    private OpsDispatcher dispatcher;

    @Override
    public Set<KoulutustyyppiToteutus> getTyypit() {
        return Sets.newHashSet(KoulutustyyppiToteutus.LOPS2019);
    }

    @Override
    public NavigationNodeDto buildNavigation(Long perusteId) {
        return dispatcher.get(NavigationBuilder.class).buildNavigation(perusteId);
    }
}
