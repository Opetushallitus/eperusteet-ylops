package fi.vm.sade.eperusteet.ylops.repository.aipe;

import fi.vm.sade.eperusteet.ylops.domain.aipe.AIPEOppiaine;
import fi.vm.sade.eperusteet.ylops.repository.version.JpaWithVersioningRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AIPEOppiaineRepository extends JpaWithVersioningRepository<AIPEOppiaine, Long> {
}
