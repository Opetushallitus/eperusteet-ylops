package fi.vm.sade.eperusteet.ylops.repository.ops;

import fi.vm.sade.eperusteet.ylops.domain.KoulutusTyyppi;
import fi.vm.sade.eperusteet.ylops.domain.Tila;
import fi.vm.sade.eperusteet.ylops.domain.Tyyppi;
import fi.vm.sade.eperusteet.ylops.domain.ops.Opetussuunnitelma;
import fi.vm.sade.eperusteet.ylops.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpetussuunnitelmaWithLatestTilaUpdateTime;
import fi.vm.sade.eperusteet.ylops.repository.version.JpaWithVersioningRepository;
import fi.vm.sade.eperusteet.ylops.service.util.Pair;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@Repository
public interface OpetussuunnitelmaRepository extends JpaWithVersioningRepository<Opetussuunnitelma, Long> {

    @Query(value = "SELECT org from Opetussuunnitelma o join o.organisaatiot org where o.id = ?1")
    List<String> findOrganisaatiot(long id);

    @Query(value = "SELECT o from Opetussuunnitelma o where o.cachedPeruste.perusteId = ?1")
    Set<Opetussuunnitelma> findByPerusteId(long perusteId);

    @Query(value = "SELECT NEW fi.vm.sade.eperusteet.ylops.service.util.Pair(o.tyyppi, o.tila) from Opetussuunnitelma o where o.id = ?1")
    Pair<Tyyppi, Tila> findTyyppiAndTila(long id);

    @Query(value = "SELECT NEW java.lang.Boolean(o.esikatseltavissa) from Opetussuunnitelma o where o.id = ?1")
    Boolean isEsikatseltavissa(long id);

    Set<Opetussuunnitelma> findOneByTyyppiAndTila(Tyyppi tyyppi, Tila tila);

    Set<Opetussuunnitelma> findOneByTyyppiAndTilaAndKoulutustyyppi(Tyyppi tyyppi, Tila tila, KoulutusTyyppi kt);

    Opetussuunnitelma findFirst1ByTyyppi(Tyyppi tyyppi);

    List<Opetussuunnitelma> findAllByTyyppi(Tyyppi tyyppi);

    @Query(nativeQuery = true,
            value = "SELECT ops.id, aud.muokattu AS viimeisinTilaMuutosAika " +
                    "FROM opetussuunnitelma ops " +
                    "INNER JOIN (SELECT MIN(muokattu) muokattu, tila, id FROM opetussuunnitelma_aud GROUP BY tila, id) aud ON aud.id = ops.id AND aud.tila = ops.tila " +
                    "WHERE ops.tyyppi = :tyyppi")
    List<OpetussuunnitelmaWithLatestTilaUpdateTime> findAllWithLatestTilaUpdateDate(String tyyppi);

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

    String limitedPagedOpetussuunnitelmat =
            "FROM Opetussuunnitelma ops " +
                    "JOIN ops.organisaatiot org " +
                    "LEFT JOIN ops.nimi nimi " +
                    "LEFT JOIN nimi.teksti teksti " +
                    "WHERE ops.tyyppi = :tyyppi " +
                    "AND ( " +
                    "   (:tila = fi.vm.sade.eperusteet.ylops.domain.Tila.JULKAISTU AND ops.tila != fi.vm.sade.eperusteet.ylops.domain.Tila.POISTETTU AND (ops.julkaisut IS NOT EMPTY OR ops.tila = fi.vm.sade.eperusteet.ylops.domain.Tila.JULKAISTU)) " +
                    "   OR (:tila = fi.vm.sade.eperusteet.ylops.domain.Tila.POISTETTU AND ops.tila = :tila)" +
                    "   OR (ops.tila = :tila AND ops.julkaisut IS EMPTY)" +
                    " ) " +
                    "AND (:nimi IS NULL or LOWER(teksti.teksti) LIKE LOWER(CONCAT('%',:nimi,'%'))) " +
                    "AND teksti.kieli = :kieli " +
                    "AND (:koulutustyyppi IS NULL or ops.koulutustyyppi = :koulutustyyppi) ";
    String limitedPagedOpetussuunnitelmatOrganisaatiot = "AND org IS NOT NULL AND org IN (:organisaatiot)";

    @Query(
            value = "SELECT DISTINCT ops, teksti.teksti " + limitedPagedOpetussuunnitelmat + limitedPagedOpetussuunnitelmatOrganisaatiot,
            countQuery = "SELECT COUNT(distinct ops) " + limitedPagedOpetussuunnitelmat + limitedPagedOpetussuunnitelmatOrganisaatiot)
    Page<Object[]> findSivutettu(
            @Param("tyyppi") Tyyppi tyyppi,
            @Param("tila") Tila tila,
            @Param("nimi") String nimi,
            @Param("koulutustyyppi") KoulutusTyyppi koulutusTyyppi,
            @Param("organisaatiot") Collection<String> organisaatiot,
            @Param("kieli") Kieli kieli,
            Pageable pageable
    );

    @Query(
            value = "SELECT DISTINCT ops, teksti.teksti " + limitedPagedOpetussuunnitelmat,
            countQuery = "SELECT COUNT(distinct ops) " + limitedPagedOpetussuunnitelmat)
    Page<Object[]> findSivutettuAdmin(
            @Param("tyyppi") Tyyppi tyyppi,
            @Param("tila") Tila tila,
            @Param("nimi") String nimi,
            @Param("koulutustyyppi") KoulutusTyyppi koulutusTyyppi,
            @Param("kieli") Kieli kieli,
            Pageable pageable
    );

    @Query(value = "SELECT COUNT(DISTINCT o) FROM Opetussuunnitelma o JOIN o.organisaatiot org " +
            "WHERE org IN (:organisaatiot) AND o.tyyppi = :tyyppi AND o.tila IN (:tilat) AND o.julkaisut IS EMPTY")
    Long countByTyyppi(@Param("tyyppi") Tyyppi tyyppi,
                       @Param("tilat") Collection<Tila> tilat,
                       @Param("organisaatiot") Collection<String> organisaatiot);

    @Query(value = "SELECT COUNT(DISTINCT o) FROM Opetussuunnitelma o WHERE o.tyyppi = :tyyppi AND o.tila IN (:tilat) AND o.julkaisut IS EMPTY")
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

    @Query(value = "SELECT DISTINCT o " +
            "FROM Opetussuunnitelma o " +
            "JOIN o.organisaatiot org " +
            "WHERE org IN (:organisaatiot) " +
            "AND o.id = :id")
    Opetussuunnitelma findByOpsIdAndOrganisaatiot(@Param("id") Long id, @Param("organisaatiot") Collection<String> organisaatiot);

    @Query(value = "SELECT o FROM Opetussuunnitelma o WHERE o.tekstit.id in ?1")
    Set<Opetussuunnitelma> findByTekstiRoot(Iterable<Long> ids);

    @Query(value = "SELECT o FROM Opetussuunnitelma o JOIN o.oppiaineet oa JOIN oa.oppiaine a WHERE a.id = ?1")
    Set<Opetussuunnitelma> findByOppiaineId(long id);

    @Query(value = "SELECT o FROM Opetussuunnitelma o JOIN o.vuosiluokkakokonaisuudet ov JOIN ov.vuosiluokkakokonaisuus v WHERE v.id = ?1")
    Set<Opetussuunnitelma> findByVuosiluokkakokonaisuusId(long id);

    Opetussuunnitelma findByLops2019OpintojaksotIdIn(List<Long> ids);

    @Query(value = "SELECT o " +
            "FROM Opetussuunnitelma o " +
            "WHERE o.tyyppi = :tyyppi " +
            "AND tila != 'POISTETTU' " +
            "AND  o.koulutustyyppi IN (:koulutustyyppi) " +
            "AND (o.julkaisut IS NOT EMPTY OR o.tila = 'JULKAISTU')")
    List<Opetussuunnitelma> findJulkaistutByTyyppi(@Param("tyyppi") Tyyppi tyyppi, @Param("koulutustyyppi") Set<KoulutusTyyppi> koulutusTyyppi);

    @Query(value = "SELECT o " +
            "FROM Opetussuunnitelma o " +
            "WHERE o.tyyppi = :tyyppi " +
            "AND tila != 'POISTETTU' " +
            "AND (o.julkaisut IS NOT EMPTY OR o.tila = 'JULKAISTU')")
    List<Opetussuunnitelma> findJulkaistutByTyyppi(@Param("tyyppi") Tyyppi tyyppi);

    @Query(nativeQuery = true, value = "SELECT tekstit_id FROM opetussuunnitelma_aud aud " +
            "WHERE id = :opetussuunnitelmaId " +
            "AND tekstit_id != (SELECT tekstit_id FROM opetussuunnitelma WHERE id = aud.id) " +
            "ORDER BY muokattu DESC " +
            "LIMIT 1")
    Long findEdellinenTekstitId(@Param("opetussuunnitelmaId") Long id);
}
