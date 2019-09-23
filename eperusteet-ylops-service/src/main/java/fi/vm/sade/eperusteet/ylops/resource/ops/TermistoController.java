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
package fi.vm.sade.eperusteet.ylops.resource.ops;

import fi.vm.sade.eperusteet.ylops.dto.ops.TermiDto;
import fi.vm.sade.eperusteet.ylops.resource.config.InternalApi;
import fi.vm.sade.eperusteet.ylops.service.audit.EperusteetYlopsAudit;

import static fi.vm.sade.eperusteet.ylops.service.audit.EperusteetYlopsMessageFields.TERMI;
import static fi.vm.sade.eperusteet.ylops.service.audit.EperusteetYlopsOperation.LISAYS;
import static fi.vm.sade.eperusteet.ylops.service.audit.EperusteetYlopsOperation.MUOKKAUS;
import static fi.vm.sade.eperusteet.ylops.service.audit.EperusteetYlopsOperation.POISTO;

import fi.vm.sade.eperusteet.ylops.service.audit.LogMessage;
import fi.vm.sade.eperusteet.ylops.service.ops.TermistoService;

import java.util.List;

import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author apvilkko
 */
@RestController
@RequestMapping("/opetussuunnitelmat/{opsId}")
@InternalApi
@Api(value = "Termisto")
public class TermistoController {
    @Autowired
    private EperusteetYlopsAudit audit;


    @Autowired
    private TermistoService termistoService;

    @RequestMapping(value = "/termisto", method = GET)
    public List<TermiDto> getAllTermit(
            @PathVariable("opsId") final Long opsId) {
        return termistoService.getTermit(opsId);
    }

    @RequestMapping(value = "/termi/{avain}", method = GET)
    public TermiDto getTermi(
            @PathVariable("opsId") final Long opsId,
            @PathVariable("avain") final String avain) {
        return termistoService.getTermi(opsId, avain);
    }

    @RequestMapping(value = "/termisto", method = POST)
    @ResponseStatus(HttpStatus.CREATED)
    public TermiDto addTermi(
            @PathVariable("opsId") final Long opsId,
            @RequestBody TermiDto dto) {
        return audit.withAudit(LogMessage.builder(opsId, TERMI, LISAYS), (Void) -> {
            dto.setId(null);
            return termistoService.addTermi(opsId, dto);
        });
    }

    @RequestMapping(value = "/termisto/{id}", method = POST)
    public TermiDto updateTermi(
            @PathVariable("opsId") final Long opsId,
            @PathVariable("id") final Long id,
            @RequestBody TermiDto dto) {
        return audit.withAudit(LogMessage.builder(opsId, TERMI, MUOKKAUS), (Void) -> {
            dto.setId(id);
            return termistoService.updateTermi(opsId, dto);
        });
    }

    @RequestMapping(value = "/termisto/{id}", method = DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTermi(
            @PathVariable("opsId") final Long opsId,
            @PathVariable("id") final Long id) {
        audit.withAudit(LogMessage.builder(opsId, TERMI, POISTO), (Void) -> {
            termistoService.deleteTermi(opsId, id);
            return null;
        });
    }
}
