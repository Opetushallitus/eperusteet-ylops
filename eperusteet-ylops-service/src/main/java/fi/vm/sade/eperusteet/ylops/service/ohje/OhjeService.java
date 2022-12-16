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
package fi.vm.sade.eperusteet.ylops.service.ohje;

import fi.vm.sade.eperusteet.ylops.dto.ohje.OhjeDto;

import java.util.List;
import java.util.UUID;

import org.springframework.security.core.parameters.P;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * @author mikkom
 */
public interface OhjeService {
    @PreAuthorize("isAuthenticated()")
    OhjeDto getOhje(@P("id") Long id);

    @PreAuthorize("isAuthenticated()")
    List<OhjeDto> getTekstiKappaleOhjeet(@P("uuid") UUID uuid);

    @PreAuthorize("isAuthenticated()")
    OhjeDto addOhje(OhjeDto ohjeDto);

    @PreAuthorize("isAuthenticated()")
    OhjeDto updateOhje(OhjeDto ohjeDto);

    @PreAuthorize("isAuthenticated()")
    void removeOhje(@P("id") Long id);
}
