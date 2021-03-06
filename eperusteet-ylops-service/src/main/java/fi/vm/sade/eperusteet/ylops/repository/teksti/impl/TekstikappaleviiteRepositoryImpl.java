/*
 * Copyright (c) 2013 The Finnish Board of Education - Opetushallitus
 *
 * This program is free software: Licensed under the EUPL, Version 1.1 or - as
 * soon as they will be approved by the European Commission - subsequent versions
 * of the EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at: http://ec.europa.eu/idabc/eupl
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * European Union Public Licence for more details.
 */
package fi.vm.sade.eperusteet.ylops.repository.teksti.impl;

import fi.vm.sade.eperusteet.ylops.domain.ops.Opetussuunnitelma;
import fi.vm.sade.eperusteet.ylops.domain.teksti.TekstiKappaleViite;
import fi.vm.sade.eperusteet.ylops.repository.teksti.TekstikappaleviiteRepositoryCustom;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * @author jhyoty
 */
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
