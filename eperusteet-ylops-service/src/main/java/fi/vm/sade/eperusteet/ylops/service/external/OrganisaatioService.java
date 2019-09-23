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
import fi.vm.sade.eperusteet.ylops.dto.koodisto.OrganisaatioLaajaDto;
import fi.vm.sade.eperusteet.ylops.dto.koodisto.OrganisaatioQueryDto;
import java.util.List;
import java.util.Set;

import org.springframework.security.access.prepost.PreAuthorize;

/**
 * @author mikkom
 */
public interface OrganisaatioService {

    @PreAuthorize("permitAll()")
    JsonNode getOrganisaatio(String organisaatioOid);

    @PreAuthorize("isAuthenticated()")
    JsonNode getOrganisaatioVirkailijat(Set<String> organisaatioOids);

    @PreAuthorize("isAuthenticated()")
    JsonNode getPeruskoulutByKuntaId(String kuntaId);

    @PreAuthorize("isAuthenticated()")
    List<JsonNode> getRyhmat();

    @PreAuthorize("isAuthenticated()")
    JsonNode getLukiotByKuntaId(String kuntaId);

    @PreAuthorize("isAuthenticated()")
    JsonNode getPeruskoulutByOid(String oid);

    @PreAuthorize("isAuthenticated()")
    JsonNode getPeruskoulutoimijat(List<String> kuntaIdt);

    @PreAuthorize("isAuthenticated()")
    JsonNode getLukioByOid(String oid);

    @PreAuthorize("isAuthenticated()")
    JsonNode getLukiotoimijat(List<String> kuntaIdt);

    @PreAuthorize("isAuthenticated()")
    List<OrganisaatioLaajaDto> getKoulutustoimijat(OrganisaatioQueryDto query);
}
