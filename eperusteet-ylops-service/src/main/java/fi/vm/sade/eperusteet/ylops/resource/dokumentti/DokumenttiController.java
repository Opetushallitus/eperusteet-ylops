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

package fi.vm.sade.eperusteet.ylops.resource.dokumentti;

import fi.vm.sade.eperusteet.ylops.domain.dokumentti.DokumenttiTila;
import fi.vm.sade.eperusteet.ylops.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.ylops.dto.dokumentti.DokumenttiDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpetussuunnitelmaKevytDto;
import fi.vm.sade.eperusteet.ylops.dto.teksti.LokalisoituTekstiDto;
import fi.vm.sade.eperusteet.ylops.service.audit.EperusteetYlopsAudit;
import fi.vm.sade.eperusteet.ylops.service.audit.LogMessage;
import fi.vm.sade.eperusteet.ylops.service.dokumentti.DokumenttiService;
import fi.vm.sade.eperusteet.ylops.service.exception.BusinessRuleViolationException;
import fi.vm.sade.eperusteet.ylops.service.exception.DokumenttiException;
import fi.vm.sade.eperusteet.ylops.service.ops.OpetussuunnitelmaService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;

import static fi.vm.sade.eperusteet.ylops.service.audit.EperusteetYlopsMessageFields.OPETUSSUUNNITELMA;
import static fi.vm.sade.eperusteet.ylops.service.audit.EperusteetYlopsOperation.*;

/**
 * @author iSaul
 */
@Api("Dokumentit")
@RestController
@RequestMapping("/dokumentit")
public class DokumenttiController {
    private static final int MAX_TIME_IN_MINUTES = 2;

    @Autowired
    private EperusteetYlopsAudit audit;

    @Autowired
    DokumenttiService service;

    @Autowired
    OpetussuunnitelmaService opetussuunnitelmaService;

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<DokumenttiDto> create(
            @RequestParam final long opsId,
            @RequestParam(defaultValue = "fi") final String kieli
    ) throws DokumenttiException {
        DokumenttiDto dto = service.getDto(opsId, Kieli.of(kieli));
        if (dto == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        // Aloitetaan luonti jos luonti ei ole jo päällä tai maksimi luontiaika ylitetty
        if (isTimePass(dto) || dto.getTila() != DokumenttiTila.LUODAAN) {
            // Vaihdetaan dokumentin tila luonniksi
            service.setStarted(dto);

            // Luodaan dokumentin sisältö
            service.generateWithDto(dto);

            return new ResponseEntity<>(service.getDto(dto.getId()), HttpStatus.CREATED);
        } else {
            throw new BusinessRuleViolationException("Luonti on jo käynissä");
        }
    }

    private boolean isTimePass(DokumenttiDto dokumenttiDto) {
        Date date = dokumenttiDto.getAloitusaika();
        if (date == null) {
            return true;
        }

        Date newDate = DateUtils.addMinutes(date, MAX_TIME_IN_MINUTES);
        return newDate.before(new Date());
    }

    @RequestMapping(value = "/{dokumenttiId}", method = RequestMethod.GET, produces = "application/pdf")
    public ResponseEntity<Object> get(@PathVariable final Long dokumenttiId) {
        byte[] pdfdata = service.get(dokumenttiId);

        if (pdfdata == null || pdfdata.length == 0) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        if (!service.hasPermission(dokumenttiId)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-disposition", "inline; filename=\"" + dokumenttiId + ".pdf\"");
        DokumenttiDto dokumenttiDto = service.getDto(dokumenttiId);
        if (dokumenttiDto != null) {
            OpetussuunnitelmaKevytDto opsDto = opetussuunnitelmaService.getOpetussuunnitelma(dokumenttiDto.getOpsId());
            if (opsDto != null) {
                LokalisoituTekstiDto nimi = opsDto.getNimi();
                if (nimi != null && nimi.getTekstit().containsKey(dokumenttiDto.getKieli())) {
                    headers.set("Content-disposition", "inline; filename=\""
                            + nimi.getTekstit().get(dokumenttiDto.getKieli()) + ".pdf\"");
                }
            }
        }

        return new ResponseEntity<>(pdfdata, headers, HttpStatus.OK);
    }

    @RequestMapping(value = "/ops", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Long> getDokumenttiId(
            @RequestParam final Long opsId,
            @RequestParam(defaultValue = "fi") final String kieli
    ) {
        Long dokumenttiId = service.getDokumenttiId(opsId, Kieli.of(kieli));
        return ResponseEntity.ok(dokumenttiId);
    }

    @RequestMapping(method = RequestMethod.GET, params = "opsId")
    public ResponseEntity<DokumenttiDto> getDokumentti(
            @RequestParam final Long opsId,
            @RequestParam(defaultValue = "fi") final String kieli
    ) {
        Kieli k = Kieli.of(kieli);
        DokumenttiDto dto = service.getDto(opsId, k);

        // Jos dokumentti ei löydy valmiiksi niin koitetaan tehdä uusi
        if (dto == null) {
            return ResponseEntity.ok(service.createDtoFor(opsId, Kieli.of(kieli)));
        } else {
            return ResponseEntity.ok(dto);
        }
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "dokumenttiId", dataType = "string", paramType = "path", required = true)
    })
    @RequestMapping(value = "/{dokumenttiId}/dokumentti", method = RequestMethod.GET)
    public ResponseEntity<DokumenttiDto> query(@PathVariable final Long dokumenttiId) {
        DokumenttiDto dto = service.query(dokumenttiId);
        if (dto == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(dto, HttpStatus.OK);
        }
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "dokumenttiId", dataType = "string", paramType = "path", required = true)
    })
    @RequestMapping(value = "/{dokumenttiId}/tila", method = RequestMethod.GET)
    public ResponseEntity<DokumenttiTila> exist(
            @RequestParam final Long opsId,
            @RequestParam(defaultValue = "fi") final String kieli
    ) {
        Kieli k = Kieli.of(kieli);
        DokumenttiTila tila = service.getTila(opsId, k);
        if (tila == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            return ResponseEntity.ok(tila);
        }
    }

    @RequestMapping(value = "/kuva", method = RequestMethod.POST)
    public ResponseEntity<DokumenttiDto> addImage(
            @RequestParam Long opsId,
            @RequestParam String tyyppi,
            @RequestParam(defaultValue = "fi") String kieli,
            @RequestPart MultipartFile file
    ) throws IOException {
        LogMessage.builder(opsId, OPETUSSUUNNITELMA, DOKUMENTTI_KUVAN_LISAYS).log();

        Kieli k = Kieli.of(kieli);

        DokumenttiDto dokumenttiDto = service.getDto(opsId, Kieli.of(kieli));

        // Jos dokumentti ei löydy valmiiksi niin koitetaan tehdä uusi
        if (dokumenttiDto == null) {
            dokumenttiDto = service.createDtoFor(opsId, Kieli.of(kieli));
        }

        // Jos tila epäonnistunut, opsia ei löytynyt
        if (dokumenttiDto == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        // Lisää kuva dokumenttiin
        DokumenttiDto dto = service.addImage(opsId, dokumenttiDto, tyyppi, k, file);

        if (dto != null) {
            return ResponseEntity.ok(dto);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/kuva", method = RequestMethod.GET)
    public ResponseEntity<Object> getImage(
            @RequestParam Long opsId,
            @RequestParam String tyyppi,
            @RequestParam(defaultValue = "fi") String kieli
    ) {
        Kieli k = Kieli.of(kieli);
        byte[] image = service.getImage(opsId, tyyppi, k);
        if (image == null) {
            return ResponseEntity.notFound().build();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);
        headers.setContentLength(image.length);

        return new ResponseEntity<>(image, headers, HttpStatus.OK);
    }

    @RequestMapping(value = "/kuva", method = RequestMethod.DELETE)
    public ResponseEntity<Object> deleteImage(
            @RequestParam Long opsId,
            @RequestParam String tyyppi,
            @RequestParam(defaultValue = "fi") String kieli
    ) {
        LogMessage.builder(opsId, OPETUSSUUNNITELMA, DOKUMENTTI_KUVAN_POISTO).log();
        Kieli k = Kieli.of(kieli);
        service.deleteImage(opsId, tyyppi, k);

        return ResponseEntity.noContent().build();
    }
}
