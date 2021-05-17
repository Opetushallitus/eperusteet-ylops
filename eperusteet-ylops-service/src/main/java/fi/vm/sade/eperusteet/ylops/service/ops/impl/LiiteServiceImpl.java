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
import fi.vm.sade.eperusteet.ylops.dto.peruste.PerusteDto;
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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;

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
    public void exportLiitePerusteelta(Long opsId, UUID id, OutputStream os) {
        Opetussuunnitelma ops = opetussuunnitelmat.findOne(opsId);
        PerusteDto perusteDto = eperusteetService.getPeruste(ops.getPerusteenDiaarinumero());
        InputStream is = exportLiitePerusteelta(opsId, id, perusteDto.getId());

        // Kopioidaan kuva bufferiin
        try {
            IOUtils.copy(is, os);
        } catch (IOException | NullPointerException e) {
            throw new ServiceException("liite-kopiointi-epaonnistui", e);
        }
    }

    @Override
    @Transactional(readOnly = true, noRollbackFor = NotExistsException.class)
    public InputStream export(Long opsId, UUID id, Long perusteId) {
        // Opsin kuva
        Liite liite = liiteRepository.findOne(id);

        if (liite == null) {
            return exportLiitePerusteelta(opsId, id, perusteId);
        }

        try {
            return liite.getData().getBinaryStream();
        } catch (SQLException e) {
            throw new ServiceException("liite-blob-hakeminen-epaonnistui", e);
        }
    }

    @Override
    @Transactional(readOnly = true, noRollbackFor = NotExistsException.class)
    public InputStream exportLiitePerusteelta(Long opsId, UUID id, Long perusteId) {
        try {
            byte[] liite = eperusteetService.getLiite(perusteId, id);
            if (liite.length > 0) {
                return new ByteArrayInputStream(liite);
            } else {
                throw new NotExistsException("liite-ei-ole");
            }
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
                throw new NotExistsException("liite-ei-ole");
            }

            throw e;
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
        Opetussuunnitelma ops = opetussuunnitelmat.findOne(opsId);
        List<Liite> liitteet = getLiitteetDeep(ops);
        return mapper.mapAsList(liitteet, LiiteDto.class);
    }

    private List<Liite> getLiitteetDeep(Opetussuunnitelma ops) {
        List<Liite> liitteet = new ArrayList<>(this.liiteRepository.findByOpsId(ops.getId()));

        // Rekursiivisesti
        Opetussuunnitelma parent = ops.getPohja();
        if (parent != null) {
            liitteet.addAll(getLiitteetDeep(parent));
        }

        return liitteet;
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
