package fi.vm.sade.eperusteet.ylops.repository.ops;

import fi.vm.sade.eperusteet.ylops.domain.ops.PoistettuPeruste;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PoistettuPerusteRepository extends JpaRepository<PoistettuPeruste, Long> {

    boolean existsByPerusteId(Long perusteId);
}
