package fi.vm.sade.eperusteet.ylops.service.ops;

import fi.vm.sade.eperusteet.ylops.domain.KoulutustyyppiToteutus;

import java.util.Set;

public interface OpsToteutus {
    Set<KoulutustyyppiToteutus> getTyypit();
    Class getImpl();
}
