package fi.vm.sade.eperusteet.ylops.repository.ops;

import fi.vm.sade.eperusteet.ylops.domain.Termi;
import fi.vm.sade.eperusteet.ylops.domain.ops.Opetussuunnitelma;
import fi.vm.sade.eperusteet.ylops.repository.CustomJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TermistoRepository extends CustomJpaRepository<Termi, Long> {
    List<Termi> findByOpsId(Long opsId);

    Termi findOneByOpsAndAvain(Opetussuunnitelma ops, String avain);
}
