package fi.vm.sade.eperusteet.ylops.service.ops.impl.navigationjulkinen;

import fi.vm.sade.eperusteet.ylops.service.ops.NavigationBuilderJulkinen;
import fi.vm.sade.eperusteet.ylops.service.ops.impl.navigation.NavigationBuilderDefaultImpl;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class NavigationBuilderDefaultJulkinenImpl extends NavigationBuilderDefaultImpl implements NavigationBuilderJulkinen {

}
