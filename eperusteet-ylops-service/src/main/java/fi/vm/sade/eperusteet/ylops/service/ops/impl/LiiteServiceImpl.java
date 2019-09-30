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

import fi.vm.sade.eperusteet.ylops.domain.liite.Liite;
import fi.vm.sade.eperusteet.ylops.domain.ops.Opetussuunnitelma;
import fi.vm.sade.eperusteet.ylops.dto.liite.LiiteDto;
import fi.vm.sade.eperusteet.ylops.repository.liite.LiiteRepository;
import fi.vm.sade.eperusteet.ylops.repository.ops.OpetussuunnitelmaRepository;
import fi.vm.sade.eperusteet.ylops.service.exception.NotExistsException;
import fi.vm.sade.eperusteet.ylops.service.exception.ServiceException;
import fi.vm.sade.eperusteet.ylops.service.external.EperusteetService;
import fi.vm.sade.eperusteet.ylops.service.mapping.DtoMapper;
import fi.vm.sade.eperusteet.ylops.service.ops.LiiteService;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author jhyoty
 */
@Service
public class LiiteServiceImpl implements LiiteService {

    @Autowired
    private LiiteRepository liiteRepository;

    @Autowired
    private OpetussuunnitelmaRepository opetussuunnitelmat;

    @Autowired
    private EperusteetService eperusteetService;

    @Autowired
    DtoMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public void export(Long opsId, UUID id, OutputStream os) {
        InputStream is;

        // Opsin kuva
        Liite liite = liiteRepository.findOne(id);

        if (liite == null) {
            throw new NotExistsException("liite-ei-ole");
        }

        try {
            is = liite.getData().getBinaryStream();
        } catch (SQLException e) {
            throw new ServiceException("liite-blob-hakeminen-epaonnistui", e);
        }

        // Kopioidaan kuva bufferiin
        try {
            IOUtils.copy(is, os);
        } catch (IOException | NullPointerException e) {
            throw new ServiceException("liite-kopiointi-epaonnistui", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public InputStream export(Long opsId, UUID id, Long perusteId) {
        if (perusteId != null) {
            // Perusteen kuva
            byte[] liite = eperusteetService.getLiite(perusteId, id);
            if (liite.length > 0) {
                return new ByteArrayInputStream(liite);
            } else {
                throw new NotExistsException("liite-ei-ole");
            }
        } else {
            // Opsin kuva
            Liite liite = liiteRepository.findOne(id);

            if (liite == null) {
                throw new NotExistsException("liite-ei-ole");
            }

            try {
                return liite.getData().getBinaryStream();
            } catch (SQLException e) {
                throw new ServiceException("liite-blob-hakeminen-epaonnistui", e);
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public LiiteDto get(Long opsId, UUID id) {
        Liite liite = liiteRepository.findOne(id);
        //TODO. tarkasta että liite liittyy pyydettyyn suunnitelmaan tai johonkin sen esivanhempaan
        return mapper.map(liite, LiiteDto.class);
    }

    @Override
    @Transactional
    public UUID add(Long opsId, String tyyppi, String nimi, long length, InputStream is) {
        Liite liite = liiteRepository.add(tyyppi, nimi, length, is);
        Opetussuunnitelma ops = opetussuunnitelmat.findOne(opsId);
        ops.attachLiite(liite);
        return liite.getId();
    }

    @Override
    @Transactional(readOnly = true)
    public List<LiiteDto> getAll(Long opsId) {
        List<Liite> liitteet = this.liiteRepository.findByOpsId(opsId);
        return mapper.mapAsList(liitteet, LiiteDto.class);
    }

    @Override
    @Transactional
    public void delete(Long opsId, UUID id) {
        Liite liite = liiteRepository.findOne(opsId, id);
        if (liite == null) {
            throw new NotExistsException("Liitettä ei ole");
        }
        opetussuunnitelmat.findOne(opsId).removeLiite(liite);
    }

}
