package fi.vm.sade.eperusteet.ylops.repository.ukk;

import fi.vm.sade.eperusteet.ylops.domain.ukk.Kysymys;
import fi.vm.sade.eperusteet.ylops.repository.version.JpaWithVersioningRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KysymysRepository extends JpaWithVersioningRepository<Kysymys, Long> {
    
}
