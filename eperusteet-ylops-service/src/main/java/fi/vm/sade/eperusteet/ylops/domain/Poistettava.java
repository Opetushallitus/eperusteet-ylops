package fi.vm.sade.eperusteet.ylops.domain;

import fi.vm.sade.eperusteet.ylops.domain.lops2019.PoistetunTyyppi;
import fi.vm.sade.eperusteet.ylops.domain.teksti.LokalisoituTeksti;

public interface Poistettava {
    Long getId();
    LokalisoituTeksti getNimi();
    PoistetunTyyppi getPoistetunTyyppi();
}
