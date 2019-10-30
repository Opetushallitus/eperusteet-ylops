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
package fi.vm.sade.eperusteet.ylops.service.teksti;

import fi.vm.sade.eperusteet.ylops.domain.Tila;
import fi.vm.sade.eperusteet.ylops.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.ylops.domain.teksti.TekstiKappale;
import fi.vm.sade.eperusteet.ylops.dto.teksti.TekstiKappaleDto;
import fi.vm.sade.eperusteet.ylops.repository.teksti.TekstiKappaleRepository;
import fi.vm.sade.eperusteet.ylops.repository.teksti.TekstikappaleviiteRepository;
import fi.vm.sade.eperusteet.ylops.service.ops.TekstiKappaleViiteService;
import fi.vm.sade.eperusteet.ylops.service.teksti.TekstiKappaleService;
import fi.vm.sade.eperusteet.ylops.test.AbstractIntegrationTest;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static fi.vm.sade.eperusteet.ylops.test.util.TestUtils.lokalisoituTekstiOf;

/**
 * @author mikkom
 */
@Transactional
public class TekstiKappaleServiceIT extends AbstractIntegrationTest {

    @Autowired
    private TekstiKappaleService tekstiKappaleService;

    @Autowired
    private TekstiKappaleRepository tekstiKappaleRepository;

    @Test
    public void testGet() {
        TekstiKappale tekstiKappale = new TekstiKappale();
        final String NIMI = "Namnet";
        final String TEKSTI = "Teksten";
        tekstiKappale.setNimi(lokalisoituTekstiOf(Kieli.SV, NIMI));
        tekstiKappale.setTeksti(lokalisoituTekstiOf(Kieli.SV, TEKSTI));
        tekstiKappale.setTila(Tila.LUONNOS);

        tekstiKappale = tekstiKappaleRepository.save(tekstiKappale);

        TekstiKappaleDto dto = tekstiKappaleService.get(tekstiKappale.getId());
        Assert.assertNotNull(dto);

        Assert.assertEquals(NIMI, dto.getNimi().get(Kieli.SV));
        Assert.assertEquals(TEKSTI, dto.getTeksti().get(Kieli.SV));
        Assert.assertEquals(tekstiKappale.getTila(), dto.getTila());
    }
}
