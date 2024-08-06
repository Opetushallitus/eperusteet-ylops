package fi.vm.sade.eperusteet.ylops.resource.dokumentti;

import fi.vm.sade.eperusteet.ylops.domain.dokumentti.DokumenttiTila;
import fi.vm.sade.eperusteet.ylops.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.ylops.dto.dokumentti.DokumenttiDto;
import fi.vm.sade.eperusteet.ylops.dto.dokumentti.DokumenttiKuvaDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpetussuunnitelmaKevytDto;
import fi.vm.sade.eperusteet.ylops.dto.pdf.PdfData;
import fi.vm.sade.eperusteet.ylops.dto.teksti.LokalisoituTekstiDto;
import fi.vm.sade.eperusteet.ylops.service.dokumentti.DokumenttiKuvaService;
import fi.vm.sade.eperusteet.ylops.service.dokumentti.DokumenttiService;
import fi.vm.sade.eperusteet.ylops.service.exception.DokumenttiException;
import fi.vm.sade.eperusteet.ylops.service.mapping.DtoMapper;
import fi.vm.sade.eperusteet.ylops.service.ops.OpetussuunnitelmaService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.Objects;

@Slf4j
@Api("Dokumentit")
@RestController
@RequestMapping("/api/dokumentit")
public class DokumenttiController {
    @Autowired
    private DtoMapper mapper;

    @Autowired
    DokumenttiService dokumenttiService;

    @Autowired
    DokumenttiKuvaService dokumenttiKuvaService;

    @Autowired
    OpetussuunnitelmaService opetussuunnitelmaService;

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<DokumenttiDto> create(@RequestParam final long opsId,
                                                @RequestParam(defaultValue = "fi") final String kieli) throws DokumenttiException {

        DokumenttiDto viimeisinJulkaistuDokumentti = dokumenttiService.getJulkaistuDokumentti(opsId, Kieli.of(kieli), null);
        if (viimeisinJulkaistuDokumentti != null && viimeisinJulkaistuDokumentti.getTila().equals(DokumenttiTila.EPAONNISTUI)) {
            dokumenttiService.setStarted(viimeisinJulkaistuDokumentti);
            dokumenttiService.generateWithDto(viimeisinJulkaistuDokumentti, opetussuunnitelmaService.getOpetussuunnitelmaJulkaistuSisalto(opsId));
        }

        DokumenttiDto dto = dokumenttiService.createDtoFor(opsId, Kieli.of(kieli));
        if (dto != null && dto.getTila() != DokumenttiTila.EPAONNISTUI) {
            dokumenttiService.setStarted(dto);
            dokumenttiService.generateWithDto(dto);
            return new ResponseEntity<>(dto, HttpStatus.CREATED);
        }

        return new ResponseEntity<>(dto, HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(value = "/{fileName}", method = RequestMethod.GET, produces = "application/pdf")
    public ResponseEntity<Object> get(
            @PathVariable("fileName") String fileName
    ) {
        Long dokumenttiId = Long.valueOf(FilenameUtils.removeExtension(fileName));
        String extension = FilenameUtils.getExtension(fileName);

        byte[] pdfdata = dokumenttiService.get(dokumenttiId);

        // Tarkistetaan tiedostopääte jos asetettu kutsuun
        if (!ObjectUtils.isEmpty(extension) && !Objects.equals(extension, "pdf")) {
            log.error("Got wrong file extension");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        if (pdfdata == null || pdfdata.length == 0) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        if (!dokumenttiService.hasPermission(dokumenttiId)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-disposition", "inline; filename=\"" + dokumenttiId + ".pdf\"");
        DokumenttiDto dokumenttiDto = dokumenttiService.getDto(dokumenttiId);
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
        // estetään googlea indeksoimasta pdf:iä
        headers.set("X-Robots-Tag", "noindex");
        return new ResponseEntity<>(pdfdata, headers, HttpStatus.OK);
    }

    @RequestMapping(value = "/ops", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Long> getLatestDokumenttiId(@RequestParam final Long opsId,
                                                @RequestParam(defaultValue = "fi") final String kieli) {
        return ResponseEntity.ok(dokumenttiService.getLatestValmisDokumenttiId(opsId, Kieli.of(kieli)));
    }

    @RequestMapping(value = "/latest", method = RequestMethod.GET, params = "opsId")
    public ResponseEntity<DokumenttiDto> getLatestDokumentti(@RequestParam("opsId") final Long opsId,
                                                             @RequestParam(defaultValue = "fi") final String kieli) {
        DokumenttiDto dto = dokumenttiService.getLatestDokumentti(opsId, Kieli.of(kieli));
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @RequestMapping(value = "/julkaistu", method = RequestMethod.GET)
    public ResponseEntity<DokumenttiDto> getJulkaistuDokumentti(
            @RequestParam() final Long opsId,
            @RequestParam() final String kieli,
            @RequestParam(required = false) final Integer revision) {
        return ResponseEntity.ok(dokumenttiService.getJulkaistuDokumentti(opsId, Kieli.of(kieli), revision));
    }

    @RequestMapping(value = "/{dokumenttiId}/dokumentti", method = RequestMethod.GET)
    public ResponseEntity<DokumenttiDto> query(@PathVariable final Long dokumenttiId) {
        DokumenttiDto dto = dokumenttiService.query(dokumenttiId);
        if (dto == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(dto, HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/dokumenttikuva", method = RequestMethod.GET)
    public ResponseEntity<DokumenttiKuvaDto> getDokumenttiKuva(@RequestParam Long opsId,
                                                               @RequestParam(defaultValue = "fi") String kieli) {

        DokumenttiKuvaDto dto = dokumenttiKuvaService.getDto(opsId, Kieli.of(kieli));

        if (dto == null) {
            dto = mapper.map(dokumenttiKuvaService.createDtoFor(opsId, Kieli.of(kieli)), DokumenttiKuvaDto.class);
        }
        return ResponseEntity.ok(dto);
    }

    @RequestMapping(value = "/kuva", method = RequestMethod.POST)
    public ResponseEntity<DokumenttiKuvaDto> addImage(
            @RequestParam Long opsId,
            @RequestParam String tyyppi,
            @RequestParam(defaultValue = "fi") String kieli,
            @RequestPart MultipartFile file) throws IOException {

        DokumenttiKuvaDto dto = dokumenttiKuvaService.addImage(opsId, tyyppi, Kieli.of(kieli), file);

        if (dto != null) {
            return ResponseEntity.ok(dto);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/kuva", method = RequestMethod.GET)
    public ResponseEntity<byte[]> getImage(
            @RequestParam Long opsId,
            @RequestParam String tyyppi,
            @RequestParam(defaultValue = "fi") String kieli) {

        byte[] image = dokumenttiKuvaService.getImage(opsId, tyyppi, Kieli.of(kieli));
        if (image == null) {
            return ResponseEntity.notFound().build();
        }
        return new ResponseEntity<>(image, HttpStatus.OK);
    }

    @RequestMapping(value = "/kuva", method = RequestMethod.DELETE)
    public ResponseEntity<Object> deleteImage(
            @RequestParam Long opsId,
            @RequestParam String tyyppi,
            @RequestParam(defaultValue = "fi") String kieli) {

        dokumenttiKuvaService.deleteImage(opsId, tyyppi, Kieli.of(kieli));
        return ResponseEntity.noContent().build();
    }

    @PostMapping(path = "/pdf/data/{dokumenttiId}")
    @ResponseBody
    public ResponseEntity<String> savePdfData(@PathVariable("dokumenttiId") Long dokumenttiId,
                                              @RequestBody PdfData pdfData) {
        dokumenttiService.updateDokumenttiPdfData(Base64.getDecoder().decode(pdfData.getData()), dokumenttiId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(path = "/pdf/tila/{dokumenttiId}")
    @ResponseBody
    public ResponseEntity<String> updateDokumenttiTila(@PathVariable("dokumenttiId") Long dokumenttiId,
                                                       @RequestBody PdfData pdfData) {
        dokumenttiService.updateDokumenttiTila(DokumenttiTila.of(pdfData.getTila()), dokumenttiId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
