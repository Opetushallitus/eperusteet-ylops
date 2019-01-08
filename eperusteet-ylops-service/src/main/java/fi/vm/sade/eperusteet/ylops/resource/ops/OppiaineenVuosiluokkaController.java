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

import fi.vm.sade.eperusteet.ylops.dto.ops.OppiaineenVuosiluokkaDto;
import fi.vm.sade.eperusteet.ylops.dto.teksti.TekstiosaDto;
import fi.vm.sade.eperusteet.ylops.resource.util.Responses;
import fi.vm.sade.eperusteet.ylops.service.audit.EperusteetYlopsAudit;

import static fi.vm.sade.eperusteet.ylops.service.audit.EperusteetYlopsMessageFields.VALINNAINENVUOSILUOKKA;
import static fi.vm.sade.eperusteet.ylops.service.audit.EperusteetYlopsMessageFields.VUOSILUOKKA;
import static fi.vm.sade.eperusteet.ylops.service.audit.EperusteetYlopsOperation.MUOKKAUS;

import fi.vm.sade.eperusteet.ylops.service.audit.LogMessage;
import fi.vm.sade.eperusteet.ylops.service.ops.OppiaineService;
import io.swagger.annotations.Api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

/**
 * @author mikkom
 */
@RestController
@RequestMapping("/opetussuunnitelmat/{opsId}/oppiaineet/{oppiaineId}/vuosiluokkakokonaisuudet/{kokonaisuusId}/vuosiluokat")
@ApiIgnore
@Api(value = "OppiaineenVuosiluokat")
public class OppiaineenVuosiluokkaController {
    @Autowired
    private EperusteetYlopsAudit audit;

    @Autowired
    private OppiaineService oppiaineService;

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<OppiaineenVuosiluokkaDto> get(
            @PathVariable("opsId") final Long opsId,
            @PathVariable("oppiaineId") final Long oppiaineId,
            @PathVariable("id") final Long id) {
        OppiaineenVuosiluokkaDto oa = oppiaineService.getVuosiluokka(opsId, oppiaineId, id);
        return Responses.ofNullable(oa);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.POST)
    public OppiaineenVuosiluokkaDto updateVuosiluokanSisalto(
            @PathVariable("opsId") final Long opsId,
            @PathVariable("oppiaineId") final Long oppiaineId,
            @PathVariable("id") final Long id,
            @RequestBody OppiaineenVuosiluokkaDto dto) {
        return audit.withAudit(LogMessage.builder(opsId, VUOSILUOKKA, MUOKKAUS), (Void) -> {
            dto.setId(id);
            return oppiaineService.updateVuosiluokanSisalto(opsId, oppiaineId, dto);
        });
    }

    @RequestMapping(value = "/{id}/valinnainen", method = RequestMethod.POST)
    public OppiaineenVuosiluokkaDto updateValinnaisenVuosiluokanSisalto(
            @PathVariable("opsId") final Long opsId,
            @PathVariable("oppiaineId") final Long oppiaineId,
            @PathVariable("id") final Long id,
            @RequestBody List<TekstiosaDto> tavoitteetDto) {
        return audit.withAudit(LogMessage.builder(opsId, VALINNAINENVUOSILUOKKA, MUOKKAUS), (Void) -> {
            return oppiaineService.updateValinnaisenVuosiluokanSisalto(opsId, oppiaineId, id, tavoitteetDto);
        });
    }
}
