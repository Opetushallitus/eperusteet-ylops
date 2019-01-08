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
import fi.vm.sade.eperusteet.ylops.dto.ops.*;
import fi.vm.sade.eperusteet.ylops.dto.peruste.PerusteDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.PerusteOppiaineDto;
import fi.vm.sade.eperusteet.ylops.resource.util.Responses;
import fi.vm.sade.eperusteet.ylops.service.audit.EperusteetYlopsAudit;

import static fi.vm.sade.eperusteet.ylops.service.audit.EperusteetYlopsMessageFields.KIELITARJONTA;
import static fi.vm.sade.eperusteet.ylops.service.audit.EperusteetYlopsMessageFields.OPPIAINE;
import static fi.vm.sade.eperusteet.ylops.service.audit.EperusteetYlopsMessageFields.VALINNAINEN;
import static fi.vm.sade.eperusteet.ylops.service.audit.EperusteetYlopsOperation.KLOONAUS;
import static fi.vm.sade.eperusteet.ylops.service.audit.EperusteetYlopsOperation.LISAYS;
import static fi.vm.sade.eperusteet.ylops.service.audit.EperusteetYlopsOperation.MUOKKAUS;
import static fi.vm.sade.eperusteet.ylops.service.audit.EperusteetYlopsOperation.PALAUTUS;
import static fi.vm.sade.eperusteet.ylops.service.audit.EperusteetYlopsOperation.PALAUTUSALKUPERAISEEN;
import static fi.vm.sade.eperusteet.ylops.service.audit.EperusteetYlopsOperation.POISTO;

import fi.vm.sade.eperusteet.ylops.service.audit.LogMessage;
import fi.vm.sade.eperusteet.ylops.service.ops.OpetussuunnitelmaService;
import fi.vm.sade.eperusteet.ylops.service.ops.OppiaineService;
import io.swagger.annotations.Api;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author mikkom
 */
@RestController
@RequestMapping("/opetussuunnitelmat/{opsId}/oppiaineet")
@Api(value = "Oppiaineet")
public class OppiaineController {
    @Autowired
    private EperusteetYlopsAudit audit;


    @Autowired
    private OppiaineService oppiaineService;

    @Autowired
    private OpetussuunnitelmaService ops;

    @RequestMapping(method = RequestMethod.POST)
    public OppiaineDto add(@PathVariable final Long opsId, @RequestBody OppiaineDto dto) {
        return audit.withAudit(LogMessage.builder(opsId, OPPIAINE, LISAYS), (Void) -> {
            return oppiaineService.add(opsId, dto);
        });
    }

    @RequestMapping(value = "/valinnainen", method = RequestMethod.POST)
    public OppiaineDto addValinnainen(@PathVariable final Long opsId, @RequestBody OppiaineenTallennusDto dto) {
        return audit.withAudit(LogMessage.builder(opsId, VALINNAINEN, LISAYS), (Void) -> {
            return oppiaineService.addValinnainen(
                    opsId, dto.getOppiaine(),
                    dto.getVuosiluokkakokonaisuusId(),
                    dto.getVuosiluokat(),
                    dto.getTavoitteet(), null, null, false);
        });
    }

    @RequestMapping(value = "/{id}/kielitarjonta", method = RequestMethod.POST)
    public OppiaineDto addOppimaara(
            @PathVariable final Long opsId,
            @PathVariable final Long id,
            @RequestBody KopioOppimaaraDto kt) {
        return audit.withAudit(LogMessage.builder(opsId, KIELITARJONTA, LISAYS), (Void) -> {
            return oppiaineService.addCopyOppimaara(opsId, id, kt);
        });
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<UnwrappedOpsOppiaineDto> get(
            @PathVariable final Long opsId,
            @PathVariable final Long id
    ) {
        return Responses.ofNullable(new UnwrappedOpsOppiaineDto(oppiaineService.get(opsId, id)));
    }

    @RequestMapping(value = "/{id}/versio/{versio}", method = RequestMethod.GET)
    public ResponseEntity<UnwrappedOpsOppiaineDto> getVersion(
            @PathVariable final Long opsId,
            @PathVariable final Long id,
            @PathVariable final Integer versio) {
        return Responses.ofNullable(new UnwrappedOpsOppiaineDto(oppiaineService.getVersion(opsId, id, versio)));
    }

    @RequestMapping(value = "/{id}/raakaversio/{versio}", method = RequestMethod.GET)
    public ResponseEntity<OppiaineDto> getRevision(
            @PathVariable final Long opsId,
            @PathVariable final Long id,
            @PathVariable final Integer versio) {
        return Responses.ofNullable(oppiaineService.getRevision(opsId, id, versio));
    }

    @RequestMapping(value = "/{id}/versio/{versio}", method = RequestMethod.POST)
    public ResponseEntity<UnwrappedOpsOppiaineDto> revertToVersion(
            @PathVariable final Long opsId,
            @PathVariable final Long id,
            @PathVariable final Integer versio) {
        return audit.withAudit(LogMessage.builder(opsId, OPPIAINE, PALAUTUS)
                .palautus(id, versio.longValue()), (Void) -> {
            return Responses.ofNullable(new UnwrappedOpsOppiaineDto(oppiaineService.revertTo(opsId, id, versio)));
        });
    }

    @RequestMapping(value = "/{id}/palauta", method = RequestMethod.POST)
    public OppiainePalautettuDto restoreOppiaine(
            @PathVariable final Long opsId,
            @PathVariable final Long id) {
        return audit.withAudit(LogMessage.builder(opsId, OPPIAINE, PALAUTUS), (Void) -> {
            return oppiaineService.restore(opsId, id, null);
        });
    }

    @RequestMapping(value = "/{id}/palauta/{oppimaaraId}", method = RequestMethod.POST)
    public OppiainePalautettuDto restoreOppiaine(
            @PathVariable final Long opsId,
            @PathVariable final Long id,
            @PathVariable final Long oppimaaraId) {
        return audit.withAudit(LogMessage.builder(opsId, OPPIAINE, PALAUTUS), (Void) -> {
            return oppiaineService.restore(opsId, id, oppimaaraId);
        });
    }

    @RequestMapping(value = "/{id}/palauta/{oppimaaraId}/{versio}", method = RequestMethod.POST)
    public OppiainePalautettuDto restoreOppiaine(
            @PathVariable final Long opsId,
            @PathVariable final Long id,
            @PathVariable final Long oppimaaraId,
            @PathVariable final Integer versio) {
        return audit.withAudit(LogMessage.builder(opsId, OPPIAINE, PALAUTUS)
                .add("oppiaineId", id)
                .palautus(oppimaaraId, versio.longValue()), (Void) -> {
            return oppiaineService.restore(opsId, id, oppimaaraId, versio);
        });
    }

    @RequestMapping(value = "/{id}/versiot", method = RequestMethod.GET)
    public List<RevisionDto> getVersionHistory(@PathVariable final Long opsId, @PathVariable final Long id) {
        return oppiaineService.getVersions(opsId, id);
    }

    @RequestMapping(value = "/poistetut", method = RequestMethod.GET)
    public ResponseEntity<List<PoistettuOppiaineDto>> getRemoved(@PathVariable final Long opsId) {
        return Responses.ofNullable(oppiaineService.getRemoved(opsId));
    }

    @RequestMapping(value = "/{id}/parent", method = RequestMethod.GET)
    public ResponseEntity<OppiaineDto> getParent(@PathVariable final Long opsId, @PathVariable final Long id) {
        return Responses.ofNullable(oppiaineService.getParent(opsId, id));
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<OppiaineDto> getAll(@PathVariable final Long opsId,
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
    public UnwrappedOpsOppiaineDto update(@PathVariable final Long opsId, @PathVariable final Long id,
                                          @RequestBody OppiaineDto dto) {
        return audit.withAudit(LogMessage.builder(opsId, OPPIAINE, MUOKKAUS), (Void) -> {
            dto.setId(id);
            return new UnwrappedOpsOppiaineDto(oppiaineService.update(opsId, dto));
        });
    }

    @RequestMapping(value = "/{id}/valinnainen", method = RequestMethod.POST)
    public OppiaineDto updateValinnainen(
            @PathVariable final Long opsId,
            @PathVariable final Long id,
            @RequestBody OppiaineenTallennusDto dto
    ) {
        return audit.withAudit(LogMessage.builder(opsId, VALINNAINEN, MUOKKAUS), (Void) -> {
            dto.getOppiaine().setId(id);
            return oppiaineService.updateValinnainen(
                    opsId, dto.getOppiaine(),
                    dto.getVuosiluokkakokonaisuusId(),
                    dto.getVuosiluokat(),
                    dto.getTavoitteet());
        });
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable final Long opsId, @PathVariable final Long id) {
        audit.withAudit(LogMessage.builder(opsId, OPPIAINE, POISTO), (Void) -> {
            oppiaineService.delete(opsId, id);
            return null;
        });
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
//        return audit.withAudit(LogMessage.builder(opsId, OPPIAINE, MUOKKAUSKO), (Void) -> {
//            return null;
//        });
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
        return audit.withAudit(LogMessage.builder(opsId, OPPIAINE, KLOONAUS), (Void) -> {
            return new UnwrappedOpsOppiaineDto(oppiaineService.kopioiMuokattavaksi(opsId, id));
        });
    }

    @RequestMapping(value = "/{id}/palautaYlempi", method = RequestMethod.POST)
    public UnwrappedOpsOppiaineDto palautaYlempi(
            @PathVariable final Long opsId,
            @PathVariable final Long id) {
        return audit.withAudit(LogMessage.builder(opsId, OPPIAINE, PALAUTUSALKUPERAISEEN), (Void) -> {
            return new UnwrappedOpsOppiaineDto(oppiaineService.palautaYlempi(opsId, id));
        });
    }

}
