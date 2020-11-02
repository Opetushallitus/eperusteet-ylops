package fi.vm.sade.eperusteet.ylops.dto.peruste;

import fi.vm.sade.eperusteet.ylops.service.external.impl.perustedto.TekstiKappaleViiteDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TPOOpetuksenSisaltoDto {
    private TekstiKappaleViiteDto sisalto;
}
