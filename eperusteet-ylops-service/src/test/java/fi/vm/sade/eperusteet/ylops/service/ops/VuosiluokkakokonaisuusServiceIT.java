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
package fi.vm.sade.eperusteet.ylops.service.ops;

import fi.vm.sade.eperusteet.ylops.domain.Vuosiluokka;
import fi.vm.sade.eperusteet.ylops.domain.Vuosiluokkakokonaisuusviite;
import fi.vm.sade.eperusteet.ylops.domain.ops.Opetussuunnitelma;
import fi.vm.sade.eperusteet.ylops.dto.Reference;
import fi.vm.sade.eperusteet.ylops.dto.ops.VuosiluokkakokonaisuusDto;
import fi.vm.sade.eperusteet.ylops.repository.ops.OpetussuunnitelmaRepository;
import fi.vm.sade.eperusteet.ylops.repository.ops.VuosiluokkakokonaisuusviiteRepository;
import fi.vm.sade.eperusteet.ylops.service.ops.VuosiluokkakokonaisuusService;
import fi.vm.sade.eperusteet.ylops.service.util.SecurityUtil;
import fi.vm.sade.eperusteet.ylops.test.AbstractIntegrationTest;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Optional;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;

/**
 * @author hyoty
 */
@DirtiesContext
public class VuosiluokkakokonaisuusServiceIT extends AbstractIntegrationTest {

    @Autowired
    private OpetussuunnitelmaRepository suunnitelmat;

    @Autowired
    private VuosiluokkakokonaisuusviiteRepository viitteet;

    @Autowired
    private VuosiluokkakokonaisuusService service;

    private Long opsId;
    private Reference viite1Ref;
    private Reference viite2Ref;

    @Before
    public void setup() {
        Vuosiluokkakokonaisuusviite viite = new Vuosiluokkakokonaisuusviite(UUID.randomUUID(), EnumSet.of(Vuosiluokka.VUOSILUOKKA_1, Vuosiluokka.VUOSILUOKKA_2));
        this.viite1Ref = Reference.of(viitteet.save(viite));
        viite = new Vuosiluokkakokonaisuusviite(UUID.randomUUID(), EnumSet.of(Vuosiluokka.VUOSILUOKKA_3, Vuosiluokka.VUOSILUOKKA_4, Vuosiluokka.VUOSILUOKKA_5, Vuosiluokka.VUOSILUOKKA_6));
        this.viite2Ref = Reference.of(viitteet.save(viite));
        Opetussuunnitelma ops = new Opetussuunnitelma();
        ops.setOrganisaatiot(Collections.singleton(SecurityUtil.OPH_OID));
        ops = suunnitelmat.save(ops);
        opsId = ops.getId();
    }

    @Test
    public void crudTest() {

        VuosiluokkakokonaisuusDto dto = new VuosiluokkakokonaisuusDto();
        dto.setTunniste(Optional.of(viite1Ref));

        dto = service.add(opsId, dto);
        dto = service.get(opsId, dto.getId()).getVuosiluokkakokonaisuus();
        dto.setTunniste(Optional.of(viite2Ref));
        dto = service.update(opsId, dto).getVuosiluokkakokonaisuus();
        service.delete(opsId, dto.getId());
    }

}
