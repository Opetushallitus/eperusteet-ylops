package fi.vm.sade.eperusteet.ylops.service.ops;

import fi.vm.sade.eperusteet.ylops.domain.KoulutustyyppiToteutus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OpsFeaturesFactory<T> {

    @Autowired
    @OpsStrategyQualifier({})
    private T opsStrategyDefault;

    @Autowired(required = false)
    @OpsStrategyQualifier(KoulutustyyppiToteutus.LOPS2019)
    private T opsStrategyLops2019;

    @Autowired(required = false)
    @OpsStrategyQualifier(KoulutustyyppiToteutus.PERUSOPETUS)
    private T opsStrategyPerusopetus;

    @Autowired(required = false)
    @OpsStrategyQualifier(KoulutustyyppiToteutus.YKSINKERTAINEN)
    private T opsStrategyYksinkertainen;

    private T tryToGetStrategy(KoulutustyyppiToteutus toteutus) {
        switch (toteutus) {
            case LOPS2019: return opsStrategyLops2019;
            case PERUSOPETUS: return opsStrategyPerusopetus;
            case YKSINKERTAINEN: return opsStrategyYksinkertainen;
            default: return opsStrategyDefault;
        }
    }

    public T getStrategy(KoulutustyyppiToteutus toteutus) {
        if (toteutus == null) {
            return opsStrategyDefault;
        }

        T concrete = tryToGetStrategy(toteutus);
        if (concrete == null) {
            return opsStrategyDefault;
        }
        else {
            return concrete;
        }
    }
}
