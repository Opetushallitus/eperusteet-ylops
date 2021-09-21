package fi.vm.sade.eperusteet.ylops.service.ops;

public interface NavigationBuilderPublic extends NavigationBuilder {
    @Override
    default Class getImpl() {
        return NavigationBuilderPublic.class;
    }
    
}
