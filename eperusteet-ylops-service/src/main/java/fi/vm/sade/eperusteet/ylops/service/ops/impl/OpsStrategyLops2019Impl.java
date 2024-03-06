package fi.vm.sade.eperusteet.ylops.service.ops.impl;

import fi.vm.sade.eperusteet.ylops.domain.KoulutustyyppiToteutus;
import fi.vm.sade.eperusteet.ylops.domain.ops.Opetussuunnitelma;
import fi.vm.sade.eperusteet.ylops.dto.teksti.TekstiKappaleViiteDto;
import fi.vm.sade.eperusteet.ylops.service.ops.OpsStrategy;
import fi.vm.sade.eperusteet.ylops.service.ops.OpsStrategyQualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@OpsStrategyQualifier(KoulutustyyppiToteutus.LOPS2019)
public class OpsStrategyLops2019Impl implements OpsStrategy {

    @Override
    @Transactional(readOnly = false)
    public void reorder(TekstiKappaleViiteDto.Puu tree, Opetussuunnitelma ops) {

    }
}
