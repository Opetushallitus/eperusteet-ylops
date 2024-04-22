package fi.vm.sade.eperusteet.ylops.repository.liite;

import fi.vm.sade.eperusteet.ylops.domain.liite.Liite;
import fi.vm.sade.eperusteet.ylops.repository.CustomJpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LiiteRepository extends CustomJpaRepository<Liite, UUID>, LiiteRepositoryCustom {

    @Query("SELECT l FROM Opetussuunnitelma o JOIN o.liitteet l WHERE o.id = ?1")
    List<Liite> findByOpsId(Long opsId);

    @Query("SELECT l FROM Opetussuunnitelma o JOIN o.liitteet l WHERE o.id = ?1 AND l.id = ?2")
    Liite findOne(Long opsId, UUID id);
}
