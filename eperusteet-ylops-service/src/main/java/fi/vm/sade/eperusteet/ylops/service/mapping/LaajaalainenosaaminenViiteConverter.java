/*
 * Copyright (c) 2013 The Finnish Board of Education - Opetushallitus
 *
 * This program is free software: Licensed under the EUPL, Version 1.1 or - as
 * soon as they will be approved by the European Commission - subsequent versions
 * of the EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at: http://ec.europa.eu/idabc/eupl
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * European Union Public Licence for more details.
 */
package fi.vm.sade.eperusteet.ylops.service.mapping;

import fi.vm.sade.eperusteet.ylops.domain.LaajaalainenosaaminenViite;
import fi.vm.sade.eperusteet.ylops.dto.Reference;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

/**
 * @author hyoty
 */
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
