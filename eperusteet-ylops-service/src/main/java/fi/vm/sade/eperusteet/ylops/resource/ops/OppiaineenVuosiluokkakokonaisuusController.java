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

import fi.vm.sade.eperusteet.ylops.domain.Vuosiluokka;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpetuksenTavoiteDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OppiaineDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OppiaineenVuosiluokkaDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OppiaineenVuosiluokkakokonaisuusDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.PerusteDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.PerusteOppiaineenVuosiluokkakokonaisuusDto;
import fi.vm.sade.eperusteet.ylops.resource.util.CacheControl;
import fi.vm.sade.eperusteet.ylops.resource.util.Responses;
import fi.vm.sade.eperusteet.ylops.service.ops.OpetussuunnitelmaService;
import fi.vm.sade.eperusteet.ylops.service.ops.OppiaineService;
import io.swagger.annotations.Api;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

/**
 * @author jhyoty
 */
@RestController
@RequestMapping("/opetussuunnitelmat/{opsId}/oppiaineet/{oppiaineId}/vuosiluokkakokonaisuudet")
@ApiIgnore
@Api(value = "OppiaineenVuosiluokkakokonaisuudet")
public class OppiaineenVuosiluokkakokonaisuusController {

    @Autowired
    private OppiaineService oppiaineService;

    @Autowired
    private OpetussuunnitelmaService ops;

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<OppiaineenVuosiluokkakokonaisuusDto> getOppiaineenVuosiluokkakokonaisuus(
            @PathVariable("opsId") final Long opsId,
            @PathVariable("oppiaineId") final Long oppiaineId,
            @PathVariable("id") final Long id) {

        OppiaineDto oa = oppiaineService.get(opsId, oppiaineId).getOppiaine();
        return Responses.of(oa.getVuosiluokkakokonaisuudet().stream()
                .filter(vk -> vk.getId().equals(id))
                .findAny());
    }

    @RequestMapping(value = "/{id}/tavoitteet", method = RequestMethod.GET)
    public Map<Vuosiluokka, Set<UUID>> getVuosiluokkienTavoitteet(
            @PathVariable("opsId") final Long opsId,
            @PathVariable("oppiaineId") final Long oppiaineId,
            @PathVariable("id") final Long id) {

        OppiaineDto oa = oppiaineService.get(opsId, oppiaineId).getOppiaine();
        return oa.getVuosiluokkakokonaisuudet().stream()
                .filter(vk -> vk.getId().equals(id))
                .flatMap(vk -> vk.getVuosiluokat().stream())
                .collect(Collectors.toMap(
                        OppiaineenVuosiluokkaDto::getVuosiluokka,
                        l -> l.getTavoitteet().stream().map(OpetuksenTavoiteDto::getTunniste).collect(Collectors.toSet())));
    }

    @RequestMapping(value = "/{id}/tavoitteet", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateVuosiluokkienTavoitteet(
            @PathVariable("opsId") final Long opsId,
            @PathVariable("oppiaineId") final Long oppiaineId,
            @PathVariable("id") final Long id,
            @RequestBody Map<Vuosiluokka, Set<UUID>> tavoitteet) {
        oppiaineService.updateVuosiluokkienTavoitteet(opsId, oppiaineId, id, tavoitteet);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.POST)
    public OppiaineenVuosiluokkakokonaisuusDto updateVuosiluokkakokonaisuudenSisalto(
            @PathVariable("opsId") final Long opsId,
            @PathVariable("oppiaineId") final Long oppiaineId,
            @PathVariable("id") final Long id,
            @RequestBody OppiaineenVuosiluokkakokonaisuusDto dto) {
        dto.setId(id);
        return oppiaineService.updateVuosiluokkakokonaisuudenSisalto(opsId, oppiaineId, dto);
    }

    /*
    @RequestMapping(method = RequestMethod.GET)
    public Set<OppiaineenVuosiluokkakokonaisuusDto> getAll(
        @PathVariable("opsId") final Long opsId,
        @PathVariable("oppiaineId") final Long oppiaineId) {
        return oppiaineService.getLiitetiedosto(opsId, oppiaineId).getVuosiluokkakokonaisuudet();
    }

    @RequestMapping(value = "/valinnaiset",method = RequestMethod.GET)
    public List<OppiaineDto> getValinnaiset(@PathVariable("opsId") final Long opsId) {
        return oppiaineService.getAll(opsId, true);
    }
    */

    @RequestMapping(value = "/{id}/peruste", method = RequestMethod.GET)
    @CacheControl(nonpublic = false, age = 3600)
    public ResponseEntity<PerusteOppiaineenVuosiluokkakokonaisuusDto> getOppiaineenVuosiluokkakokonaisuudenPerusteSisalto(
            @PathVariable("opsId") final Long opsId,
            @PathVariable("oppiaineId") final Long oppiaineId,
            @PathVariable("id") final Long id) {

        final PerusteDto peruste = ops.getPeruste(opsId);
        final Optional<OppiaineDto> aine = Optional.ofNullable(oppiaineService.get(opsId, oppiaineId).getOppiaine());

        return Responses.of(aine.flatMap(a -> a.getVuosiluokkakokonaisuudet().stream()
                .filter(vk -> vk.getId().equals(id))
                .findAny()
                .flatMap(ovk -> peruste.getPerusopetus().getOppiaine(a.getTunniste())
                        .flatMap(poa -> poa.getVuosiluokkakokonaisuus(ovk.getVuosiluokkakokonaisuus())))));

    }
}
