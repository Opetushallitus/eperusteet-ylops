package fi.vm.sade.eperusteet.ylops.repository.ops;

import fi.vm.sade.eperusteet.ylops.domain.oppiaine.PoistettuOppiaine;
import fi.vm.sade.eperusteet.ylops.repository.version.JpaWithVersioningRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by autio on 24.2.2016.
 */
@Repository
@Deprecated
public interface PoistettuOppiaineRepository extends JpaWithVersioningRepository<PoistettuOppiaine, Long> {
    @Query("SELECT po FROM PoistettuOppiaine po WHERE po.opetussuunnitelma.id = ?1 AND (po.palautettu IS FALSE OR po.palautettu is NULL)")
    List<PoistettuOppiaine> findPoistetutByOpsId(Long opsId);
}
