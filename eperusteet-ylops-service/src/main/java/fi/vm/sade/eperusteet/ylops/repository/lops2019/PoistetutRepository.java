package fi.vm.sade.eperusteet.ylops.repository.lops2019;

import fi.vm.sade.eperusteet.ylops.domain.lops2019.Poistettu;
import fi.vm.sade.eperusteet.ylops.domain.lops2019.PoistetunTyyppi;
import fi.vm.sade.eperusteet.ylops.domain.ops.Opetussuunnitelma;
import fi.vm.sade.eperusteet.ylops.repository.version.JpaWithVersioningRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PoistetutRepository extends JpaWithVersioningRepository<Poistettu, Long> {
    List<Poistettu> findAllByOpetussuunnitelma(Opetussuunnitelma ops);

    List<Poistettu> findAllByOpetussuunnitelmaAndTyyppi(Opetussuunnitelma ops, PoistetunTyyppi tyyppi);

    Poistettu findByOpetussuunnitelmaIdAndPoistettuIdAndTyyppi(Long opsId, Long poistettuId, PoistetunTyyppi tyyppi);

}
