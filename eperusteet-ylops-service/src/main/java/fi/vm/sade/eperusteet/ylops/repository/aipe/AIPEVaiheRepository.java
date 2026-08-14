package fi.vm.sade.eperusteet.ylops.repository.aipe;

import fi.vm.sade.eperusteet.ylops.domain.aipe.AIPEVaihe;
import fi.vm.sade.eperusteet.ylops.repository.version.JpaWithVersioningRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AIPEVaiheRepository extends JpaWithVersioningRepository<AIPEVaihe, Long> {
}
