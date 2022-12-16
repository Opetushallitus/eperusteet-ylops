package fi.vm.sade.eperusteet.ylops.repository.ops;

import fi.vm.sade.eperusteet.ylops.domain.ops.Kommentti2019;
import fi.vm.sade.eperusteet.ylops.repository.version.JpaWithVersioningRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface Kommentti2019Repository extends JpaWithVersioningRepository<Kommentti2019, Long> {
    Kommentti2019 getOneByTunniste(UUID tunniste);
    List<Kommentti2019> findAllByThread(UUID thread);
    Kommentti2019 findFirstByThreadOrderByLuotuAsc(UUID thread);
    long countByThread(UUID thread);
}
