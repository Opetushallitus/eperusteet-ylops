package fi.vm.sade.eperusteet.ylops.service.util.impl;

import fi.vm.sade.eperusteet.ylops.domain.ops.JulkaistuOpetussuunnitelmaTila;
import fi.vm.sade.eperusteet.ylops.repository.ops.JulkaistuOpetussuunnitelmaTilaRepository;
import fi.vm.sade.eperusteet.ylops.service.util.JulkaistuOpetussuunnitelmaTilaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@Profile("!docker")
public class JulkaistuOpetussuunnitelmaTilaServiceImpl implements JulkaistuOpetussuunnitelmaTilaService {

    @Autowired
    private JulkaistuOpetussuunnitelmaTilaRepository julkaistuOpetussuunnitelmaTilaRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveJulkaistuOpetussuunnitelmaTila(JulkaistuOpetussuunnitelmaTila julkaistuOpetussuunnitelmaTila) {
        julkaistuOpetussuunnitelmaTilaRepository.save(julkaistuOpetussuunnitelmaTila);
    }
}
