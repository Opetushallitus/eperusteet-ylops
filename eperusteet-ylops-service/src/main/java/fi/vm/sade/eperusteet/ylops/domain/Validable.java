package fi.vm.sade.eperusteet.ylops.domain;

import fi.vm.sade.eperusteet.ylops.domain.teksti.LokalisoituTeksti;
import fi.vm.sade.eperusteet.ylops.dto.lops2019.Validointi.ValidointiContext;
import fi.vm.sade.eperusteet.ylops.dto.lops2019.Validointi.ValidointiDto;

public interface Validable extends ReferenceableEntity {
    void validate(ValidointiDto validointi, ValidointiContext ctx);
    ValidationCategory category();
    LokalisoituTeksti getNimi();
}
