package fi.vm.sade.eperusteet.ylops.repository.lops2019;

import fi.vm.sade.eperusteet.ylops.domain.lops2019.Lops2019Sisalto;
import fi.vm.sade.eperusteet.ylops.repository.version.JpaWithVersioningRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Lops2019SisaltoRepository extends JpaWithVersioningRepository<Lops2019Sisalto, Long> {
}
