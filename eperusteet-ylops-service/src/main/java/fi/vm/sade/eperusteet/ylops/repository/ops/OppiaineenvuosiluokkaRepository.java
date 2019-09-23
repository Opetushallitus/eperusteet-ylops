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
package fi.vm.sade.eperusteet.ylops.repository.ops;

import fi.vm.sade.eperusteet.ylops.domain.oppiaine.Oppiaineenvuosiluokka;
import fi.vm.sade.eperusteet.ylops.repository.version.JpaWithVersioningRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * @author jhyoty
 */
@Repository
public interface OppiaineenvuosiluokkaRepository extends JpaWithVersioningRepository<Oppiaineenvuosiluokka, Long> {
    @Query(value = "SELECT CASE WHEN COUNT(*) > 0 THEN true ELSE false END FROM Oppiaine o JOIN o.vuosiluokkakokonaisuudet k JOIN k.vuosiluokat v WHERE o.id = ?1 AND k.id=?2 AND v.id = ?3")
    boolean exists(long oppiaineId, long kokonaisuusId, long vuosiluokkaId);

    @Query(value = "SELECT vl FROM Oppiaine o JOIN o.vuosiluokkakokonaisuudet k JOIN k.vuosiluokat vl WHERE o.id = ?1 AND vl.id = ?2")
    Oppiaineenvuosiluokka findByOppiaine(Long oppiaineId, Long vuosiluokkaId);

    @Query(value = "SELECT vl FROM Oppiaine o JOIN o.vuosiluokkakokonaisuudet k JOIN k.vuosiluokat vl WHERE o.id = ?1 AND vl.id = ?2")
    Oppiaineenvuosiluokka findByOpsAndOppiaine(Long opsId, Long oppiaineId, Long vuosiluokkaId);
}
