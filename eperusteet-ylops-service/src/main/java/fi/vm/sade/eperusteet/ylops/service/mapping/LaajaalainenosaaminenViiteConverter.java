package fi.vm.sade.eperusteet.ylops.service.mapping;

import fi.vm.sade.eperusteet.ylops.domain.LaajaalainenosaaminenViite;
import fi.vm.sade.eperusteet.ylops.dto.Reference;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

public class LaajaalainenosaaminenViiteConverter extends BidirectionalConverter<Reference, LaajaalainenosaaminenViite> {

    @Override
    public Reference convertFrom(LaajaalainenosaaminenViite source, Type<Reference> destinationType, MappingContext mappingContext) {
        return new Reference(source.getViite());
    }

    @Override
    public LaajaalainenosaaminenViite convertTo(Reference source, Type<LaajaalainenosaaminenViite> destinationType, MappingContext mappingContext) {
        return new LaajaalainenosaaminenViite(source.toString());
    }

}
