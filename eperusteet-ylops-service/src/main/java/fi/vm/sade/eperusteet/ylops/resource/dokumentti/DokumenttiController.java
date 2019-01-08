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
import fi.vm.sade.eperusteet.ylops.domain.ops.Opetussuunnitelma;
import fi.vm.sade.eperusteet.ylops.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.ylops.dto.dokumentti.DokumenttiDto;
import fi.vm.sade.eperusteet.ylops.repository.dokumentti.DokumenttiRepository;
import fi.vm.sade.eperusteet.ylops.repository.ops.OpetussuunnitelmaRepository;
import fi.vm.sade.eperusteet.ylops.service.audit.EperusteetYlopsAudit;
import fi.vm.sade.eperusteet.ylops.service.audit.LogMessage;
import fi.vm.sade.eperusteet.ylops.service.dokumentti.DokumenttiService;
import fi.vm.sade.eperusteet.ylops.service.exception.DokumenttiException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Objects;
import java.util.Optional;

import static fi.vm.sade.eperusteet.ylops.service.audit.EperusteetYlopsMessageFields.OPETUSSUUNNITELMA;
import static fi.vm.sade.eperusteet.ylops.service.audit.EperusteetYlopsOperation.GENEROI;

/**
 * @author iSaul
 */
@RestController
@RequestMapping("/dokumentit")
@Api("Dokumentit")
public class DokumenttiController {
    private static final int MAX_TIME_IN_MINUTES = 2;

    @Autowired
    private EperusteetYlopsAudit audit;

    @Autowired
    DokumenttiService service;

    @Autowired
    DokumenttiRepository dokumenttiRepository;

    @Autowired
    OpetussuunnitelmaRepository opetussuunnitelmaRepository;

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<DokumenttiDto> create(
            @RequestParam final long opsId,
            @RequestParam(defaultValue = "fi") final String kieli
    ) throws DokumenttiException {
        HttpStatus status;

        DokumenttiDto dtoForDokumentti = service.getDto(opsId, Kieli.of(kieli));

        // Jos dokumentti ei löydy valmiiksi niin koitetaan tehdä uusi
        if (dtoForDokumentti == null)
            dtoForDokumentti = service.createDtoFor(opsId, Kieli.of(kieli));

        // Jos tila epäonnistunut, opsia ei löytynyt
        if (dtoForDokumentti == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        // Aloitetaan luonti jos luonti ei ole jo päällä tai maksimi luontiaika ylitetty
        if (isTimePass(dtoForDokumentti) || dtoForDokumentti.getTila() != DokumenttiTila.LUODAAN) {
            // Vaihdetaan dokumentin tila luonniksi
            service.setStarted(dtoForDokumentti);

            // Generoidaan dokumentin data sisältö
            // Asynkroninen metodi
            service.generateWithDto(dtoForDokumentti);

            status = HttpStatus.ACCEPTED;
        } else {
            status = HttpStatus.FORBIDDEN;
        }

        // Uusi objekti dokumentissa, jossa päivitetyt tiedot
        final DokumenttiDto dtoDokumentti = service.getDto(dtoForDokumentti.getId());
        audit.withAudit(LogMessage.builder(opsId, OPETUSSUUNNITELMA, GENEROI));

        return new ResponseEntity<>(dtoDokumentti, status);
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
        Optional.ofNullable(dokumenttiRepository.findOne(dokumenttiId))
                .filter(Objects::nonNull)
                .map(dokumentti -> opetussuunnitelmaRepository.findOne(dokumentti.getOpsId()))
                .filter(Objects::nonNull)
                .map(Opetussuunnitelma::getNimi)
                .filter(Objects::nonNull)
                .ifPresent(nimi -> headers.set("Content-disposition", "inline; filename=\"" + nimi + ".pdf\""));

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
        if (dto == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(dto, HttpStatus.OK);
        }
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "dokumenttiId", dataType = "string", paramType = "path", required = true)
    })
    @RequestMapping(value = "/{dokumenttiId}/dokumentti", method = RequestMethod.GET)
    public ResponseEntity<DokumenttiDto> query(@PathVariable final Long dokumenttiId) {
        DokumenttiDto dto = service.query(dokumenttiId);
        if (dto == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        else
            return new ResponseEntity<>(dto, HttpStatus.OK);
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
}
