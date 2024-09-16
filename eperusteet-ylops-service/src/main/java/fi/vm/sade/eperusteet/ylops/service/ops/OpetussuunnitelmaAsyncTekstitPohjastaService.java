package fi.vm.sade.eperusteet.ylops.service.ops;

import fi.vm.sade.eperusteet.ylops.domain.MuokkausTapahtuma;
import fi.vm.sade.eperusteet.ylops.domain.ops.Opetussuunnitelma;
import fi.vm.sade.eperusteet.ylops.dto.ops.MuokkaustietoLisatieto;
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

    @Autowired
    private OpetussuunnitelmanMuokkaustietoService opetussuunnitelmanMuokkaustietoService;

    @PreAuthorize("hasPermission(#opetussuunnitelma.getId(), 'opetussuunnitelma', 'MUOKKAUS') or (#opetussuunnitelma.getPohja() != null and hasPermission(#opetussuunnitelma.getPohja().getId(), 'opetussuunnitelma', 'MUOKKAUS'))")
    @Async
    public void syncTekstitPohjastaKaikki(@P("opetussuunnitelma") Opetussuunnitelma pohjaOps) {
        opetussuunnitelmaRepository.findAllByPohjaId(pohjaOps.getId()).forEach(opetussuunnitelma -> {
            try {
                opetussuunnitelmanMuokkaustietoService.poistaOpsMuokkaustieto(opetussuunnitelma, MuokkaustietoLisatieto.POHJA_TEKSTI_SYNKRONOITU_VIRHE);
                dispatcher.get(OpsPohjaSynkronointi.class).syncTekstitPohjasta(opetussuunnitelma.getId(), pohjaOps.getId());
            } catch(Exception e) {
                log.error("Virhe synkronoidessa tekstejä opetussuunnitelmaan {}", opetussuunnitelma.getId(), e);
                opetussuunnitelmanMuokkaustietoService.addOpsMuokkausTieto(opetussuunnitelma, opetussuunnitelma, MuokkausTapahtuma.VIRHE, MuokkaustietoLisatieto.POHJA_TEKSTI_SYNKRONOITU_VIRHE);
            }
            syncTekstitPohjastaKaikki(opetussuunnitelma);
        });
    }

}
