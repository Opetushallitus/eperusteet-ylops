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

import com.wordnik.swagger.annotations.Api;
import fi.vm.sade.eperusteet.ylops.domain.oppiaine.OppiaineTyyppi;
import fi.vm.sade.eperusteet.ylops.dto.RevisionDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.*;
import fi.vm.sade.eperusteet.ylops.dto.peruste.PerusteDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.PerusteOppiaineDto;
import fi.vm.sade.eperusteet.ylops.resource.util.Responses;
import fi.vm.sade.eperusteet.ylops.service.ops.OpetussuunnitelmaService;
import fi.vm.sade.eperusteet.ylops.service.ops.OppiaineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author mikkom
 */
@RestController
@RequestMapping("/opetussuunnitelmat/{opsId}/oppiaineet")
@Api(value = "Opetussuunnitelmat")
public class OppiaineController {

    @Autowired
    private OppiaineService oppiaineService;

    @Autowired
    private OpetussuunnitelmaService ops;

    @RequestMapping(method = RequestMethod.POST)
    public OppiaineDto add(@PathVariable("opsId") final Long opsId, @RequestBody OppiaineDto dto) {
        return oppiaineService.add(opsId, dto);
    }

    @RequestMapping(value = "/valinnainen", method = RequestMethod.POST)
    public OppiaineDto addValinnainen(@PathVariable("opsId") final Long opsId, @RequestBody OppiaineenTallennusDto dto) {
        return oppiaineService.addValinnainen(opsId, dto.getOppiaine(), dto.getVuosiluokkakokonaisuusId(),
                                              dto.getVuosiluokat(), dto.getTavoitteet(), null);
    }

    @RequestMapping(value = "/{id}/kielitarjonta", method = RequestMethod.POST)
    public OppiaineDto addOppimaara(
            @PathVariable("opsId") final Long opsId,
            @PathVariable("id") final Long oppiaineId,
            @RequestBody KopioOppimaaraDto kt) {
        return oppiaineService.addCopyOppimaara(opsId, oppiaineId, kt);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<UnwrappedOpsOppiaineDto> get(@PathVariable("opsId") final Long opsId, @PathVariable("id") final Long id) {
        return Responses.ofNullable(new UnwrappedOpsOppiaineDto(oppiaineService.get(opsId, id)));
    }

    @RequestMapping(value = "/{id}/versio/{versio}", method = RequestMethod.GET)
    public ResponseEntity<UnwrappedOpsOppiaineDto> getVersion(
            @PathVariable("opsId") final Long opsId,
            @PathVariable("id") final Long id,
            @PathVariable("versio") final Integer versio) {
        return Responses.ofNullable(new UnwrappedOpsOppiaineDto(oppiaineService.getVersion(opsId, id, versio)));
    }

    @RequestMapping(value = "/{id}/versio/{versio}", method = RequestMethod.POST)
    public ResponseEntity<UnwrappedOpsOppiaineDto> revertToVersion(
            @PathVariable("opsId") final Long opsId,
            @PathVariable("id") final Long id,
            @PathVariable("versio") final Integer versio) {
        return Responses.ofNullable(new UnwrappedOpsOppiaineDto(oppiaineService.revertTo(opsId, id, versio)));
    }

    @RequestMapping(value = "/{id}/palauta", method = RequestMethod.POST)
    public OppiainePalautettuDto restoreOppiaine(
            @PathVariable("opsId") final Long opsId,
            @PathVariable("id") final Long id) {
        return oppiaineService.restore(opsId, id, null);
    }

    @RequestMapping(value = "/{id}/palauta/{oppimaaraId}", method = RequestMethod.POST)
    public OppiainePalautettuDto restoreOppiaine(
            @PathVariable("opsId") final Long opsId,
            @PathVariable("id") final Long id,
            @PathVariable("oppimaaraId") final Long oppimaaraId) {
        return oppiaineService.restore(opsId, id, oppimaaraId);
    }

    @RequestMapping(value = "/{id}/versiot", method = RequestMethod.GET)
    public List<RevisionDto> getVersionHistory(@PathVariable("opsId") final Long opsId, @PathVariable("id") final Long id) {
        return oppiaineService.getVersions(opsId, id);
    }

    @RequestMapping(value = "/poistetut", method = RequestMethod.GET)
    public ResponseEntity<List<PoistettuOppiaineDto>> getRemoved(@PathVariable("opsId") final Long opsId) {
        return Responses.ofNullable(oppiaineService.getRemoved(opsId));
    }

    @RequestMapping(value = "/{id}/parent", method = RequestMethod.GET)
    public ResponseEntity<OppiaineDto> getParent(@PathVariable("opsId") final Long opsId, @PathVariable("id") final Long id) {
        return Responses.ofNullable(oppiaineService.getParent(opsId, id));
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<OppiaineDto> getAll(@PathVariable("opsId") final Long opsId,
                                    @RequestParam(value = "tyyppi", required = false) OppiaineTyyppi tyyppi) {
        if (tyyppi == null) {
            return oppiaineService.getAll(opsId, OppiaineTyyppi.YHTEINEN);
        } else {
            return oppiaineService.getAll(opsId, tyyppi);
        }
    }

    @RequestMapping(value = "/yhteiset",method = RequestMethod.GET)
    public List<OppiaineDto> getYhteiset(@PathVariable("opsId") final Long opsId) {
        return oppiaineService.getAll(opsId, false);
    }

    @RequestMapping(value = "/valinnaiset",method = RequestMethod.GET)
    public List<OppiaineDto> getValinnaiset(@PathVariable("opsId") final Long opsId) {
        return oppiaineService.getAll(opsId, true);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.POST)
    public UnwrappedOpsOppiaineDto update(@PathVariable("opsId") final Long opsId, @PathVariable("id") final Long id,
        @RequestBody OppiaineDto dto) {
        dto.setId(id);
        return new UnwrappedOpsOppiaineDto(oppiaineService.update(opsId, dto));
    }

    @RequestMapping(value = "/{id}/valinnainen", method = RequestMethod.POST)
    public OppiaineDto updateValinnainen(@PathVariable("opsId") final Long opsId, @PathVariable("id") final Long id,
                                         @RequestBody OppiaineenTallennusDto dto) {
        dto.getOppiaine().setId(id);
        return oppiaineService.updateValinnainen(opsId, dto.getOppiaine(), dto.getVuosiluokkakokonaisuusId(),
                                                 dto.getVuosiluokat(), dto.getTavoitteet());
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("opsId") final Long opsId, @PathVariable("id") final Long id) {
        oppiaineService.delete(opsId, id);
    }

    @RequestMapping(value = "/{id}/peruste", method = RequestMethod.GET)
    public ResponseEntity<PerusteOppiaineDto> getPerusteSisalto(@PathVariable("opsId") final Long opsId, @PathVariable("id") final Long id) {
        PerusteDto p = ops.getPeruste(opsId);
        return Responses.of(Optional.ofNullable(oppiaineService.get(opsId, id))
            .flatMap(a -> p.getPerusopetus().getOppiaine(a.getOppiaine().getTunniste())));
    }

    @RequestMapping(value = "/{id}/muokattavakopio", method = RequestMethod.POST)
    public UnwrappedOpsOppiaineDto kopioiMuokattavaksi(@PathVariable("opsId") final Long opsId,
                                                       @PathVariable("id") final Long id) {
        return new UnwrappedOpsOppiaineDto(oppiaineService.kopioiMuokattavaksi(opsId, id));
    }

    @RequestMapping(value = "/{id}/palautaYlempi", method = RequestMethod.POST)
    public UnwrappedOpsOppiaineDto palautaYlempi(@PathVariable("opsId") final Long opsId,
                                                 @PathVariable("id") final Long id) {
        return new UnwrappedOpsOppiaineDto(oppiaineService.palautaYlempi(opsId, id));
    }

    @RequestMapping(value = "/{id}/palautettavissa", method = RequestMethod.GET)
    public HashMap<String, Boolean> palautettavissa(@PathVariable("opsId") final Long opsId,
                                   @PathVariable("id") final Long id) {
        return new HashMap<String, Boolean>(1){{put("palautettavissa", oppiaineService.palautettavissa(opsId, id));}};
    }

}
