/*
 *  Copyright (c) 2013 The Finnish Board of Education - Opetushallitus
 *
 *  This program is free software: Licensed under the EUPL, Version 1.1 or - as
 *  soon as they will be approved by the European Commission - subsequent versions
 *  of the EUPL (the "Licence");
 *
 *  You may not use this work except in compliance with the Licence.
 *  You may obtain a copy of the Licence at: http://ec.europa.eu/idabc/eupl
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  European Union Public Licence for more details.
 */

package fi.vm.sade.eperusteet.ylops.repository.ops;

import fi.vm.sade.eperusteet.ylops.domain.lukio.OpsOppiaineId;
import fi.vm.sade.eperusteet.ylops.domain.lukio.OpsOppiaineParentView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * User: tommiratamaa
 * Date: 9.12.2015
 * Time: 17.01
 */
@Repository
public interface OpsOppiaineParentViewRepository extends JpaRepository<OpsOppiaineParentView, OpsOppiaineId> {
    @Query(value = "select v from OpsOppiaineParentView v where v.opsOppiaine.opetussuunnitelmaId = ?1")
    List<OpsOppiaineParentView> findByOpetusuunnitelmaId(long opsId);

    @Query(value = "select v from OpsOppiaineParentView v where v.opsOppiaine.opetussuunnitelmaId = ?1 and v.tunniste = ?2")
    List<OpsOppiaineParentView> findByTunnisteAndOpetusuunnitelmaId(long opsId, UUID tunniste);
}
