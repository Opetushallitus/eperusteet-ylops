package fi.vm.sade.eperusteet.ylops.repository.lops2019;

import fi.vm.sade.eperusteet.ylops.domain.lops2019.Lops2019Poistettu;
import fi.vm.sade.eperusteet.ylops.domain.ops.Opetussuunnitelma;
import fi.vm.sade.eperusteet.ylops.repository.version.JpaWithVersioningRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface Lops2019OpintojaksoPoistettuRepository extends JpaWithVersioningRepository<Lops2019Poistettu, Long> {
    List<Lops2019Poistettu> findAllByOpetussuunnitelma(Opetussuunnitelma ops);
}