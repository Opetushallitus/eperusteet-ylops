package fi.vm.sade.eperusteet.ylops.service.mapping;

import fi.vm.sade.eperusteet.ylops.domain.ReferenceableEntity;
import fi.vm.sade.eperusteet.ylops.dto.Reference;
import ma.glasnost.orika.ConverterException;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;
import org.springframework.stereotype.Component;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.metamodel.IdentifiableType;
import jakarta.persistence.metamodel.ManagedType;
import java.io.Serializable;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

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
    public Reference convertTo(ReferenceableEntity s, Type<Reference> type, MappingContext mappingContext) {
        return Reference.of(s);
    }

    @Override
    public ReferenceableEntity convertFrom(Reference reference, Type<ReferenceableEntity> type, MappingContext mappingContext) {
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
