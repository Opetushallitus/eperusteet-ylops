package fi.vm.sade.eperusteet.ylops.repository.teksti;

import fi.vm.sade.eperusteet.ylops.domain.teksti.TekstiKappale;
import fi.vm.sade.eperusteet.ylops.repository.version.JpaWithVersioningRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TekstiKappaleRepository extends JpaWithVersioningRepository<TekstiKappale, Long> {
}
