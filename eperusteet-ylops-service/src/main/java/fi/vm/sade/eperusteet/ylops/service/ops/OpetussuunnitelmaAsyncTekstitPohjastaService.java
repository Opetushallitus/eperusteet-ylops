package fi.vm.sade.eperusteet.ylops.service.ops;

import fi.vm.sade.eperusteet.ylops.domain.ops.Opetussuunnitelma;
import fi.vm.sade.eperusteet.ylops.repository.ops.OpetussuunnitelmaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@Slf4j
public class OpetussuunnitelmaAsyncTekstitPohjastaService {

    @Autowired
    private OpetussuunnitelmaRepository opetussuunnitelmaRepository;

    @Autowired
    private OpsDispatcher dispatcher;

    @PreAuthorize("hasPermission(#opetussuunnitelma.getId(), 'opetussuunnitelma', 'MUOKKAUS') or (#opetussuunnitelma.getPohja() != null and hasPermission(#opetussuunnitelma.getPohja().getId(), 'opetussuunnitelma', 'MUOKKAUS'))")
    @Async
    public void syncTekstitPohjastaKaikki(@P("opetussuunnitelma") Opetussuunnitelma ops) {
        opetussuunnitelmaRepository.findAllByPohjaId(ops.getId()).forEach(opetussuunnitelma -> {
            try {
                dispatcher.get(OpsPohjaSynkronointi.class).syncTekstitPohjasta(opetussuunnitelma);
                syncTekstitPohjastaKaikki(opetussuunnitelma);
            } catch(Exception e) {
                log.error("Virhe synkronoidessa tekstejä opetussuunnitelmaan " + opetussuunnitelma.getId(), e);
                throw e;
            }
        });
    }

}
