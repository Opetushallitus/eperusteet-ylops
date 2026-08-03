package fi.vm.sade.eperusteet.ylops.repository.tpo;

import fi.vm.sade.eperusteet.ylops.domain.tpo.Taiteenosa;
import fi.vm.sade.eperusteet.ylops.repository.version.JpaWithVersioningRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TaiteenosaRepository extends JpaWithVersioningRepository<Taiteenosa, Long> {

    @Query(value = "SELECT tos FROM TpoSisalto s JOIN s.taiteenalat ta JOIN ta.taiteenosat tos " +
            "WHERE tos.id = ?1 AND s.opetussuunnitelma.id = ?2")
    Optional<Taiteenosa> findByIdAndOpetussuunnitelmaId(Long taiteenosaId, Long opsId);
}
