package fi.vm.sade.eperusteet.ylops.repository.teksti;

import fi.vm.sade.eperusteet.ylops.domain.teksti.TekstiKappaleViite;

public interface TekstikappaleviiteRepositoryCustom {
    TekstiKappaleViite findInOps(Long opsId, Long viiteId);
}
