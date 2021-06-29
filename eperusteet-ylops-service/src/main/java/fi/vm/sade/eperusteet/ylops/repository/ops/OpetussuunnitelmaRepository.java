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

import fi.vm.sade.eperusteet.ylops.domain.KoulutusTyyppi;
import fi.vm.sade.eperusteet.ylops.domain.Tila;
import fi.vm.sade.eperusteet.ylops.domain.Tyyppi;
import fi.vm.sade.eperusteet.ylops.domain.ops.Opetussuunnitelma;
import fi.vm.sade.eperusteet.ylops.repository.version.JpaWithVersioningRepository;
import fi.vm.sade.eperusteet.ylops.service.util.Pair;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * @author mikkom
 */
@Repository
public interface OpetussuunnitelmaRepository extends JpaWithVersioningRepository<Opetussuunnitelma, Long> {

    @Query(value = "SELECT org from Opetussuunnitelma o join o.organisaatiot org where o.id = ?1")
    List<String> findOrganisaatiot(long id);

    @Query(value = "SELECT o from Opetussuunnitelma o where o.cachedPeruste.perusteId = ?1")
    Set<Opetussuunnitelma> findByPerusteIdAndOrganisaatio(long id);

    @Query(value = "SELECT NEW fi.vm.sade.eperusteet.ylops.service.util.Pair(o.tyyppi, o.tila) from Opetussuunnitelma o where o.id = ?1")
    Pair<Tyyppi, Tila> findTyyppiAndTila(long id);

    @Query(value = "SELECT NEW java.lang.Boolean(o.esikatseltavissa) from Opetussuunnitelma o where o.id = ?1")
    Boolean isEsikatseltavissa(long id);

    Set<Opetussuunnitelma> findOneByTyyppiAndTila(Tyyppi tyyppi, Tila tila);

    Set<Opetussuunnitelma> findOneByTyyppiAndTilaAndKoulutustyyppi(Tyyppi tyyppi, Tila tila, KoulutusTyyppi kt);

    Opetussuunnitelma findFirst1ByTyyppi(Tyyppi tyyppi);

    List<Opetussuunnitelma> findAllByTyyppi(Tyyppi tyyppi);

    List<Opetussuunnitelma> findAllByTyyppiAndTilaAndKoulutustyyppi(Tyyppi tyyppi, Tila tila, KoulutusTyyppi kt);

    @Query(value = "SELECT o FROM Opetussuunnitelma o WHERE (o.tila = 'JULKAISTU') AND o.tyyppi = :tyyppi")
    List<Opetussuunnitelma> findAllByTyyppiAndTilaIsJulkaistu(@Param("tyyppi") Tyyppi tyyppi);

    List<Opetussuunnitelma> findAllByTyyppiAndTila(Tyyppi tyyppi, Tila tila);

    @Query(value = "SELECT o FROM Opetussuunnitelma o WHERE o.pohja.id = ?1")
    Set<Opetussuunnitelma> findAllByPohjaId(long id);

    @Query(value = "SELECT DISTINCT o FROM Opetussuunnitelma o JOIN o.organisaatiot org " +
            "WHERE org IN (:organisaatiot) AND o.tyyppi = :tyyppi")
    List<Opetussuunnitelma> findAllByTyyppi(@Param("tyyppi") Tyyppi tyyppi,
                                            @Param("organisaatiot") Collection<String> organisaatiot);

    @Query(value = "SELECT COUNT(DISTINCT o) FROM Opetussuunnitelma o JOIN o.organisaatiot org " +
            "WHERE org IN (:organisaatiot) AND o.tyyppi = :tyyppi AND o.tila IN (:tilat)")
    Long countByTyyppi(@Param("tyyppi") Tyyppi tyyppi,
                       @Param("tilat") Collection<Tila> tilat,
                       @Param("organisaatiot") Collection<String> organisaatiot);

    @Query(value = "SELECT COUNT(DISTINCT o) FROM Opetussuunnitelma o WHERE o.tyyppi = :tyyppi AND o.tila IN (:tilat)")
    Long countByTyyppi(@Param("tyyppi") Tyyppi tyyppi,
                       @Param("tilat") Collection<Tila> tilat);

    @Query(value = "SELECT COUNT(DISTINCT o) " +
            "FROM Opetussuunnitelma o " +
            "JOIN o.organisaatiot org " +
            "LEFT JOIN o.julkaisut j " +
            "WHERE org IN (:organisaatiot) " +
            "AND o.tyyppi = :tyyppi " +
            "AND (o.tila = fi.vm.sade.eperusteet.ylops.domain.Tila.JULKAISTU OR j.id IS NOT NULL)")
    Long countByTyyppiAndJulkaistut(@Param("tyyppi") Tyyppi tyyppi,
                                    @Param("organisaatiot") Collection<String> organisaatiot);

    @Query(value = "SELECT COUNT(DISTINCT o) " +
            "FROM Opetussuunnitelma o " +
            "LEFT JOIN o.julkaisut j " +
            "WHERE o.tyyppi = :tyyppi " +
            "AND (o.tila = fi.vm.sade.eperusteet.ylops.domain.Tila.JULKAISTU OR j.id IS NOT NULL)")
    Long countByTyyppiAndJulkaistut(@Param("tyyppi") Tyyppi tyyppi);

    @Query(value = "SELECT DISTINCT o FROM Opetussuunnitelma o JOIN o.organisaatiot org " +
            "WHERE o.tyyppi = fi.vm.sade.eperusteet.ylops.domain.Tyyppi.POHJA AND (o.tila = fi.vm.sade.eperusteet.ylops.domain.Tila.VALMIS OR org IN (:organisaatiot))")
    List<Opetussuunnitelma> findPohja(@Param("organisaatiot") Collection<String> organisaatiot);
    
    @Query(value = "SELECT DISTINCT o FROM Opetussuunnitelma o JOIN o.organisaatiot org " +
            "WHERE o.tyyppi = fi.vm.sade.eperusteet.ylops.domain.Tyyppi.OPS AND org IN (:organisaatiot)")
    Set<Opetussuunnitelma> findOpsPohja(@Param("organisaatiot") Collection<String> organisaatiot);

    @Query(value = "SELECT o FROM Opetussuunnitelma o WHERE o.tekstit.id in ?1")
    Set<Opetussuunnitelma> findByTekstiRoot(Iterable<Long> ids);

    @Query(value = "SELECT o FROM Opetussuunnitelma o JOIN o.oppiaineet oa JOIN oa.oppiaine a WHERE a.id = ?1")
    Set<Opetussuunnitelma> findByOppiaineId(long id);

    @Query(value = "SELECT o FROM Opetussuunnitelma o JOIN o.vuosiluokkakokonaisuudet ov JOIN ov.vuosiluokkakokonaisuus v WHERE v.id = ?1")
    Set<Opetussuunnitelma> findByVuosiluokkakokonaisuusId(long id);

    Opetussuunnitelma findByLops2019OpintojaksotIdIn(List<Long> ids);
}
