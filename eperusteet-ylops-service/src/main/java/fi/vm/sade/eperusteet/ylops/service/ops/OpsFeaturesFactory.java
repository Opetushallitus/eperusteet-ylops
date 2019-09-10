package fi.vm.sade.eperusteet.ylops.service.ops;

import fi.vm.sade.eperusteet.ylops.domain.KoulutustyyppiToteutus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OpsFeaturesFactory {

    @Autowired
    @OpsStrategyQualifier({})
    private OpsStrategy opsStrategyDefault;

    @Autowired(required = false)
    @OpsStrategyQualifier(KoulutustyyppiToteutus.LOPS2019)
    private OpsStrategy opsStrategyLops2019;

    @Autowired(required = false)
    @OpsStrategyQualifier(KoulutustyyppiToteutus.PERUSOPETUS)
    private OpsStrategy opsStrategyPerusopetus;

    @Autowired(required = false)
    @OpsStrategyQualifier(KoulutustyyppiToteutus.YKSINKERTAINEN)
    private OpsStrategy opsStrategyYksinkertainen;

    private OpsStrategy tryToGetStrategy(KoulutustyyppiToteutus toteutus) {
        switch (toteutus) {
            case LOPS2019: return opsStrategyLops2019;
            case PERUSOPETUS: return opsStrategyPerusopetus;
            case YKSINKERTAINEN: return opsStrategyYksinkertainen;
            default: return opsStrategyDefault;
        }
    }

    public OpsStrategy getStrategy(KoulutustyyppiToteutus toteutus) {
        if (toteutus == null) {
            return opsStrategyDefault;
        }

        OpsStrategy concrete = tryToGetStrategy(toteutus);
        if (concrete == null) {
            return opsStrategyDefault;
        }
        else {
            return concrete;
        }
    }
}
