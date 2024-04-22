package fi.vm.sade.eperusteet.ylops.service.mapping;

import fi.vm.sade.eperusteet.ylops.domain.koodisto.KoodistoKoodi;
import fi.vm.sade.eperusteet.ylops.dto.koodisto.KoodistoDto;
import fi.vm.sade.eperusteet.ylops.repository.koodisto.KoodistoKoodiRepository;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class KoodistoKoodiConverter extends BidirectionalConverter<KoodistoKoodi, KoodistoDto> {

    @Autowired
    private KoodistoKoodiRepository repository;

    @Override
    public KoodistoDto convertTo(KoodistoKoodi source, Type<KoodistoDto> destinationType, MappingContext mappingContext) {
        return new KoodistoDto(source.getId(), source.getKoodiUri(), source.getKoodiArvo());
    }

    @Override
    public KoodistoKoodi convertFrom(KoodistoDto source, Type<KoodistoKoodi> destinationType, MappingContext mappingContext) {
        if (source.getId() != null) {
            return repository.findOne(source.getId());
        }

        return repository.findByKoodiUri(source.getKoodiUri())
                .orElse(new KoodistoKoodi(source.getKoodiUri(), source.getKoodiArvo()));
    }
}
