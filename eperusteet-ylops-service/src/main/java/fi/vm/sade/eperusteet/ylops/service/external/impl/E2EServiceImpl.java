package fi.vm.sade.eperusteet.ylops.service.external.impl;

import fi.vm.sade.eperusteet.ylops.service.util.E2EService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

@Profile("e2e")
@Service
@Transactional
public class E2EServiceImpl implements E2EService {

    @Autowired
    private EntityManager em;

    @Autowired
    Environment env;

    @Override
    public void reset() {
        em.createNativeQuery("truncate opetussuunnitelma cascade;").executeUpdate();
    }

}
