package fi.vm.sade.eperusteet.ylops.repository.ops;

import fi.vm.sade.eperusteet.ylops.domain.lukio.OpsOppiaineId;
import fi.vm.sade.eperusteet.ylops.domain.lukio.OpsOppiaineParentView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OpsOppiaineParentViewRepository extends JpaRepository<OpsOppiaineParentView, OpsOppiaineId> {
    @Query(value = "select v from OpsOppiaineParentView v where v.opsOppiaine.opetussuunnitelmaId = ?1")
    List<OpsOppiaineParentView> findByOpetusuunnitelmaId(long opsId);

    @Query(value = "select v from OpsOppiaineParentView v where v.opsOppiaine.opetussuunnitelmaId = ?1 and v.tunniste = ?2")
    List<OpsOppiaineParentView> findByTunnisteAndOpetusuunnitelmaId(long opsId, UUID tunniste);
}
