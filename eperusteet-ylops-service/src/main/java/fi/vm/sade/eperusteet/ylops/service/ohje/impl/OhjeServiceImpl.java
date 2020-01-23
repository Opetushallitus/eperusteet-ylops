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
package fi.vm.sade.eperusteet.ylops.service.ohje.impl;

import fi.vm.sade.eperusteet.ylops.domain.ohje.Ohje;
import fi.vm.sade.eperusteet.ylops.domain.ohje.OhjeTyyppi;
import fi.vm.sade.eperusteet.ylops.dto.ohje.OhjeDto;
import fi.vm.sade.eperusteet.ylops.repository.ohje.OhjeRepository;
import fi.vm.sade.eperusteet.ylops.service.exception.BusinessRuleViolationException;
import fi.vm.sade.eperusteet.ylops.service.mapping.DtoMapper;
import fi.vm.sade.eperusteet.ylops.service.ohje.OhjeService;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.method.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static fi.vm.sade.eperusteet.ylops.service.util.Nulls.assertExists;

/**
 * @author mikkom
 */
@Service
@Transactional
public class OhjeServiceImpl implements OhjeService {

    @Autowired
    private DtoMapper mapper;

    @Autowired
    private OhjeRepository repository;

    @Override
    @Transactional(readOnly = true)
    public OhjeDto getOhje(Long id) {
        Ohje ohje = repository.findOne(id);
        assertExists(ohje, "Pyydettyä ohjetta ei ole olemassa");
        return mapper.map(ohje, OhjeDto.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OhjeDto> getTekstiKappaleOhjeet(UUID uuid) {
        List<Ohje> ohjeet = repository.findByKohde(uuid);
        return mapper.mapAsList(ohjeet, OhjeDto.class);
    }

    @Override
    public OhjeDto addOhje(OhjeDto ohjeDto) {
        if (ohjeDto.getTyyppi() == null) {
            ohjeDto.setTyyppi(OhjeTyyppi.PERUSTETEKSTI);
        }

        if (ohjeDto.getKohde() == null) {
            throw new BusinessRuleViolationException("kohdetta-ei-asetettu");
        }
        Ohje ohje = mapper.map(ohjeDto, Ohje.class);
        ohje = repository.save(ohje);
        return mapper.map(ohje, OhjeDto.class);
    }

    @Override
    public OhjeDto updateOhje(OhjeDto ohjeDto) {
        Ohje ohje = repository.findOne(ohjeDto.getId());
        assertExists(ohje, "Päivitettävää ohjetta ei ole olemassa");
        mapper.map(ohjeDto, ohje);
        ohje = repository.save(ohje);
        return mapper.map(ohje, OhjeDto.class);
    }

    @Override
    public void removeOhje(Long id) {
        Ohje ohje = repository.findOne(id);
        repository.delete(ohje);
    }

}
