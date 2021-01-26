package fi.vm.sade.eperusteet.ylops.service.ops.impl;

import fi.vm.sade.eperusteet.ylops.service.ops.NavigationBuilder;
import fi.vm.sade.eperusteet.ylops.service.ops.NavigationBuilderPublic;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class NavigationBuilderPerusopetusPublicImpl extends NavigationBuilderPerusopetusImpl implements NavigationBuilderPublic {

    @Override
    public Class<? extends NavigationBuilder> getNavigationBuilderClass() {
        return NavigationBuilderPublic.class;
    }
}
