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
package fi.vm.sade.eperusteet.ylops.service.ops;

import fi.vm.sade.eperusteet.ylops.domain.Vuosiluokka;
import fi.vm.sade.eperusteet.ylops.domain.oppiaine.OppiaineTyyppi;
import fi.vm.sade.eperusteet.ylops.dto.RevisionDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.*;
import fi.vm.sade.eperusteet.ylops.dto.teksti.TekstiosaDto;
import fi.vm.sade.eperusteet.ylops.service.locking.LockService;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * @author mikkom
 */
public interface OppiaineService extends LockService<OpsOppiaineCtx> {
    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')")
    void updateVuosiluokkienTavoitteet(@P("opsId") Long opsId, Long oppiaineId, Long vlkId, Map<Vuosiluokka, Set<UUID>> tavoitteet);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    List<OppiaineDto> getAll(@P("opsId") Long opsId);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    List<OppiaineDto> getAll(@P("opsId") Long opsId, boolean valinnaiset);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    List<OppiaineDto> getAll(@P("opsId") Long opsId, OppiaineTyyppi tyyppi);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    OpsOppiaineDto get(@P("opsId") Long opsId, Long id);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    OppiaineDto getParent(@P("opsId") Long opsId, Long id);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')")
    OppiaineDto add(@P("opsId") Long opsId, OppiaineDto oppiaineDto);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')")
    OppiaineDto addCopyOppimaara(@P("opsId") Long opsId, Long oppiaineId, KopioOppimaaraDto oppiaineDto);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')")
    OppiaineLaajaDto add(@P("opsId") Long opsId, OppiaineLaajaDto oppiaineDto);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')")
    OppiaineDto addValinnainen(@P("opsId") Long opsId, OppiaineDto oppiaineDto, Long vlkId,
                               Set<Vuosiluokka> vuosiluokat, List<TekstiosaDto> tavoitteetDto, Integer oldJnro,
                               OppiaineenVuosiluokkakokonaisuusDto oaVlk, boolean updateOld);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')")
    OpsOppiaineDto update(@P("opsId") Long opsId, OppiaineDto oppiaineDto);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')")
    OpsOppiaineDto update(Long opsId, Long vuosiluokkakokonaisuusId, OppiaineDto oppiaineDto);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')")
    OppiaineDto updateValinnainen(@P("opsId") Long opsId, OppiaineDto oppiaineDto, Long vlkId,
                                  Set<Vuosiluokka> vuosiluokat, List<TekstiosaDto> tavoitteet);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')")
    OppiaineDto updateYksinkertainen(@P("opsId") Long opsId, OppiaineDto oppiaineDto, Long vlkId,
                                  Set<Vuosiluokka> vuosiluokat, List<TekstiosaDto> tavoitteet);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'POISTO')")
    List<OppiaineLaajaDto> getAllVersions(@P("opsId") Long opsId, Long oppiaineId);

    @PreAuthorize("hasPermission(null, 'pohja', 'LUONTI') || hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')")
    OppiainePalautettuDto restore(@P("opsId") Long opsId, Long oppiaineId, Long oppimaaraId);

    @PreAuthorize("hasPermission(null, 'pohja', 'LUONTI') || hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')")
    OppiainePalautettuDto restore(@P("opsId") Long opsId, Long oppiaineId, Long oppimaaraId, Integer versio);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')")
    OpsOppiaineDto kopioiMuokattavaksi(@P("opsId") Long opsId, Long id, boolean asetaPohjanOppiaine);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')")
    PoistettuOppiaineDto delete(@P("opsId") Long opsId, Long id);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')")
    OppiaineenVuosiluokkakokonaisuusDto updateVuosiluokkakokonaisuudenSisalto(@P("opsId") Long opsId, Long id, OppiaineenVuosiluokkakokonaisuusDto dto);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    OppiaineenVuosiluokkaDto getVuosiluokka(@P("opsId") Long opsId, Long oppiaineId, Long vuosiluokkaId);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')")
    OppiaineenVuosiluokkaDto updateVuosiluokanSisalto(@P("opsId") Long opsId, Long id, Long kokonaisuusId, OppiaineenVuosiluokkaDto dto);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')")
    OppiaineenVuosiluokkaDto updateValinnaisenVuosiluokanSisalto(@P("opsId") Long opsId, Long id,
                                                                 Long oppiaineenVuosiluokkaId,
                                                                 List<TekstiosaDto> tavoitteetDto);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'POISTO')")
    OpsOppiaineDto palautaYlempi(@P("opsId") Long opsId, Long id);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    List<RevisionDto> getVersions(@P("opsId") Long opsId, Long id);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    OpsOppiaineDto getVersion(@P("opsId") Long opsId, Long id, Integer versio);

    @PreAuthorize("hasPermission(null, 'pohja', 'LUONTI')")
    OppiaineDto getRevision(Long opsId, Long id, Integer versio);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')")
    OpsOppiaineDto revertTo(@P("opsId") Long opsId, Long id, Integer versio);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    List<PoistettuOppiaineDto> getRemoved(@P("opsId") Long opsId);
}
