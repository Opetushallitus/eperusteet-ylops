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

import fi.vm.sade.eperusteet.ylops.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.ylops.domain.teksti.LokalisoituTeksti;
import fi.vm.sade.eperusteet.ylops.repository.teksti.LokalisoituTekstiRepository;
import fi.vm.sade.eperusteet.ylops.service.external.impl.perustedto.PerusteenLokalisoituTekstiDto;
import java.util.Map;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author jhyoty
 */
@Component
public class PerusteenLokalisoituTekstiConverter extends BidirectionalConverter<LokalisoituTeksti, PerusteenLokalisoituTekstiDto> {

    @Override
    public PerusteenLokalisoituTekstiDto convertTo(LokalisoituTeksti tekstiPalanen, Type<PerusteenLokalisoituTekstiDto> type) {
        return new PerusteenLokalisoituTekstiDto(tekstiPalanen.getId(), tekstiPalanen.getTunniste(), tekstiPalanen.getTeksti());
    }

    @Override
    public LokalisoituTeksti convertFrom(PerusteenLokalisoituTekstiDto dto, Type<LokalisoituTeksti> type) {
        return LokalisoituTeksti.of(dto.getTekstit());
    }
}
