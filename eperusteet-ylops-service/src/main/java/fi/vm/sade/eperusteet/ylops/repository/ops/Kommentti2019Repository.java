package fi.vm.sade.eperusteet.ylops.repository.ops;

import fi.vm.sade.eperusteet.ylops.domain.ops.Kommentti2019;
import fi.vm.sade.eperusteet.ylops.repository.version.JpaWithVersioningRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface Kommentti2019Repository extends JpaWithVersioningRepository<Kommentti2019, Long> {
    Kommentti2019 findOneByUuid(UUID uuid);
}
