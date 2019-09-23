/*
 *
 *  * Copyright (c) 2013 The Finnish Board of Education - Opetushallitus
 *  *
 *  * This program is free software: Licensed under the EUPL, Version 1.1 or - as
 *  * soon as they will be approved by the European Commission - subsequent versions
 *  * of the EUPL (the "Licence");
 *  *
 *  * You may not use this work except in compliance with the Licence.
 *  * You may obtain a copy of the Licence at: http://ec.europa.eu/idabc/eupl
 *  *
 *  * This program is distributed in the hope that it will be useful,
 *  * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  * European Union Public Licence for more details.
 *
 */

package fi.vm.sade.eperusteet.ylops.service.dokumentti.impl;

import fi.vm.sade.eperusteet.ylops.domain.dokumentti.Dokumentti;
import fi.vm.sade.eperusteet.ylops.dto.dokumentti.DokumenttiDto;
import fi.vm.sade.eperusteet.ylops.repository.dokumentti.DokumenttiRepository;
import fi.vm.sade.eperusteet.ylops.service.dokumentti.DokumenttiStateService;
import fi.vm.sade.eperusteet.ylops.service.mapping.DtoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author iSaul
 */
@Service
public class DokumenttiStateServiceImpl implements DokumenttiStateService {

    @Autowired
    private DokumenttiRepository dokumenttiRepository;

    @Autowired
    private DtoMapper mapper;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Dokumentti save(DokumenttiDto dto) {
        Dokumentti dokumentti = dokumenttiRepository.findOne(dto.getId());
        mapper.map(dto, dokumentti);
        return dokumenttiRepository.save(dokumentti);
    }

}
