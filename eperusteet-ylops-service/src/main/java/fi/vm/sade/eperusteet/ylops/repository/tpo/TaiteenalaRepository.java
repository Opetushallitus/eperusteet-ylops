package fi.vm.sade.eperusteet.ylops.repository.tpo;

import fi.vm.sade.eperusteet.ylops.domain.tpo.Taiteenala;
import fi.vm.sade.eperusteet.ylops.repository.version.JpaWithVersioningRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TaiteenalaRepository extends JpaWithVersioningRepository<Taiteenala, Long> {

    @Query("SELECT t FROM Taiteenala t WHERE t.id = :id AND t.sisalto.opetussuunnitelma.id = :opsId")
    Taiteenala findByIdAndOpetussuunnitelmaId(@Param("id") Long id, @Param("opsId") Long opsId);
}
