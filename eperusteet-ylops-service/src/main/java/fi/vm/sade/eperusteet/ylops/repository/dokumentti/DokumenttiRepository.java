package fi.vm.sade.eperusteet.ylops.repository.dokumentti;

import fi.vm.sade.eperusteet.ylops.domain.dokumentti.Dokumentti;
import fi.vm.sade.eperusteet.ylops.domain.dokumentti.DokumenttiTila;
import fi.vm.sade.eperusteet.ylops.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.ylops.repository.CustomJpaRepository;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Repository
public interface DokumenttiRepository extends CustomJpaRepository<Dokumentti, Long> {
    List<Dokumentti> findByOpsIdAndKieliAndTila(Long opsId, Kieli kieli, DokumenttiTila tila, Sort sort);

    Dokumentti findFirstByOpsIdAndKieliOrderByAloitusaikaDesc(Long opsId, Kieli kieli);

    Dokumentti findByIdInAndKieli(Set<Long> id, Kieli kieli);

    @Modifying
    @Query(nativeQuery = true,
        value = "UPDATE dokumentti " +
                "SET tila = 'EPAONNISTUI' " +
                "WHERE tila IN ('LUODAAN', 'JONOSSA') " +
                "AND aloitusaika < :cutoff")
    void cleanStuckPrintings(@Param("cutoff") LocalDateTime cutoff);
}
