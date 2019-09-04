package fi.vm.sade.eperusteet.ylops.service.ops.impl;

import fi.vm.sade.eperusteet.ylops.domain.ops.Opetussuunnitelma;
import fi.vm.sade.eperusteet.ylops.dto.teksti.TekstiKappaleViiteDto;
import fi.vm.sade.eperusteet.ylops.service.ops.OpsStrategy;
import fi.vm.sade.eperusteet.ylops.service.ops.OpsStrategyQualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@OpsStrategyQualifier({})
public class OpsStrategyDefaultImpl implements OpsStrategy {
    @Override
    @Transactional(readOnly = false)
    public void reorder(TekstiKappaleViiteDto.Puu tree, Opetussuunnitelma ops) {
        Opetussuunnitelma pohja = ops.getPohja();
        if (pohja != null) {
            Set<UUID> pohjaTekstit = pohja.getTekstit().getLapset().stream()
                    .map(x -> x.getTekstiKappale().getTunniste())
                    .collect(Collectors.toSet());

            // Pois päätason tekstit joita ei pohjassa ole määritelty
            tree.setLapset(tree.getLapset().stream()
                    .filter(x -> {
                        return pohjaTekstit.contains(x.getTekstiKappale().getTunniste());
                    })
                    .collect(Collectors.toList()));
        }
    }
}
