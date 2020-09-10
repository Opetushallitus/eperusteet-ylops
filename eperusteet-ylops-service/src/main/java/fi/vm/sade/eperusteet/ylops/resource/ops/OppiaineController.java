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

import fi.vm.sade.eperusteet.ylops.domain.oppiaine.OppiaineTyyppi;
import fi.vm.sade.eperusteet.ylops.dto.RevisionDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.KopioOppimaaraDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OppiaineDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OppiainePalautettuDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OppiaineenTallennusDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.PoistettuOppiaineDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.UnwrappedOpsOppiaineDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.PerusteDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.PerusteOppiaineDto;
import fi.vm.sade.eperusteet.ylops.resource.util.Responses;
import fi.vm.sade.eperusteet.ylops.service.ops.OpetussuunnitelmaService;
import fi.vm.sade.eperusteet.ylops.service.ops.OppiaineService;
import fi.vm.sade.eperusteet.ylops.service.ops.PoistoService;
import io.swagger.annotations.Api;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author mikkom
 */
@RestController
@RequestMapping("/opetussuunnitelmat/{opsId}/oppiaineet")
@Api(value = "Oppiaineet")
public class OppiaineController {

    @Autowired
    private OppiaineService oppiaineService;

    @Autowired
    private OpetussuunnitelmaService ops;

    @Autowired
    private PoistoService poistoService;

    @RequestMapping(method = RequestMethod.POST)
    public OppiaineDto addOppiaine(@PathVariable final Long opsId, @RequestBody OppiaineDto dto) {
        return oppiaineService.add(opsId, dto);
    }

    @RequestMapping(value = "/valinnainen", method = RequestMethod.POST)
    public OppiaineDto addValinnainen(@PathVariable final Long opsId, @RequestBody OppiaineenTallennusDto dto) {
        return oppiaineService.addValinnainen(
                opsId,
                dto.getOppiaine(),
                dto.getVuosiluokkakokonaisuusId(),
                dto.getVuosiluokat(),
                dto.getTavoitteet(), null, null, false);
    }

    @RequestMapping(value = "/{id}/kielitarjonta", method = RequestMethod.POST)
    public OppiaineDto addOppimaara(
            @PathVariable final Long opsId,
            @PathVariable final Long id,
            @RequestBody KopioOppimaaraDto kt) {
        return oppiaineService.addCopyOppimaara(opsId, id, kt);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<UnwrappedOpsOppiaineDto> getOppiaine(
            @PathVariable final Long opsId,
            @PathVariable final Long id
    ) {
        return Responses.ofNullable(new UnwrappedOpsOppiaineDto(oppiaineService.get(opsId, id)));
    }

    @RequestMapping(value = "/{id}/versio/{versio}", method = RequestMethod.GET)
    public ResponseEntity<UnwrappedOpsOppiaineDto> getOppiaineVersion(
            @PathVariable final Long opsId,
            @PathVariable final Long id,
            @PathVariable final Integer versio) {
        return Responses.ofNullable(new UnwrappedOpsOppiaineDto(oppiaineService.getVersion(opsId, id, versio)));
    }

    @RequestMapping(value = "/{id}/raakaversio/{versio}", method = RequestMethod.GET)
    public ResponseEntity<OppiaineDto> getOppiaineRevision(
            @PathVariable final Long opsId,
            @PathVariable final Long id,
            @PathVariable final Integer versio) {
        return Responses.ofNullable(oppiaineService.getRevision(opsId, id, versio));
    }

    @RequestMapping(value = "/{id}/versio/{versio}", method = RequestMethod.POST)
    public ResponseEntity<UnwrappedOpsOppiaineDto> revertOppiaineToVersion(
            @PathVariable final Long opsId,
            @PathVariable final Long id,
            @PathVariable final Integer versio) {
        return Responses.ofNullable(new UnwrappedOpsOppiaineDto(oppiaineService.revertTo(opsId, id, versio)));
    }

    @RequestMapping(value = "/{id}/palauta", method = RequestMethod.POST)
    public OppiainePalautettuDto restoreOppiaine(
            @PathVariable final Long opsId,
            @PathVariable final Long id) {
        return poistoService.restoreOppiaine(opsId, id);
    }

    @RequestMapping(value = "/{id}/palauta/{oppimaaraId}", method = RequestMethod.POST)
    public OppiainePalautettuDto restoreOppiaineOppimaara(
            @PathVariable final Long opsId,
            @PathVariable final Long id,
            @PathVariable final Long oppimaaraId) {
        return poistoService.restoreOppiaine(opsId, id);
    }

    @RequestMapping(value = "/{id}/palauta/{oppimaaraId}/{versio}", method = RequestMethod.POST)
    public OppiainePalautettuDto restoreOppiaineOppimaaraVersio(
            @PathVariable final Long opsId,
            @PathVariable final Long id,
            @PathVariable final Long oppimaaraId,
            @PathVariable final Integer versio) {
        return oppiaineService.restore(opsId, id, oppimaaraId, versio);
    }

    @RequestMapping(value = "/{id}/versiot", method = RequestMethod.GET)
    public List<RevisionDto> getOppiaineVersionHistory(@PathVariable final Long opsId, @PathVariable final Long id) {
        return oppiaineService.getVersions(opsId, id);
    }

    @RequestMapping(value = "/poistetut", method = RequestMethod.GET)
    public ResponseEntity<List<PoistettuOppiaineDto>> getRemovedOppiaineet(@PathVariable final Long opsId) {
        return Responses.ofNullable(oppiaineService.getRemoved(opsId));
    }

    @RequestMapping(value = "/{id}/parent", method = RequestMethod.GET)
    public ResponseEntity<OppiaineDto> getOppiaineParent(@PathVariable final Long opsId, @PathVariable final Long id) {
        return Responses.ofNullable(oppiaineService.getParent(opsId, id));
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<OppiaineDto> getAllOppiaineet(@PathVariable final Long opsId,
                                    @RequestParam(value = "tyyppi", required = false) OppiaineTyyppi tyyppi) {
        if (tyyppi == null) {
            return oppiaineService.getAll(opsId, OppiaineTyyppi.YHTEINEN);
        } else {
            return oppiaineService.getAll(opsId, tyyppi);
        }
    }

    @RequestMapping(value = "/yhteiset", method = RequestMethod.GET)
    public List<OppiaineDto> getYhteiset(@PathVariable final Long opsId) {
        return oppiaineService.getAll(opsId, false);
    }

    @RequestMapping(value = "/valinnaiset", method = RequestMethod.GET)
    public List<OppiaineDto> getValinnaiset(@PathVariable final Long opsId) {
        return oppiaineService.getAll(opsId, true);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.POST)
    public UnwrappedOpsOppiaineDto updateOppiaine(@PathVariable final Long opsId, @PathVariable final Long id,
                                                  @RequestBody OppiaineDto dto) {
        dto.setId(id);
        return new UnwrappedOpsOppiaineDto(oppiaineService.update(opsId, dto));
    }

    @RequestMapping(value = "/{id}/vlk/{vlkId}", method = RequestMethod.POST)
    public UnwrappedOpsOppiaineDto updateOppiaineWithVlk(@PathVariable final Long opsId, @PathVariable final Long vlkId, @PathVariable final Long id,
                                                  @RequestBody OppiaineDto dto) {
        dto.setId(id);
        return new UnwrappedOpsOppiaineDto(oppiaineService.update(opsId, vlkId, dto));
    }

    @RequestMapping(value = "/{id}/valinnainen", method = RequestMethod.POST)
    public OppiaineDto updateValinnainen(
            @PathVariable final Long opsId,
            @PathVariable final Long id,
            @RequestBody OppiaineenTallennusDto dto
    ) {
        dto.getOppiaine().setId(id);
        return oppiaineService.updateValinnainen(
                opsId,
                dto.getOppiaine(),
                dto.getVuosiluokkakokonaisuusId(),
                dto.getVuosiluokat(),
                dto.getTavoitteet());
    }

    @RequestMapping(value = "/{id}/yksinkertainen", method = RequestMethod.POST)
    public OppiaineDto updateYksinkertainen(
            @PathVariable final Long opsId,
            @PathVariable final Long id,
            @RequestBody OppiaineenTallennusDto dto
    ) {
        dto.getOppiaine().setId(id);
        return oppiaineService.updateYksinkertainen(
                opsId,
                dto.getOppiaine(),
                dto.getVuosiluokkakokonaisuusId(),
                dto.getVuosiluokat(),
                dto.getTavoitteet());
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOppiaine(@PathVariable final Long opsId, @PathVariable final Long id) {
        oppiaineService.delete(opsId, id);
    }

//    @RequestMapping(value = "/{id}/versions", method = RequestMethod.GET)
//    @ApiIgnore
//    @ResponseBody
//    public List<OppiaineLaajaDto> getAllVersions(@PathVariable("opsId") final Long opsId, @PathVariable("id") final Long id) {
//        return oppiaineService.getAllVersions(opsId, id);
//    }
//
//    @RequestMapping(value = "/{id}/restore", method = RequestMethod.POST)
//    @ApiIgnore
//    @ResponseBody
//    public OppiaineLaajaDto restore(@PathVariable("opsId") final Long opsId, @PathVariable("id") final Long id) {
//        return oppiaineService.restore(opsId, id);
//    }

    @RequestMapping(value = "/{id}/peruste", method = RequestMethod.GET)
    public ResponseEntity<PerusteOppiaineDto> getPerusteSisalto(
            @PathVariable final Long opsId,
            @PathVariable final Long id
    ) {
        PerusteDto p = ops.getPeruste(opsId);
        return Responses.of(Optional.ofNullable(oppiaineService.get(opsId, id))
                .flatMap(a -> p.getPerusopetus().getOppiaine(a.getOppiaine().getTunniste())));
    }

    @RequestMapping(value = "/{id}/muokattavakopio", method = RequestMethod.POST)
    public UnwrappedOpsOppiaineDto kopioiMuokattavaksi(
            @PathVariable final Long opsId,
            @PathVariable final Long id) {
        return new UnwrappedOpsOppiaineDto(oppiaineService.kopioiMuokattavaksi(opsId, id));
    }

    @RequestMapping(value = "/{id}/palautaYlempi", method = RequestMethod.POST)
    public UnwrappedOpsOppiaineDto palautaYlempi(
            @PathVariable final Long opsId,
            @PathVariable final Long id) {
        return new UnwrappedOpsOppiaineDto(oppiaineService.palautaYlempi(opsId, id));
    }

}
