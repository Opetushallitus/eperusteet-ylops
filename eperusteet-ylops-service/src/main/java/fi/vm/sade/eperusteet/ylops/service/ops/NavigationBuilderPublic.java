package fi.vm.sade.eperusteet.ylops.service.ops;

import fi.vm.sade.eperusteet.ylops.domain.teksti.TekstiKappaleViite;
import java.util.function.Predicate;

public interface NavigationBuilderPublic extends NavigationBuilder {
    @Override
    default Class getImpl() {
        return NavigationBuilderPublic.class;
    }

    default Predicate<TekstiKappaleViite> tekstikappaleFilter() {
        return tkv -> !tkv.isPiilotettu();
    }
}
