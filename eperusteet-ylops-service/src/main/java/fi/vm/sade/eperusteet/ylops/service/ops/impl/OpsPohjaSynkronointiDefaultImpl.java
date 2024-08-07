package fi.vm.sade.eperusteet.ylops.service.ops.impl;

import fi.vm.sade.eperusteet.ylops.domain.KoulutustyyppiToteutus;
import fi.vm.sade.eperusteet.ylops.domain.MuokkausTapahtuma;
import fi.vm.sade.eperusteet.ylops.domain.ops.Opetussuunnitelma;
import fi.vm.sade.eperusteet.ylops.domain.teksti.TekstiKappaleViite;
import fi.vm.sade.eperusteet.ylops.dto.ops.MuokkaustietoLisatieto;
import fi.vm.sade.eperusteet.ylops.repository.ops.OpetussuunnitelmaRepository;
import fi.vm.sade.eperusteet.ylops.service.exception.BusinessRuleViolationException;
import fi.vm.sade.eperusteet.ylops.service.ops.OpetussuunnitelmaHierarkiaKopiointiService;
import fi.vm.sade.eperusteet.ylops.service.ops.OpetussuunnitelmanMuokkaustietoService;
import fi.vm.sade.eperusteet.ylops.service.ops.OpsPohjaSynkronointi;
import fi.vm.sade.eperusteet.ylops.service.util.CollectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class OpsPohjaSynkronointiDefaultImpl implements OpsPohjaSynkronointi {

    @Autowired
    private OpetussuunnitelmaRepository opetussuunnitelmaRepository;

    @Autowired
    private OpetussuunnitelmaHierarkiaKopiointiService hierarkiaKopiointiService;

    @Autowired
    private OpetussuunnitelmanMuokkaustietoService opetussuunnitelmanMuokkaustietoService;

    @Override
    public void syncTekstitPohjasta(Opetussuunnitelma ops) {
        Opetussuunnitelma pohja = opetussuunnitelmaRepository.findOne(ops.getPohja().getId());
        Set<UUID> aiemmatTekstikappaleTunnisteet = getOpetussuunnitelmaOmatTekstikappaleViiteUUID(ops);
        hierarkiaKopiointiService.kopioiPohjanRakenne(ops, pohja);
        Set<UUID> uudetTekstikappaleTunnisteet = getOpetussuunnitelmaOmatTekstikappaleViiteUUID(ops);

        if (aiemmatTekstikappaleTunnisteet.size() > 0 && !uudetTekstikappaleTunnisteet.containsAll(aiemmatTekstikappaleTunnisteet)) {
            throw new BusinessRuleViolationException("hierarkiakopiointi-epaonnistui");
        }

        opetussuunnitelmanMuokkaustietoService.addOpsMuokkausTieto(ops, ops, MuokkausTapahtuma.PAIVITYS, MuokkaustietoLisatieto.POHJA_TEKSTI_SYNKRONOITU);
    }

    private Set<UUID> getOpetussuunnitelmaOmatTekstikappaleViiteUUID(Opetussuunnitelma ops) {
        return CollectionUtil.treeToStream(
                ops.getTekstit(),
                TekstiKappaleViite::getLapset)
                .filter(tkv -> tkv.getVanhempi() != null)
                .filter(tkv -> tkv.getPerusteTekstikappaleId() == null)
                .map(tkv -> tkv.getTekstiKappale().getTunniste())
                .collect(Collectors.toSet());
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
