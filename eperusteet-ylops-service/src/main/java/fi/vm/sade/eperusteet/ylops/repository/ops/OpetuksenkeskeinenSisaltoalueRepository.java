package fi.vm.sade.eperusteet.ylops.repository.ops;

import fi.vm.sade.eperusteet.ylops.domain.oppiaine.OpetuksenKeskeinensisaltoalue;
import fi.vm.sade.eperusteet.ylops.repository.version.JpaWithVersioningRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OpetuksenkeskeinenSisaltoalueRepository extends JpaWithVersioningRepository<OpetuksenKeskeinensisaltoalue, Long> {
}
