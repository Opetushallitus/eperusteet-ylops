package fi.vm.sade.eperusteet.ylops.repository.teksti;

import fi.vm.sade.eperusteet.ylops.domain.teksti.TekstiKappale;
import fi.vm.sade.eperusteet.ylops.domain.teksti.TekstiKappaleViite;
import fi.vm.sade.eperusteet.ylops.repository.version.JpaWithVersioningRepository;

import java.util.List;

import org.springframework.stereotype.Repository;

@Repository
public interface TekstikappaleviiteRepository extends JpaWithVersioningRepository<TekstiKappaleViite, Long>, TekstikappaleviiteRepositoryCustom {
    List<TekstiKappaleViite> findAllByTekstiKappale(TekstiKappale tekstiKappale);

    List<TekstiKappaleViite> findAllByOriginalId(Long originalId);
}
