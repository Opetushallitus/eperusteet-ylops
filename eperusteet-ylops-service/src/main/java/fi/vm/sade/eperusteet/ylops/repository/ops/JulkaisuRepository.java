package fi.vm.sade.eperusteet.ylops.repository.ops;

import fi.vm.sade.eperusteet.ylops.domain.ops.Opetussuunnitelma;
import fi.vm.sade.eperusteet.ylops.domain.ops.OpetussuunnitelmanJulkaisu;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpetussuunnitelmaJulkaisuKevyt;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Repository
public interface JulkaisuRepository extends JpaRepository<OpetussuunnitelmanJulkaisu, Long> {
    List<OpetussuunnitelmanJulkaisu> findAllByOpetussuunnitelma(Opetussuunnitelma ops);

    long countByOpetussuunnitelmaId(Long id);

    OpetussuunnitelmanJulkaisu findFirstByOpetussuunnitelmaOrderByRevisionDesc(Opetussuunnitelma opetussuunnitelma);

    OpetussuunnitelmanJulkaisu findByOpetussuunnitelmaAndRevision(Opetussuunnitelma opetussuunnitelma, Integer revision);

    @Query("SELECT julkaisu FROM OpetussuunnitelmanJulkaisu julkaisu WHERE julkaisu.opetussuunnitelma = :ops")
    List<OpetussuunnitelmaJulkaisuKevyt> findKevytdataByOpetussuunnitelma(@Param("ops") Opetussuunnitelma ops);

    OpetussuunnitelmanJulkaisu findByOpetussuunnitelmaAndRevision(Opetussuunnitelma opetussuunnitelma, int revision);

    String julkaisutQuery = """
        FROM (
               SELECT *
               FROM julkaistu_opetussuunnitelma_data_view data
               WHERE 1 = 1
               AND (data.peruste->'id')::BIGINT NOT IN (SELECT peruste_id FROM poistetut_perusteet)
               AND CAST(julkaisukielet as text) LIKE LOWER(CONCAT('%',:kieli,'%'))
               AND (:nimi LIKE '' OR LOWER(nimi->>:kieli) LIKE LOWER(CONCAT('%',:nimi,'%')) OR EXISTS (SELECT 1 FROM jsonb_array_elements(organisaatiot) elem WHERE LOWER(elem->'nimi'->>:kieli) LIKE LOWER(CONCAT('%',:nimi,'%'))))
               AND (:organisaatio LIKE '' OR EXISTS (SELECT 1 FROM jsonb_array_elements(organisaatiot) elem WHERE LOWER(elem->>'oid') LIKE LOWER(CONCAT('%',:organisaatio,'%'))))
               AND (:perusteenDiaarinumero = '' OR peruste->>'diaarinumero' = :perusteenDiaarinumero)
               AND (COALESCE(:koulutustyypit, NULL) IS NULL OR koulutustyyppi IN (:koulutustyypit))
               AND (COALESCE(:julkaistuJalkeen, NULL) IS NULL OR julkaisuaika >= :julkaistuJalkeen)
               AND (COALESCE(:julkaistuEnnen, NULL) IS NULL OR julkaisuaika < :julkaistuEnnen)
               order by nimi->>:kieli asc
            ) t
        """;

    @Query(nativeQuery = true,
            value = "SELECT CAST(row_to_json(t) as text) " + julkaisutQuery,
            countQuery = "SELECT count(*) " + julkaisutQuery
    )
    Page<String> findAllJulkisetJulkaisut(
            @Param("nimi") String nimi,
            @Param("kieli") String kieli,
            @Param("perusteenDiaarinumero") String perusteenDiaarinumero,
            @Param("koulutustyypit") List<String> koulutustyypit,
            @Param("organisaatio") String organisaatio,
            @Param("julkaistuJalkeen") LocalDateTime julkaistuJalkeen,
            @Param("julkaistuEnnen") LocalDateTime julkaistuEnnen,
            Pageable pageable);

    @Query(nativeQuery = true, value =
        """
           SELECT CAST(row_to_json(t) as text) FROM (
           SELECT *
           FROM julkaistu_opetussuunnitelma_Data_view data
           WHERE (data.peruste->'id')::BIGINT NOT IN (SELECT peruste_id FROM poistetut_perusteet)
           ) t
        """)
    List<String> findAllJulkaistutOpetussuunnitelmat();

    @Query(nativeQuery = true, value =
        """
            SELECT CAST(row_to_json(t) as text) FROM (
                SELECT *
                FROM julkaistu_opetussuunnitelma_Data_view data
                WHERE koulutustyyppi = :koulutustyyppi
                AND (data.peruste->'id')::BIGINT NOT IN (SELECT peruste_id FROM poistetut_perusteet)
            ) t
        """)
    List<String> findAllJulkaistutOpetussuunnitelmat(@Param("koulutustyyppi") String koulutustyyppi);

    OpetussuunnitelmanJulkaisu findOneByDokumentitIn(Set<Long> dokumentit);

    @Query(nativeQuery = true,
        value = """
                    SELECT CAST(jsonb_path_query(julkaisu_data.opsdata, CAST(:query AS jsonpath)) AS text)
                    FROM opetussuunnitelman_julkaisu julkaisu
                    INNER JOIN opetussuunnitelman_julkaisu_data julkaisu_data ON julkaisu.data_id = julkaisu_data.id
                    WHERE julkaisu.ops_id = :opetussuunnitelmaId
                    AND luotu = (SELECT MAX(luotu) FROM opetussuunnitelman_julkaisu WHERE ops_id = julkaisu.ops_id)
                    AND (julkaisu_data->'peruste'->'id')::BIGINT NOT IN (SELECT peruste_id FROM poistetut_perusteet)
                """)
    String findJulkaisutByJsonPath(@Param("opetussuunnitelmaId") Long opetussuunnitelmaId, @Param("query") String query);
}
