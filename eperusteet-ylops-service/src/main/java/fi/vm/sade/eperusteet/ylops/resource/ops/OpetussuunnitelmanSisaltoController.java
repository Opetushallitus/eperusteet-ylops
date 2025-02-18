package fi.vm.sade.eperusteet.ylops.resource.ops;

import fi.vm.sade.eperusteet.ylops.dto.Reference;
import fi.vm.sade.eperusteet.ylops.dto.RevisionKayttajaDto;
import fi.vm.sade.eperusteet.ylops.dto.teksti.PoistettuTekstiKappaleDto;
import fi.vm.sade.eperusteet.ylops.dto.teksti.TekstiKappaleDto;
import fi.vm.sade.eperusteet.ylops.dto.teksti.TekstiKappaleViiteDto;
import fi.vm.sade.eperusteet.ylops.dto.teksti.TekstiKappaleViitePerusteTekstillaDto;
import fi.vm.sade.eperusteet.ylops.service.ops.OpetussuunnitelmaService;
import fi.vm.sade.eperusteet.ylops.service.ops.TekstiKappaleViiteService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping("/api/opetussuunnitelmat/{opsId}")
@Tag(name = "OpetussuunnitelmanSisalto")
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
    public ResponseEntity<List<RevisionKayttajaDto>> getVersionsForTekstiKappaleViite(
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
    public ResponseEntity<TekstiKappaleViitePerusteTekstillaDto> getTekstiOtsikot(@PathVariable("opsId") final Long opsId) {
        TekstiKappaleViitePerusteTekstillaDto dto = opetussuunnitelmaService.getTekstitPerusteenTeksteilla(opsId);
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
            @PathVariable("viiteId") final Long viiteId
    ) {
        TekstiKappaleViiteDto.Puu dto = tekstiKappaleViiteService.getTekstiKappaleViite(opsId, viiteId, TekstiKappaleViiteDto.Puu.class);
        if (dto == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @RequestMapping(value = "/tekstit/{viiteId}/peruste", method = RequestMethod.GET)
    public ResponseEntity<fi.vm.sade.eperusteet.ylops.service.external.impl.perustedto.TekstiKappaleViiteDto> getPerusteTekstikappale(
            @PathVariable("opsId") final Long opsId,
            @PathVariable("viiteId") final Long viiteId
    ) {
        fi.vm.sade.eperusteet.ylops.service.external.impl.perustedto.TekstiKappaleViiteDto dto
                = tekstiKappaleViiteService.getPerusteTekstikappale(opsId, viiteId);

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

    @GetMapping(value = "/tekstit/{tunniste}/alaopetussuunnitelmalukumaara")
    public Integer getTekstikappaleAlaOpetussuunnitelmaLukumaara(
            @PathVariable("opsId") final Long opsId,
            @PathVariable("tunniste") final String tunniste) {
        return tekstiKappaleViiteService.alaOpetussuunnitelmaLukumaaraTekstikappaleTunniste(opsId, UUID.fromString(tunniste));
    }

}
