package fi.vm.sade.eperusteet.ylops.service.ops.impl;

import com.google.common.collect.Sets;
import fi.vm.sade.eperusteet.ylops.domain.KoulutustyyppiToteutus;
import fi.vm.sade.eperusteet.ylops.domain.ops.Opetussuunnitelma;
import fi.vm.sade.eperusteet.ylops.domain.teksti.TekstiKappaleViite;
import fi.vm.sade.eperusteet.ylops.repository.ops.OpetussuunnitelmaRepository;
import fi.vm.sade.eperusteet.ylops.service.ops.OpetussuunnitelmaHierarkiaKopiointiService;
import fi.vm.sade.eperusteet.ylops.service.ops.OpsPohjaSynkronointi;
import fi.vm.sade.eperusteet.ylops.service.util.CollectionUtil;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class OpsPohjaSynkronointiDefaultImpl implements OpsPohjaSynkronointi {

    @Autowired
    private OpetussuunnitelmaRepository opetussuunnitelmaRepository;

    @Autowired
    private OpetussuunnitelmaHierarkiaKopiointiService hierarkiaKopiointiService;

    @Override
    public void syncTekstitPohjasta(Long opsId) {
        Opetussuunnitelma ops = opetussuunnitelmaRepository.getOne(opsId);
        Opetussuunnitelma pohja = opetussuunnitelmaRepository.getOne(ops.getPohja().getId());
        hierarkiaKopiointiService.kopioiPohjanRakenne(ops, pohja);
    }

    @Override
    public boolean opetussuunnitelmanPohjallaUusiaTeksteja(Long opsId) {
        Opetussuunnitelma ops = opetussuunnitelmaRepository.getOne(opsId);

        if (ops.getPohja() == null) {
            return false;
        }

        Opetussuunnitelma pohja = opetussuunnitelmaRepository.getOne(ops.getPohja().getId());

        Set<Long> opsinPerusteTekstikappaleIdt = CollectionUtil.treeToStream(
                ops.getTekstit(),
                TekstiKappaleViite::getLapset)
                .map(TekstiKappaleViite::getPerusteTekstikappaleId)
                .filter(perusteTekstikappaleId -> perusteTekstikappaleId != null)
                .collect(Collectors.toSet());

        Set<Long> pohjanPerusteTekstikappaleIdt = CollectionUtil.treeToStream(
                pohja.getTekstit(),
                TekstiKappaleViite::getLapset)
                .map(TekstiKappaleViite::getPerusteTekstikappaleId)
                .filter(perusteTekstikappaleId -> perusteTekstikappaleId != null)
                .collect(Collectors.toSet());

        return pohjanPerusteTekstikappaleIdt.size() != opsinPerusteTekstikappaleIdt.size()
                || !opsinPerusteTekstikappaleIdt.containsAll(pohjanPerusteTekstikappaleIdt);
    }

    @Override
    public Set<KoulutustyyppiToteutus> getTyypit() {
        return Collections.emptySet();
    }
}
