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

import fi.vm.sade.eperusteet.ylops.dto.liite.LiiteDto;
import fi.vm.sade.eperusteet.ylops.resource.util.CacheControl;
import fi.vm.sade.eperusteet.ylops.service.audit.EperusteetYlopsAudit;

import static fi.vm.sade.eperusteet.ylops.service.audit.EperusteetYlopsMessageFields.KUVA;
import static fi.vm.sade.eperusteet.ylops.service.audit.EperusteetYlopsMessageFields.LIITE;
import static fi.vm.sade.eperusteet.ylops.service.audit.EperusteetYlopsOperation.LISAYS;
import static fi.vm.sade.eperusteet.ylops.service.audit.EperusteetYlopsOperation.POISTO;

import fi.vm.sade.eperusteet.ylops.service.audit.LogMessage;
import fi.vm.sade.eperusteet.ylops.service.ops.LiiteService;
import io.swagger.annotations.Api;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.apache.tika.Tika;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * @author jhyoty
 */
@RestController
@RequestMapping("/opetussuunnitelmat/{opsId}/kuvat")
@Api("Liitetiedostot")
public class LiitetiedostoController {
    @Autowired
    private EperusteetYlopsAudit audit;

    private static final Logger LOG = LoggerFactory.getLogger(LiitetiedostoController.class);

    private static final int BUFSIZE = 64 * 1024;
    final Tika tika = new Tika();

    @Autowired
    private LiiteService liitteet;

    private static final Set<String> SUPPORTED_TYPES;

    static {
        HashSet<String> tmp = new HashSet<>(Arrays.asList(MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE));
        SUPPORTED_TYPES = Collections.unmodifiableSet(tmp);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.POST)
    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')")
    public void reScaleImg(@PathVariable @P("opsId") Long opsId,
                           @PathVariable UUID id,
                           @RequestParam Integer width,
                           @RequestParam Integer height,
                           @RequestParam Part file) {
        //TODO implement image rescaling
//        return audit.withAudit(LogMessage.builder(opsId, LIITETIEDOSTO, MUOKKAUSKO));
    }

    @RequestMapping(method = RequestMethod.POST)
    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')")
    public ResponseEntity<String> upload(@PathVariable @P("opsId") Long opsId,
                                         @RequestParam String nimi,
                                         @RequestParam Part file,
                                         @RequestParam Integer width,
                                         @RequestParam Integer height,
                                         UriComponentsBuilder ucb)
            throws IOException, HttpMediaTypeNotSupportedException {
        final long koko = file.getSize();

        try (PushbackInputStream pis = new PushbackInputStream(file.getInputStream(), BUFSIZE)) {
            byte[] buf = new byte[koko < BUFSIZE ? (int) koko : BUFSIZE];
            int len = pis.read(buf);
            if (len < buf.length) {
                throw new IOException("luku epäonnistui");
            }
            pis.unread(buf);
            String tyyppi = tika.detect(buf);
            if (!SUPPORTED_TYPES.contains(tyyppi)) {
                throw new HttpMediaTypeNotSupportedException(tyyppi + "ei ole tuettu");
            }

            UUID id = null;
            if (width != null && height != null) {
                ByteArrayOutputStream os = scaleImage(file, tyyppi, width, height);
                id = liitteet.add(opsId, tyyppi, nimi, os.size(), new PushbackInputStream(new ByteArrayInputStream(os.toByteArray())));
            } else {
                id = liitteet.add(opsId, tyyppi, nimi, koko, pis);
            }

            HttpHeaders h = new HttpHeaders();
            h.setLocation(ucb.path("/opetussuunnitelmat/{opsId}/kuvat/{id}").buildAndExpand(opsId, id.toString()).toUri());
            audit.withAudit(LogMessage.builder(opsId, KUVA, LISAYS));
            return new ResponseEntity<>(id.toString(), h, HttpStatus.CREATED);
        }
    }

    private ByteArrayOutputStream scaleImage(@RequestParam("file") Part file, String tyyppi, Integer width, Integer height) throws IOException {
        BufferedImage a = ImageIO.read(file.getInputStream());
        BufferedImage preview = new BufferedImage(width, height, a.getType());
        preview.createGraphics().drawImage(a.getScaledInstance(width, height, Image.SCALE_SMOOTH), 0, 0, null);

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(preview, tyyppi.replace("image/", ""), os);

        return os;
    }

    private BufferedImage scaleImage(BufferedImage img, int maxDimension) {
        int w = (img.getWidth() > img.getHeight() ? maxDimension :
                (int) (((double) img.getWidth() / img.getHeight()) * maxDimension));

        int h = (img.getHeight() > img.getWidth() ? maxDimension :
                (int) (((double) img.getHeight() / img.getWidth()) * maxDimension));

        BufferedImage preview = new BufferedImage(w, h, img.getType());
        preview.createGraphics().drawImage(img.getScaledInstance(w, h, Image.SCALE_SMOOTH), 0, 0, null);
        return preview;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @CacheControl(age = CacheControl.ONE_YEAR)
    public void getLiitetiedosto(@PathVariable Long opsId,
                                 @PathVariable UUID id,
                                 @RequestHeader(value = "If-None-Match", required = false) String etag,
                                 HttpServletResponse response) throws IOException {

        LiiteDto dto = liitteet.get(opsId, id);
        if (dto != null) {
            if (etag != null && dto.getId().toString().equals(etag)) {
                response.setStatus(HttpStatus.NOT_MODIFIED.value());
            } else {
                response.setHeader("Content-Type", dto.getTyyppi());
                response.setHeader("ETag", id.toString());
                try (OutputStream os = response.getOutputStream()) {
                    liitteet.export(opsId, id, os);
                    os.flush();
                }
            }
        } else {
            response.setStatus(HttpStatus.NOT_FOUND.value());
        }
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteLiitetiedosto(@PathVariable Long opsId, @PathVariable UUID id) {
        audit.withAudit(LogMessage.builder(opsId, LIITE, POISTO), (Void) -> {
            liitteet.delete(opsId, id);
            return null;
        });
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<LiiteDto> getAllLiitteet(@PathVariable Long opsId) {
        return liitteet.getAll(opsId);
    }
}
