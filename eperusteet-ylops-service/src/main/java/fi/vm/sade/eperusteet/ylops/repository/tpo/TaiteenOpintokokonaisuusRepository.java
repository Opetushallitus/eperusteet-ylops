package fi.vm.sade.eperusteet.ylops.repository.tpo;

import fi.vm.sade.eperusteet.ylops.domain.tpo.TaiteenOpintokokonaisuus;
import fi.vm.sade.eperusteet.ylops.repository.version.JpaWithVersioningRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TaiteenOpintokokonaisuusRepository extends JpaWithVersioningRepository<TaiteenOpintokokonaisuus, Long> {

    @Query("SELECT o FROM TaiteenOpintokokonaisuus o WHERE o.id = :id AND o.vaihe.taiteenala.sisalto.opetussuunnitelma.id = :opsId")
    TaiteenOpintokokonaisuus findByIdAndOpetussuunnitelmaId(@Param("id") Long id, @Param("opsId") Long opsId);
}
