package fi.vm.sade.eperusteet.ylops.repository.teksti;

import fi.vm.sade.eperusteet.ylops.domain.teksti.PoistettuTekstiKappale;
import fi.vm.sade.eperusteet.ylops.repository.version.JpaWithVersioningRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PoistettuTekstiKappaleRepository extends JpaWithVersioningRepository<PoistettuTekstiKappale, Long> {
    @Query("SELECT ptk FROM PoistettuTekstiKappale ptk WHERE ptk.opetussuunnitelma.id = ?1 AND (ptk.palautettu IS FALSE OR ptk.palautettu is NULL)")
    List<PoistettuTekstiKappale> findPoistetutByOpsId(Long opsId);

    @Query("SELECT ptk FROM PoistettuTekstiKappale ptk, TekstiKappale tk WHERE ptk.tekstiKappale = tk.id AND tk.tunniste = :tunniste AND ptk.opetussuunnitelma.id = :opsId AND (ptk.palautettu IS FALSE OR ptk.palautettu is NULL)")
    List<PoistettuTekstiKappale> findPoistetutByOpsIdAndTunniste(Long opsId, UUID tunniste);
}
