package fi.vm.sade.eperusteet.ylops.repository.tpo;

import fi.vm.sade.eperusteet.ylops.domain.tpo.TaiteenalanVaihe;
import fi.vm.sade.eperusteet.ylops.repository.version.JpaWithVersioningRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TaiteenalanVaiheRepository extends JpaWithVersioningRepository<TaiteenalanVaihe, Long> {

    @Query("SELECT v FROM TaiteenalanVaihe v WHERE v.id = :id AND v.taiteenala.sisalto.opetussuunnitelma.id = :opsId")
    TaiteenalanVaihe findByIdAndOpetussuunnitelmaId(@Param("id") Long id, @Param("opsId") Long opsId);
}
