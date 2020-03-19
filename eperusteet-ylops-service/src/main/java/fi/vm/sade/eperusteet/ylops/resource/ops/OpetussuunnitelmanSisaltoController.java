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
import fi.vm.sade.eperusteet.ylops.dto.RevisionDto;
import fi.vm.sade.eperusteet.ylops.dto.teksti.PoistettuTekstiKappaleDto;
import fi.vm.sade.eperusteet.ylops.dto.teksti.TekstiKappaleDto;
import fi.vm.sade.eperusteet.ylops.dto.teksti.TekstiKappaleViiteDto;
import fi.vm.sade.eperusteet.ylops.dto.teksti.TekstiKappaleViiteKevytDto;

import fi.vm.sade.eperusteet.ylops.service.ops.OpetussuunnitelmaService;
import fi.vm.sade.eperusteet.ylops.service.ops.TekstiKappaleViiteService;

import java.util.List;

import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import springfox.documentation.annotations.ApiIgnore;

/**
 * @author mikkom
 */
@RestController
@RequestMapping("/opetussuunnitelmat/{opsId}")
@Api(value = "OpetussuunnitelmanSisalto")
@ApiIgnore
public class OpetussuunnitelmanSisaltoController {

    @Autowired
    private OpetussuunnitelmaService opetussuunnitelmaService;

    @Autowired
    private TekstiKappaleViiteService tekstiKappaleViiteService;

    @RequestMapping(value = "/tekstit", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<TekstiKappaleViiteDto.Matala> addTekstiKappale(
            @PathVariable("opsId") final Long opsId,
            // TODO: Lisätäänkö myös addTekstiKappaleViite PUT-metodi jossa viiteDto on pakollinen kenttä?
            @RequestBody(required = false) TekstiKappaleViiteDto.Matala tekstiKappaleViiteDto) {
        return new ResponseEntity<>(
                opetussuunnitelmaService.addTekstiKappale(opsId, tekstiKappaleViiteDto), HttpStatus.OK);
    }

    @RequestMapping(value = "/tekstit/{viiteId}/versiot", method = GET)
    public ResponseEntity<List<RevisionDto>> getVersionsForTekstiKappaleViite(
            @PathVariable("opsId") final Long opsId,
            @PathVariable("viiteId") final long viiteId) {

        return new ResponseEntity<>(tekstiKappaleViiteService.getVersions(opsId, viiteId), HttpStatus.OK);
    }

    @RequestMapping(value = "/tekstit/{viiteId}/versio/{versio}", method = GET)
    public TekstiKappaleDto getVersionForTekstiKappaleViite(
            @PathVariable("opsId") final Long opsId,
            @PathVariable("viiteId") final long viiteId,
            @PathVariable("versio") final long versio) {
        return tekstiKappaleViiteService.findTekstikappaleVersion(opsId, viiteId, versio);
    }

    @RequestMapping(value = "/tekstit/{viiteId}/revert/{versio}", method = RequestMethod.POST)
    public void revertTekstikappaleToVersion(
            @PathVariable("opsId") final Long opsId,
            @PathVariable("viiteId") final Long viiteId,
            @PathVariable("versio") final Integer versio) {
        tekstiKappaleViiteService.revertToVersion(opsId, viiteId, versio);
    }

    @RequestMapping(value = "/tekstit/removed", method = GET)
    public ResponseEntity<List<PoistettuTekstiKappaleDto>> getRemovedTekstikappaleet(@PathVariable("opsId") final Long opsId) {
        return new ResponseEntity<>(tekstiKappaleViiteService.getRemovedTekstikappaleetForOps(opsId), HttpStatus.OK);
    }

    @RequestMapping(value = "/tekstit/{id}/returnRemoved", method = POST)
    public void returnRemoved(
            @PathVariable("opsId") final Long opsId,
            @PathVariable("id") final Long id) {
        tekstiKappaleViiteService.returnRemovedTekstikappale(opsId, id);
    }

    @RequestMapping(value = "/tekstit/{viiteId}/lapsi", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<TekstiKappaleViiteDto.Matala> addTekstiKappaleLapsi(
            @PathVariable("opsId") final Long opsId,
            @PathVariable("viiteId") final Long viiteId,
            @RequestBody(required = false) TekstiKappaleViiteDto.Matala tekstiKappaleViiteDto) {
        return new ResponseEntity<>(
                opetussuunnitelmaService.addTekstiKappaleLapsi(opsId, viiteId, tekstiKappaleViiteDto), HttpStatus.CREATED);
    }

    @RequestMapping(value = "/tekstit/{parentId}/lapsi/{childId}", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<TekstiKappaleViiteDto.Matala> addTekstiKappaleLapsiTiettyynTekstikappaleeseen(
            @PathVariable("opsId") final Long opsId,
            @PathVariable("parentId") final Long parentId,
            @PathVariable("childId") final Long childId) {
        TekstiKappaleViiteDto.Matala viite = new TekstiKappaleViiteDto.Matala();
        viite.setTekstiKappaleRef(Reference.of(childId));
        return new ResponseEntity<>(
                opetussuunnitelmaService.addTekstiKappaleLapsi(opsId, parentId, viite), HttpStatus.CREATED);
    }

    @RequestMapping(value = "/tekstit", method = RequestMethod.GET)
    public ResponseEntity<TekstiKappaleViiteDto.Puu> getTekstit(
            @PathVariable("opsId") final Long opsId) {
        TekstiKappaleViiteDto.Puu dto = opetussuunnitelmaService.getTekstit(opsId, TekstiKappaleViiteDto.Puu.class);
        if (dto == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @RequestMapping(value = "/otsikot", method = RequestMethod.GET)
    public ResponseEntity<TekstiKappaleViiteKevytDto> getTekstiOtsikot(@PathVariable("opsId") final Long opsId) {
        TekstiKappaleViiteKevytDto dto = opetussuunnitelmaService.getTekstit(opsId, TekstiKappaleViiteKevytDto.class);
        if (dto == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @RequestMapping(value = "/tekstit/{viiteId}", method = RequestMethod.GET)
    public ResponseEntity<TekstiKappaleViiteDto.Matala> getTekstiKappaleViite(
            @PathVariable("opsId") final Long opsId,
            @PathVariable("viiteId") final Long viiteId) {
        TekstiKappaleViiteDto.Matala dto = tekstiKappaleViiteService.getTekstiKappaleViite(opsId, viiteId);
        if (dto == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @RequestMapping(value = "/tekstit/{viiteId}/kaikki", method = RequestMethod.GET)
    public ResponseEntity<TekstiKappaleViiteDto.Puu> getTekstiKappaleViiteSyva(
            @PathVariable("opsId") final Long opsId,
            @PathVariable("viiteId") final Long viiteId) {
        TekstiKappaleViiteDto.Puu dto = tekstiKappaleViiteService.getTekstiKappaleViite(opsId, viiteId, TekstiKappaleViiteDto.Puu.class);
        if (dto == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @RequestMapping(value = "/tekstit/{viiteId}/alkuperainen", method = RequestMethod.GET)
    public ResponseEntity<TekstiKappaleViiteDto.Matala> getTekstiKappaleViiteOriginal(
            @PathVariable("opsId") final Long opsId,
            @PathVariable("viiteId") final Long viiteId) {
        TekstiKappaleViiteDto.Matala dto = tekstiKappaleViiteService.getTekstiKappaleViiteOriginal(opsId, viiteId);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @RequestMapping(value = "/tekstit/{viiteId}/alkuperaiset", method = RequestMethod.GET)
    public ResponseEntity<List<TekstiKappaleViiteDto.Matala>> getTekstiKappaleViiteOriginals(
            @PathVariable("opsId") final Long opsId,
            @PathVariable("viiteId") final Long viiteId) {
        List<TekstiKappaleViiteDto.Matala> dtos = tekstiKappaleViiteService.getTekstiKappaleViiteOriginals(opsId, viiteId);
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    @RequestMapping(value = "/tekstit/{viiteId}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeTekstiKappaleViite(
            @PathVariable("opsId") final Long opsId,
            @PathVariable("viiteId") final Long viiteId) {
        tekstiKappaleViiteService.removeTekstiKappaleViite(opsId, viiteId);
    }

    @RequestMapping(value = "/tekstit/{viiteId}", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateTekstiKappaleViite(
            @PathVariable("opsId") final Long opsId,
            @PathVariable("viiteId") final Long viiteId,
            @RequestBody final TekstiKappaleViiteDto.Puu tekstiKappaleViiteDto) {
        if (tekstiKappaleViiteDto.getLapset() != null) {
            tekstiKappaleViiteService.reorderSubTree(opsId, viiteId, tekstiKappaleViiteDto);
        } else {
            // Päivitä vain tekstikappale
            tekstiKappaleViiteService.updateTekstiKappaleViite(opsId, viiteId, tekstiKappaleViiteDto);
        }
    }

    @RequestMapping(value = "/tekstit/{viiteId}/muokattavakopio", method = RequestMethod.POST)
    public TekstiKappaleViiteDto.Puu kloonaaTekstiKappale(
            @PathVariable("opsId") final Long opsId,
            @PathVariable("viiteId") final Long viiteId) {
            return tekstiKappaleViiteService.kloonaaTekstiKappale(opsId, viiteId);
    }

}
