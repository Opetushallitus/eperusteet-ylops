package fi.vm.sade.eperusteet.ylops.service.ops;

import fi.vm.sade.eperusteet.ylops.repository.ops.OpetussuunnitelmaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class OpetussuunnitelmaAsyncTekstitPohjastaService {

    @Autowired
    private OpetussuunnitelmaRepository opetussuunnitelmaRepository;

    @Autowired
    private OpsDispatcher dispatcher;

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')")
    @Async
    public void syncTekstitPohjastaKaikki(Long opsId) {
        opetussuunnitelmaRepository.findAllByPohjaId(opsId).forEach(opetussuunnitelma -> {
            dispatcher.get(OpsPohjaSynkronointi.class).syncTekstitPohjasta(opetussuunnitelma.getId());
            syncTekstitPohjastaKaikki(opetussuunnitelma.getId());
        });
    }

}
