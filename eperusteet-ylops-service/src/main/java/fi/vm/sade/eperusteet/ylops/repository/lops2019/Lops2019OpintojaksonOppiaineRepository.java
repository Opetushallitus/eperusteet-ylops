package fi.vm.sade.eperusteet.ylops.repository.lops2019;

import fi.vm.sade.eperusteet.ylops.domain.lops2019.Lops2019OpintojaksonOppiaine;
import fi.vm.sade.eperusteet.ylops.repository.version.JpaWithVersioningRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface Lops2019OpintojaksonOppiaineRepository extends JpaWithVersioningRepository<Lops2019OpintojaksonOppiaine, Long> {
    List<Lops2019OpintojaksonOppiaine> findAllByKoodi(String koodi);
}
