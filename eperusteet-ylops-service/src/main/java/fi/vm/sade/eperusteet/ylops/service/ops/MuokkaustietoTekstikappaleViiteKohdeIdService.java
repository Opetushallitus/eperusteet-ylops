package fi.vm.sade.eperusteet.ylops.service.ops;

import fi.vm.sade.eperusteet.ylops.domain.ops.Opetussuunnitelma;
import fi.vm.sade.eperusteet.ylops.domain.teksti.LokalisoituTeksti;
import fi.vm.sade.eperusteet.ylops.domain.teksti.TekstiKappale;
import fi.vm.sade.eperusteet.ylops.domain.teksti.TekstiKappaleViite;
import fi.vm.sade.eperusteet.ylops.dto.navigation.NavigationType;
import fi.vm.sade.eperusteet.ylops.dto.ops.MuokkaustietoKayttajallaDto;
import fi.vm.sade.eperusteet.ylops.repository.ops.OpetussuunnitelmaRepository;
import fi.vm.sade.eperusteet.ylops.repository.teksti.TekstikappaleviiteRepository;
import fi.vm.sade.eperusteet.ylops.service.util.CollectionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Korjaa vanhentuneet tekstikappaleviite-kohdeId:t vastaamaan nykyistä ops-puuta.
 * Vastaavuus: sama {@code tekstikappale.teksti_id} (lokalisoitu teksti -rivi).
 */
@Service
@Transactional(readOnly = true)
public class MuokkaustietoTekstikappaleViiteKohdeIdService {

    private static final Set<NavigationType> TEKSTIKAPPALE_VIITE_KOHTEET =
            EnumSet.of(NavigationType.viite, NavigationType.liite);

    @Autowired
    private OpetussuunnitelmaRepository opetussuunnitelmaRepository;

    @Autowired
    private TekstikappaleviiteRepository tekstikappaleviiteRepository;

    public void korjaa(Long opsId, List<MuokkaustietoKayttajallaDto> muokkaustiedot) {
        Opetussuunnitelma ops = opetussuunnitelmaRepository.findOne(opsId);
        if (ops == null || ops.getTekstit() == null) {
            return;
        }

        Set<Long> nykyisetViiteIdt = CollectionUtil.treeToStream(ops.getTekstit(), TekstiKappaleViite::getLapset)
                .map(TekstiKappaleViite::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        boolean tarvitseeKorjausta = muokkaustiedot.stream()
                .anyMatch(mt -> TEKSTIKAPPALE_VIITE_KOHTEET.contains(mt.getKohde())
                        && mt.getKohdeId() != null
                        && !nykyisetViiteIdt.contains(mt.getKohdeId()));
        if (!tarvitseeKorjausta) {
            return;
        }

        Map<Long, Long> tekstiIdNykyinenViiteId = keraaTekstiIdViiteIdKartta(ops.getTekstit());
        TekstikappaleViiteKohdeResolver resolver = new TekstikappaleViiteKohdeResolver(
                opsId, tekstiIdNykyinenViiteId, nykyisetViiteIdt);

        muokkaustiedot.stream()
                .filter(mt -> TEKSTIKAPPALE_VIITE_KOHTEET.contains(mt.getKohde()))
                .filter(mt -> mt.getKohdeId() != null && !nykyisetViiteIdt.contains(mt.getKohdeId()))
                .forEach(mt -> resolver.ratkaise(mt.getKohdeId()).ifPresent(mt::setKohdeId));
    }

    private Map<Long, Long> keraaTekstiIdViiteIdKartta(TekstiKappaleViite juuri) {
        Map<Long, Long> kartta = new HashMap<>();
        CollectionUtil.treeToStream(juuri, TekstiKappaleViite::getLapset)
                .forEach(viite -> haeTekstiId(viite).ifPresent(tekstiId -> kartta.putIfAbsent(tekstiId, viite.getId())));
        return kartta;
    }

    private Optional<Long> haeTekstiId(TekstiKappaleViite viite) {
        TekstiKappale tekstiKappale = viite.getTekstiKappale();
        if (tekstiKappale == null) {
            return Optional.empty();
        }
        LokalisoituTeksti teksti = tekstiKappale.getTeksti();
        return teksti != null ? Optional.of(teksti.getId()) : Optional.empty();
    }

    private class TekstikappaleViiteKohdeResolver {

        private final Long opsId;
        private final Map<Long, Long> tekstiIdNykyinenViiteId;
        private final Set<Long> nykyisetViiteIdt;
        private final Map<Long, Long> vanhaViiteIdUusiViiteId = new HashMap<>();
        private final Map<Long, Long> vanhaViiteIdTekstiId = new HashMap<>();
        private boolean aiemmatTekstitKasitelty;

        private TekstikappaleViiteKohdeResolver(
                Long opsId,
                Map<Long, Long> tekstiIdNykyinenViiteId,
                Set<Long> nykyisetViiteIdt
        ) {
            this.opsId = opsId;
            this.tekstiIdNykyinenViiteId = tekstiIdNykyinenViiteId;
            this.nykyisetViiteIdt = nykyisetViiteIdt;
        }

        Optional<Long> ratkaise(Long vanhaKohdeId) {
            if (vanhaKohdeId == null) {
                return Optional.empty();
            }
            if (nykyisetViiteIdt.contains(vanhaKohdeId)) {
                return Optional.of(vanhaKohdeId);
            }
            if (vanhaViiteIdUusiViiteId.containsKey(vanhaKohdeId)) {
                return Optional.ofNullable(vanhaViiteIdUusiViiteId.get(vanhaKohdeId));
            }

            return haeTekstiIdVanhalleViitteelle(vanhaKohdeId)
                    .flatMap(tekstiId -> Optional.ofNullable(tekstiIdNykyinenViiteId.get(tekstiId)))
                    .map(uusiViiteId -> {
                        vanhaViiteIdUusiViiteId.put(vanhaKohdeId, uusiViiteId);
                        return uusiViiteId;
                    });
        }

        private Optional<Long> haeTekstiIdVanhalleViitteelle(Long vanhaViiteId) {
            if (vanhaViiteIdTekstiId.containsKey(vanhaViiteId)) {
                return Optional.ofNullable(vanhaViiteIdTekstiId.get(vanhaViiteId));
            }

            TekstiKappaleViite viite = tekstikappaleviiteRepository.findOne(vanhaViiteId);
            Optional<Long> tekstiId = viite != null ? haeTekstiId(viite) : Optional.empty();
            if (tekstiId.isPresent()) {
                vanhaViiteIdTekstiId.put(vanhaViiteId, tekstiId.get());
                return tekstiId;
            }

            kasitteleAiemmatTekstitJuuret();
            return Optional.ofNullable(vanhaViiteIdTekstiId.get(vanhaViiteId));
        }

        private void kasitteleAiemmatTekstitJuuret() {
            if (aiemmatTekstitKasitelty) {
                return;
            }
            aiemmatTekstitKasitelty = true;

            for (Long aiempiTekstitJuuriId : opetussuunnitelmaRepository.findAiemmatTekstitIdt(opsId)) {
                TekstiKappaleViite juuri = tekstikappaleviiteRepository.findOne(aiempiTekstitJuuriId);
                if (juuri == null) {
                    continue;
                }
                CollectionUtil.treeToStream(juuri, TekstiKappaleViite::getLapset)
                        .filter(viite -> viite.getId() != null)
                        .forEach(viite -> haeTekstiId(viite).ifPresent(tekstiId ->
                                vanhaViiteIdTekstiId.putIfAbsent(viite.getId(), tekstiId)));
            }
        }
    }
}
