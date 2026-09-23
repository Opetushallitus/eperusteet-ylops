package fi.vm.sade.eperusteet.ylops.repository.tpo;

import fi.vm.sade.eperusteet.ylops.domain.tpo.TpoSisalto;
import fi.vm.sade.eperusteet.ylops.repository.version.JpaWithVersioningRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TpoSisaltoRepository extends JpaWithVersioningRepository<TpoSisalto, Long> {
}
