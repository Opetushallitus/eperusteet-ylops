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

package fi.vm.sade.eperusteet.ylops.service.ops.impl;

import fi.vm.sade.eperusteet.ylops.service.ops.TermistoService;
import fi.vm.sade.eperusteet.ylops.domain.ops.Opetussuunnitelma;
import fi.vm.sade.eperusteet.ylops.domain.Termi;
import fi.vm.sade.eperusteet.ylops.dto.ops.TermiDto;
import fi.vm.sade.eperusteet.ylops.repository.ops.OpetussuunnitelmaRepository;
import fi.vm.sade.eperusteet.ylops.repository.ops.TermistoRepository;
import fi.vm.sade.eperusteet.ylops.service.exception.NotExistsException;
import fi.vm.sade.eperusteet.ylops.service.mapping.DtoMapper;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author apvilkko
 */
@Service
@Transactional
public class TermistoServiceImpl implements TermistoService {

    @Autowired
    private DtoMapper mapper;

    @Autowired
    TermistoRepository termisto;

    @Autowired
    OpetussuunnitelmaRepository opsit;

    @Override
    @Transactional(readOnly = true)
    public List<TermiDto> getTermit(Long opsId) {
        List<Termi> termit = termisto.findByOpsId(opsId);
        return mapper.mapAsList(termit, TermiDto.class);
    }

    @Override
    @Transactional(readOnly = true)
    public TermiDto getTermi(Long opsId, String avain) {
        Opetussuunnitelma ops = opsit.findOne(opsId);
        Termi termi = termisto.findOneByOpsAndAvain(ops, avain);
        return mapper.map(termi, TermiDto.class);
    }

    @Override
    public TermiDto addTermi(Long opsId, TermiDto dto) {
        Opetussuunnitelma ops = opsit.findOne(opsId);
        assertExists(ops, "Opetussuunnitelmaa ei ole olemassa");
        Termi tmp = mapper.map(dto, Termi.class);
        tmp.setOps(ops);
        tmp = termisto.save(tmp);
        return mapper.map(tmp, TermiDto.class);
    }

    @Override
    public TermiDto updateTermi(Long opsId, TermiDto dto) {
        Opetussuunnitelma ops = opsit.findOne(opsId);
        assertExists(ops, "Opetussuunnitelmaa ei ole olemassa");
        Termi current = termisto.findOne(dto.getId());
        assertExists(current, "Päivitettävää tietoa ei ole olemassa");
        mapper.map(dto, current);
        termisto.save(current);
        return mapper.map(current, TermiDto.class);
    }

    @Override
    public void deleteTermi(Long opsId, Long id) {
        Termi termi = termisto.findOne(id);
        termisto.delete(termi);
    }

    private static void assertExists(Object o, String msg) {
        if (o == null) {
            throw new NotExistsException(msg);
        }
    }
}
