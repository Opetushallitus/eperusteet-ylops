package fi.vm.sade.eperusteet.ylops.repository.ops;

import fi.vm.sade.eperusteet.ylops.domain.oppiaine.Oppiaine;
import fi.vm.sade.eperusteet.ylops.domain.oppiaine.OppiaineTyyppi;
import fi.vm.sade.eperusteet.ylops.domain.ops.Opetussuunnitelma;
import fi.vm.sade.eperusteet.ylops.repository.version.JpaWithVersioningRepository;

import java.util.Set;
import java.util.UUID;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface OppiaineRepository extends JpaWithVersioningRepository<Oppiaine, Long> {
    @Query(value = "SELECT a FROM Opetussuunnitelma o JOIN o.oppiaineet oa JOIN oa.oppiaine a WHERE o.id = ?1")
    Set<Oppiaine> findByOpsId(long opsId);

    @Query(value = "SELECT a FROM Opetussuunnitelma o JOIN o.oppiaineet oa JOIN oa.oppiaine a WHERE o.id = ?1 AND a.tyyppi = 'YHTEINEN'")
    Set<Oppiaine> findYhteisetByOpsId(long opsId);

    @Query(value = "SELECT a FROM Opetussuunnitelma o JOIN o.oppiaineet oa JOIN oa.oppiaine a WHERE o.id = ?1 AND a.tyyppi <> 'YHTEINEN'")
    Set<Oppiaine> findValinnaisetByOpsId(long opsId);

    @Query(value = "SELECT a FROM Opetussuunnitelma o JOIN o.oppiaineet oa JOIN oa.oppiaine a WHERE o.id = ?1 AND a.tyyppi = ?2")
    Set<Oppiaine> findByOpsIdAndTyyppi(long opsId, OppiaineTyyppi tyyppi);

    @Query(value = "SELECT a FROM Opetussuunnitelma o JOIN o.oppiaineet oa JOIN oa.oppiaine a WHERE o.id = ?1 AND a.tunniste = ?2")
    Oppiaine findOneByOpsIdAndTunniste(long opsId, UUID tunniste);

    @Query(value = "SELECT oa.oma FROM Opetussuunnitelma o JOIN o.oppiaineet oa JOIN oa.oppiaine a LEFT JOIN a.oppimaarat m WHERE o.id = ?1 AND (a.id = ?2 OR m.id = ?2)")
    Boolean isOma(long opsId, long oppiaineId);

    @Query(value = "select ops from Opetussuunnitelma ops inner join ops.oppiaineet oo inner join oo.oppiaine o on o.id = ?1 where not(ops.id = ?2)")
    Set<Opetussuunnitelma> findOtherOpetussuunnitelmasContainingOpsOppiaine(long oppiaineId, long exceptOpsId);
}
