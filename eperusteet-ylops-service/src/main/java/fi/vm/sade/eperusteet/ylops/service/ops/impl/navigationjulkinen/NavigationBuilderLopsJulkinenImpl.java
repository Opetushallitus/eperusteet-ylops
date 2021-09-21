package fi.vm.sade.eperusteet.ylops.service.ops.impl.navigationjulkinen;

import fi.vm.sade.eperusteet.ylops.service.ops.NavigationBuilder;
import fi.vm.sade.eperusteet.ylops.service.ops.NavigationBuilderJulkinen;
import fi.vm.sade.eperusteet.ylops.service.ops.impl.navigation.NavigationBuilderLopsImpl;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class NavigationBuilderLopsJulkinenImpl extends NavigationBuilderLopsImpl implements NavigationBuilderJulkinen {

    @Override
    public Class<? extends NavigationBuilder> getNavigationBuilderClass() {
        return NavigationBuilderJulkinen.class;
    }
}
