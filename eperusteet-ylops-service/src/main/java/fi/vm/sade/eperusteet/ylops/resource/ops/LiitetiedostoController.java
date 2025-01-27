package fi.vm.sade.eperusteet.ylops.resource.ops;

import fi.vm.sade.eperusteet.ylops.dto.liite.LiiteDto;
import fi.vm.sade.eperusteet.ylops.resource.util.CacheControl;
import fi.vm.sade.eperusteet.ylops.service.ops.LiiteService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import org.apache.commons.io.FilenameUtils;
import org.apache.tika.Tika;
import org.apache.tika.mime.MimeTypeException;
import org.apache.tika.mime.MimeTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PushbackInputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/opetussuunnitelmat/{opsId}/kuvat")
@Tag(name = "Liitetiedostot")
public class LiitetiedostoController {

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
    }

    @RequestMapping(method = RequestMethod.POST)
    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')")
    public ResponseEntity<String> upload(@PathVariable @P("opsId") Long opsId,
                                         @RequestParam String nimi,
                                         @RequestParam Part file,
                                         @RequestParam Integer width,
                                         @RequestParam Integer height,
                                         UriComponentsBuilder ucb)
            throws IOException, HttpMediaTypeNotSupportedException, MimeTypeException {
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

            UUID id;
            if (width != null && height != null) {
                ByteArrayOutputStream os = scaleImage(file, tyyppi, width, height);
                id = liitteet.add(opsId, tyyppi, nimi, os.size(), new PushbackInputStream(new ByteArrayInputStream(os.toByteArray())));
            } else {
                id = liitteet.add(opsId, tyyppi, nimi, koko, pis);
            }

            MimeTypes mimeTypes = MimeTypes.getDefaultMimeTypes();
            String extension = mimeTypes.forName(tyyppi).getExtension();

            HttpHeaders h = new HttpHeaders();
            h.setLocation(ucb.path("/opetussuunnitelmat/{opsId}/kuvat/{id}" + extension).buildAndExpand(opsId, id.toString()).toUri());
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

    @RequestMapping(value = "/{fileName}", method = RequestMethod.GET)
    @CacheControl(age = CacheControl.ONE_YEAR)
    public void getLiitetiedosto(
            @PathVariable Long opsId,
            @PathVariable String fileName,
            @RequestHeader(value = "If-None-Match", required = false) String etag,
            HttpServletResponse response
    ) throws IOException {
        UUID id = UUID.fromString(FilenameUtils.removeExtension(fileName));
        LiiteDto dto = liitteet.get(opsId, id);

        if (id.toString().equals(etag)) {
            response.setStatus(HttpStatus.NOT_MODIFIED.value());
        } else if (dto != null) {
            response.setHeader("Content-Type", dto.getTyyppi());
            response.setHeader("ETag", id.toString());
            try (OutputStream os = response.getOutputStream()) {
                liitteet.export(opsId, id, os);
                os.flush();
            }
        } else {
            response.setHeader("ETag", id.toString());
            try (OutputStream os = response.getOutputStream()) {
                liitteet.exportLiitePerusteelta(opsId, id, os);
                os.flush();
            } catch (Exception e) {
                response.setStatus(HttpStatus.NOT_FOUND.value());
            }
        }

    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteLiitetiedosto(@PathVariable Long opsId, @PathVariable UUID id) {
        liitteet.delete(opsId, id);
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<LiiteDto> getAllLiitteet(@PathVariable Long opsId) {
        return liitteet.getAll(opsId);
    }
}
