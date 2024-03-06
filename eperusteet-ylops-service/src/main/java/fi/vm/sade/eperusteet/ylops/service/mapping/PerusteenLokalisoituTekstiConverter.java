package fi.vm.sade.eperusteet.ylops.service.mapping;

import fi.vm.sade.eperusteet.ylops.domain.teksti.LokalisoituTeksti;
import fi.vm.sade.eperusteet.ylops.service.external.impl.perustedto.PerusteenLokalisoituTekstiDto;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;
import org.springframework.stereotype.Component;

@Component
public class PerusteenLokalisoituTekstiConverter extends BidirectionalConverter<LokalisoituTeksti, PerusteenLokalisoituTekstiDto> {

    @Override
    public PerusteenLokalisoituTekstiDto convertTo(LokalisoituTeksti tekstiPalanen, Type<PerusteenLokalisoituTekstiDto> type, MappingContext mappingContext) {
        return new PerusteenLokalisoituTekstiDto(tekstiPalanen.getId(), tekstiPalanen.getTunniste(), tekstiPalanen.getTeksti());
    }

    @Override
    public LokalisoituTeksti convertFrom(PerusteenLokalisoituTekstiDto dto, Type<LokalisoituTeksti> type, MappingContext mappingContext) {
        return LokalisoituTeksti.of(dto.getTekstit());
    }
}
