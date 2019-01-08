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

import fi.vm.sade.eperusteet.ylops.dto.Reference;
import fi.vm.sade.eperusteet.ylops.dto.ops.OppiaineDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.UnwrappedOpsVuosiluokkakokonaisuusDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.VuosiluokkakokonaisuusDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.PerusteDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.PerusteVuosiluokkakokonaisuusDto;
import fi.vm.sade.eperusteet.ylops.resource.util.Responses;
import fi.vm.sade.eperusteet.ylops.service.audit.EperusteetYlopsAudit;

import static fi.vm.sade.eperusteet.ylops.service.audit.EperusteetYlopsMessageFields.VUOSILUOKKAKOKONAISUUS;
import static fi.vm.sade.eperusteet.ylops.service.audit.EperusteetYlopsOperation.KLOONAUS;
import static fi.vm.sade.eperusteet.ylops.service.audit.EperusteetYlopsOperation.LISAYS;
import static fi.vm.sade.eperusteet.ylops.service.audit.EperusteetYlopsOperation.MUOKKAUS;
import static fi.vm.sade.eperusteet.ylops.service.audit.EperusteetYlopsOperation.POISTO;

import fi.vm.sade.eperusteet.ylops.service.audit.LogMessage;
import fi.vm.sade.eperusteet.ylops.service.ops.OpetussuunnitelmaService;
import fi.vm.sade.eperusteet.ylops.service.ops.VuosiluokkakokonaisuusService;
import io.swagger.annotations.Api;

import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/opetussuunnitelmat/{opsId}/vuosiluokkakokonaisuudet")
@Api(value = "Vuosiluokkakokonaisuudet")
public class VuosiluokkakokonaisuusController {
    @Autowired
    private EperusteetYlopsAudit audit;


    @Autowired
    private VuosiluokkakokonaisuusService vuosiluokkakokonaisuudet;

    @Autowired
    private OpetussuunnitelmaService opetussuunnitelmat;

    @RequestMapping(method = RequestMethod.POST)
    public VuosiluokkakokonaisuusDto add(
            @PathVariable final Long opsId,
            @RequestBody VuosiluokkakokonaisuusDto dto) {
        return audit.withAudit(LogMessage.builder(opsId, VUOSILUOKKAKOKONAISUUS, LISAYS), (Void) -> {
            return vuosiluokkakokonaisuudet.add(opsId, dto);
        });
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<UnwrappedOpsVuosiluokkakokonaisuusDto> get(
            @PathVariable final Long opsId,
            @PathVariable final Long id
    ) {
        return Responses.ofNullable(new UnwrappedOpsVuosiluokkakokonaisuusDto(vuosiluokkakokonaisuudet.get(opsId, id)));
    }

    @RequestMapping(value = "/{id}/peruste", method = RequestMethod.GET)
    public PerusteVuosiluokkakokonaisuusDto getPerusteSisalto(
            @PathVariable final Long opsId,
            @PathVariable final Long id
    ) {
        final PerusteDto peruste = opetussuunnitelmat.getPeruste(opsId);
        final VuosiluokkakokonaisuusDto v = vuosiluokkakokonaisuudet.get(opsId, id).getVuosiluokkakokonaisuus();

        Optional<PerusteVuosiluokkakokonaisuusDto> vkDto = peruste.getPerusopetus().getVuosiluokkakokonaisuudet()
                .stream()
                .filter(vk -> Reference.of(vk.getTunniste()).equals(v.getTunniste().get()))
                .findAny();

        return vkDto.orElse(null);
    }

    @RequestMapping(value = "/{id}/oppiaineet", method = RequestMethod.GET)
    public Set<OppiaineDto> findOppiaineet(@PathVariable final Long opsId, @PathVariable final Long id) {
        throw new UnsupportedOperationException("Ei ole toteutettu");
    }

//    @RequestMapping(method = RequestMethod.GET)
//    public Set<VuosiluokkakokonaisuusDto> getAll(@PathVariable("opsId") final Long opsId) {
//        throw new UnsupportedOperationException("TODO: toteuta");
//    }

    @RequestMapping(value = "/{id}", method = RequestMethod.POST)
    public UnwrappedOpsVuosiluokkakokonaisuusDto update(
            @PathVariable final Long opsId,
            @PathVariable final Long id,
            @RequestBody VuosiluokkakokonaisuusDto dto) {
        return audit.withAudit(LogMessage.builder(opsId, VUOSILUOKKAKOKONAISUUS, MUOKKAUS), (Void) -> {
            dto.setId(id);
            return new UnwrappedOpsVuosiluokkakokonaisuusDto(vuosiluokkakokonaisuudet.update(opsId, dto));
        });
    }

    @RequestMapping(value = "/{id}/muokattavakopio", method = RequestMethod.POST)
    public UnwrappedOpsVuosiluokkakokonaisuusDto kopioiMuokattavaksi(
            @PathVariable final Long opsId,
            @PathVariable final Long id) {
        return audit.withAudit(LogMessage.builder(opsId, VUOSILUOKKAKOKONAISUUS, KLOONAUS), (Void) -> {
            return new UnwrappedOpsVuosiluokkakokonaisuusDto(vuosiluokkakokonaisuudet.kopioiMuokattavaksi(opsId, id));
        });
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable final Long opsId,
            @PathVariable final Long id) {
        audit.withAudit(LogMessage.builder(opsId, VUOSILUOKKAKOKONAISUUS, POISTO), (Void) -> {
            vuosiluokkakokonaisuudet.delete(opsId, id);
            return null;
        });
    }
}
