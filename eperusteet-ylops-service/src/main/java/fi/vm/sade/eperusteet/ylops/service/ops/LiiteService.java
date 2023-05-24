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

import fi.vm.sade.eperusteet.ylops.dto.liite.LiiteDto;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.UUID;

/**
 * @author jhyoty
 */
public interface LiiteService {

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')")
    UUID add(@P("opsId") final Long opsId, String tyyppi, String nimi, long length, InputStream is);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU') or isAuthenticated()")
    LiiteDto get(@P("opsId") Long opsId, UUID id);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU') or isAuthenticated()")
    List<LiiteDto> getAll(@P("opsId") Long opsId);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')")
    void delete(@P("opsId") Long opsId, UUID id);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU') or isAuthenticated()")
    void export(@P("opsId") final Long opsId, UUID id, OutputStream os);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU') or isAuthenticated()")
    void exportLiitePerusteelta(@P("opsId") final Long opsId, UUID id, OutputStream os);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    InputStream export(@P("opsId") final Long opsId, UUID id, final Long perusteId);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    InputStream exportLiitePerusteelta(@P("opsId") final Long opsId, UUID id, final Long perusteId);
}
