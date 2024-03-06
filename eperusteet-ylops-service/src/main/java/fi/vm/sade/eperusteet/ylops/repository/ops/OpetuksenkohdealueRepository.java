package fi.vm.sade.eperusteet.ylops.repository.ops;

import fi.vm.sade.eperusteet.ylops.domain.oppiaine.Opetuksenkohdealue;
import fi.vm.sade.eperusteet.ylops.repository.version.JpaWithVersioningRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OpetuksenkohdealueRepository extends JpaWithVersioningRepository<Opetuksenkohdealue, Long> {
}
