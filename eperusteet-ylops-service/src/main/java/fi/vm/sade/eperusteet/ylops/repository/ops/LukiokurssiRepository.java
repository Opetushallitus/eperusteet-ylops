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

import fi.vm.sade.eperusteet.ylops.domain.lukio.Lukiokurssi;
import fi.vm.sade.eperusteet.ylops.dto.lukio.LukioKurssiParentDto;
import fi.vm.sade.eperusteet.ylops.repository.version.JpaWithVersioningRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

/**
 * User: tommiratamaa
 * Date: 14.12.2015
 * Time: 20.44
 */
@Repository
public interface LukiokurssiRepository extends JpaWithVersioningRepository<Lukiokurssi, Long> {
    @Query(value = "select findParentKurssi(?1, ?2)", nativeQuery = true)
    Long findParentKurssiIdIfExists(long opsId, long kurssiId);

    @Query(name = "parentViewByOps", nativeQuery = true)
    List<LukioKurssiParentDto> findParentKurssisByOps(long opsId);
}
