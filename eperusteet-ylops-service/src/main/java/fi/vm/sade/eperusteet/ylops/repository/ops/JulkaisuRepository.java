package fi.vm.sade.eperusteet.ylops.repository.ops;

import fi.vm.sade.eperusteet.ylops.domain.ops.Opetussuunnitelma;
import fi.vm.sade.eperusteet.ylops.domain.ops.OpetussuunnitelmanJulkaisu;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpetussuunnitelmaJulkaisuKevyt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JulkaisuRepository extends JpaRepository<OpetussuunnitelmanJulkaisu, Long> {
    List<OpetussuunnitelmanJulkaisu> findAllByOpetussuunnitelma(Opetussuunnitelma ops);

    long countByOpetussuunnitelmaId(Long id);

    OpetussuunnitelmanJulkaisu findFirstByOpetussuunnitelmaOrderByRevisionDesc(Opetussuunnitelma opetussuunnitelma);

    @Query("SELECT julkaisu FROM OpetussuunnitelmanJulkaisu julkaisu WHERE julkaisu.opetussuunnitelma = :ops")
    List<OpetussuunnitelmaJulkaisuKevyt> findKevytdataByOpetussuunnitelma(@Param("ops") Opetussuunnitelma ops);

    OpetussuunnitelmanJulkaisu findByOpetussuunnitelmaAndRevision(Opetussuunnitelma opetussuunnitelma, int revision);
}
