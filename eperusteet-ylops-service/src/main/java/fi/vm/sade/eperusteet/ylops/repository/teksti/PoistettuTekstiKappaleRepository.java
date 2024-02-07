package fi.vm.sade.eperusteet.ylops.repository.teksti;

import fi.vm.sade.eperusteet.ylops.domain.teksti.PoistettuTekstiKappale;
import fi.vm.sade.eperusteet.ylops.repository.version.JpaWithVersioningRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PoistettuTekstiKappaleRepository extends JpaWithVersioningRepository<PoistettuTekstiKappale, Long> {
    @Query("SELECT ptk FROM PoistettuTekstiKappale ptk WHERE ptk.opetussuunnitelma.id = ?1 AND (ptk.palautettu IS FALSE OR ptk.palautettu is NULL)")
    List<PoistettuTekstiKappale> findPoistetutByOpsId(Long opsId);
}
