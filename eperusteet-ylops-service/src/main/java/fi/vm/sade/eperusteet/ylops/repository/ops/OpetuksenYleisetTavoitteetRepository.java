package fi.vm.sade.eperusteet.ylops.repository.ops;

import fi.vm.sade.eperusteet.ylops.domain.lukio.OpetuksenYleisetTavoitteet;
import fi.vm.sade.eperusteet.ylops.repository.version.JpaWithVersioningRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OpetuksenYleisetTavoitteetRepository extends JpaWithVersioningRepository<OpetuksenYleisetTavoitteet, Long> {
}
