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
package fi.vm.sade.eperusteet.ylops.service.external;

import com.fasterxml.jackson.databind.JsonNode;
import fi.vm.sade.eperusteet.ylops.domain.KoulutusTyyppi;
import fi.vm.sade.eperusteet.ylops.dto.peruste.PerusteDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.PerusteInfoDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.TiedoteQueryDto;
import fi.vm.sade.eperusteet.ylops.service.exception.NotExistsException;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * @author nkala
 */
public interface EperusteetService {
    @PreAuthorize("permitAll()")
    PerusteDto getPeruste(String diaariNumero) throws NotExistsException;

    @PreAuthorize("permitAll()")
    PerusteDto getPerusteUpdateCache(String diaarinumero) throws NotExistsException;

    @PreAuthorize("permitAll()")
    List<PerusteInfoDto> findPerusteet();

    @PreAuthorize("permitAll()")
    List<PerusteInfoDto> findPerusteet(boolean forceRefresh);

    @PreAuthorize("permitAll()")
    List<PerusteInfoDto> findPerusteet(Set<KoulutusTyyppi> tyypit);

    @PreAuthorize("permitAll()")
    List<PerusteInfoDto> findPerusopetuksenPerusteet();

    @PreAuthorize("permitAll()")
    List<PerusteInfoDto> findLukiokoulutusPerusteet();

    @PreAuthorize("permitAll()")
    PerusteDto getPerusteById(final Long id);

    @PreAuthorize("permitAll()")
    JsonNode getTiedotteet(Long jalkeen);

    @PreAuthorize("permitAll()")
    JsonNode getTiedotteetHaku(TiedoteQueryDto queryDto);

    @PreAuthorize("permitAll()")
    byte[] getLiite(final Long perusteId, final UUID id);
}
