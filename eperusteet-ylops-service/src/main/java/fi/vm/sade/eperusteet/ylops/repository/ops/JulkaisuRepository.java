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

import java.util.List;
import java.util.Set;

@Repository
public interface JulkaisuRepository extends JpaRepository<OpetussuunnitelmanJulkaisu, Long> {
    List<OpetussuunnitelmanJulkaisu> findAllByOpetussuunnitelma(Opetussuunnitelma ops);

    long countByOpetussuunnitelmaId(Long id);

    OpetussuunnitelmanJulkaisu findFirstByOpetussuunnitelmaOrderByRevisionDesc(Opetussuunnitelma opetussuunnitelma);

    @Query("SELECT julkaisu FROM OpetussuunnitelmanJulkaisu julkaisu WHERE julkaisu.opetussuunnitelma = :ops")
    List<OpetussuunnitelmaJulkaisuKevyt> findKevytdataByOpetussuunnitelma(@Param("ops") Opetussuunnitelma ops);

    OpetussuunnitelmanJulkaisu findByOpetussuunnitelmaAndRevision(Opetussuunnitelma opetussuunnitelma, int revision);

    String julkaisutQuery = "FROM ( " +
            "   SELECT * " +
            "   FROM julkaistu_opetussuunnitelma_data_view data" +
            "   WHERE 1 = 1 " +
            "   AND (:nimi LIKE '' OR LOWER(nimi->>:kieli) LIKE LOWER(CONCAT('%',:nimi,'%'))) " +
            "   AND (:perusteenDiaarinumero = '' OR peruste->>'diaarinumero' = :perusteenDiaarinumero) " +
            "   AND (COALESCE(:koulutustyypit, NULL) = '' OR koulutustyyppi IN (:koulutustyypit)) " +
            "   order by nimi->>:kieli asc " +
            ") t";

    @Query(nativeQuery = true,
            value = "SELECT CAST(row_to_json(t) as text) " + julkaisutQuery,
            countQuery = "SELECT count(*) " + julkaisutQuery
    )
    Page<String> findAllJulkisetJulkaisut(
            @Param("nimi") String nimi,
            @Param("kieli") String kieli,
            @Param("perusteenDiaarinumero") String perusteenDiaarinumero,
            @Param("koulutustyypit") List<String> koulutustyypit,
            Pageable pageable);

    @Query(nativeQuery = true, value =
            "SELECT CAST(row_to_json(t) as text) FROM ( " +
                    "   SELECT * " +
                    "   FROM julkaistu_opetussuunnitelma_Data_view data) t")
    List<String> findAllJulkaistutOpetussuunnitelmat();

    OpetussuunnitelmanJulkaisu findOneByDokumentitIn(Set<Long> dokumentit);

    @Query(nativeQuery = true,
            value = "SELECT CAST(jsonb_path_query(julkaisu_data.opsdata, CAST(:query AS jsonpath)) AS text) " +
                    "FROM opetussuunnitelman_julkaisu julkaisu " +
                    "INNER JOIN opetussuunnitelman_julkaisu_data julkaisu_data ON julkaisu.data_id = julkaisu_data.id " +
                    "WHERE julkaisu.ops_id = :opetussuunnitelmaId " +
                    "AND luotu = (SELECT MAX(luotu) FROM opetussuunnitelman_julkaisu WHERE ops_id = julkaisu.ops_id)")
    String findJulkaisutByJsonPath(@Param("opetussuunnitelmaId") Long opetussuunnitelmaId, @Param("query") String query);
}
