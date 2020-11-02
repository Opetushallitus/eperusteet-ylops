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

import fi.vm.sade.eperusteet.ylops.domain.ReferenceableEntity;
import fi.vm.sade.eperusteet.ylops.dto.Reference;

import java.io.Serializable;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.metamodel.IdentifiableType;
import javax.persistence.metamodel.ManagedType;

import ma.glasnost.orika.ConverterException;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;
import org.springframework.stereotype.Component;

/**
 * @author teele1
 */
@Component
public class ReferenceableEntityConverter extends BidirectionalConverter<ReferenceableEntity, Reference> {

    @PersistenceContext
    private EntityManager em;

    @Override
    public boolean canConvert(Type<?> sourceType, Type<?> destinationType) {
        return (this.sourceType.isAssignableFrom(sourceType) && this.destinationType.isAssignableFrom(destinationType))
                || (this.sourceType.isAssignableFrom(destinationType) && this.destinationType.isAssignableFrom(sourceType));
    }

    @Override
    public Reference convertTo(ReferenceableEntity s, Type<Reference> type) {
        return Reference.of(s);
    }

    @Override
    public ReferenceableEntity convertFrom(Reference reference, Type<ReferenceableEntity> type) {
        ManagedType<ReferenceableEntity> managedType = em.getMetamodel().managedType(type.getRawType());
        if (managedType instanceof IdentifiableType) {
            final Class<?> idType = ((IdentifiableType<?>) managedType).getIdType().getJavaType();
            return em.getReference(type.getRawType(),
                    converters.getOrDefault(idType, ReferenceableEntityConverter::fail).apply(reference));
        }
        throw new ConverterException();
    }

    private static final Map<Class<?>, Function<Reference, Serializable>> converters;

    private static <T> T fail(Reference r) {
        throw new IllegalArgumentException("Tuntematon viitetyyppi");
    }

    static {
        converters = new IdentityHashMap<>();
        converters.put(Long.class, s -> Long.valueOf(s.getId()));
        converters.put(UUID.class, s -> UUID.fromString(s.getId()));
    }

}
