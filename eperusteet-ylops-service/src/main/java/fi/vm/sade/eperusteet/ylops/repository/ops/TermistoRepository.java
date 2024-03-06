package fi.vm.sade.eperusteet.ylops.repository.ops;

import java.util.List;

import fi.vm.sade.eperusteet.ylops.domain.Termi;
import fi.vm.sade.eperusteet.ylops.domain.ops.Opetussuunnitelma;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TermistoRepository extends JpaRepository<Termi, Long> {
    List<Termi> findByOpsId(Long opsId);

    Termi findOneByOpsAndAvain(Opetussuunnitelma ops, String avain);
}
