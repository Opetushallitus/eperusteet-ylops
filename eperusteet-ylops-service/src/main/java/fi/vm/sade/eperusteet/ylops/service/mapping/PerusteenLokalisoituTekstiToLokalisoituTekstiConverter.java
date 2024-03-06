package fi.vm.sade.eperusteet.ylops.service.mapping;

import fi.vm.sade.eperusteet.ylops.dto.teksti.LokalisoituTekstiDto;
import fi.vm.sade.eperusteet.ylops.service.external.impl.perustedto.PerusteenLokalisoituTekstiDto;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;
import org.springframework.stereotype.Component;

@Component
public class PerusteenLokalisoituTekstiToLokalisoituTekstiConverter extends BidirectionalConverter<LokalisoituTekstiDto, PerusteenLokalisoituTekstiDto> {

    @Override
    public PerusteenLokalisoituTekstiDto convertTo(LokalisoituTekstiDto lokalisoituTekstiDto, Type<PerusteenLokalisoituTekstiDto> type, MappingContext mappingContext) {
        return new PerusteenLokalisoituTekstiDto(lokalisoituTekstiDto.getId(), lokalisoituTekstiDto.getTunniste(), lokalisoituTekstiDto.getTekstit());
    }

    @Override
    public LokalisoituTekstiDto convertFrom(PerusteenLokalisoituTekstiDto perusteenLokalisoituTekstiDto, Type<LokalisoituTekstiDto> type, MappingContext mappingContext) {
        return new LokalisoituTekstiDto(null, perusteenLokalisoituTekstiDto.getTunniste(), perusteenLokalisoituTekstiDto.getTekstit());
    }
}
