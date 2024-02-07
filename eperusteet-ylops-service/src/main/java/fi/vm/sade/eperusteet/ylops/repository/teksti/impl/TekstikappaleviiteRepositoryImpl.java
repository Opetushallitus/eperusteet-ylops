package fi.vm.sade.eperusteet.ylops.repository.teksti.impl;

import fi.vm.sade.eperusteet.ylops.domain.ops.Opetussuunnitelma;
import fi.vm.sade.eperusteet.ylops.domain.teksti.TekstiKappaleViite;
import fi.vm.sade.eperusteet.ylops.repository.teksti.TekstikappaleviiteRepositoryCustom;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class TekstikappaleviiteRepositoryImpl implements TekstikappaleviiteRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    @Override
    public TekstiKappaleViite findInOps(Long opsId, Long viiteId) {
        TekstiKappaleViite viite = em.find(TekstiKappaleViite.class, viiteId);
        if (viite != null) {
            Opetussuunnitelma ops = em.find(Opetussuunnitelma.class, opsId);
            if (ops != null && ops.getTekstit().getId().equals(viite.getRoot().getId())) {
                return viite;
            }
        }
        return null;
    }

}
