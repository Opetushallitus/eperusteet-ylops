package fi.vm.sade.eperusteet.ylops.dto.peruste;

import fi.vm.sade.eperusteet.ylops.service.external.impl.perustedto.TekstiKappaleViiteDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EsiopetuksenPerusteenSisaltoDto {
    private TekstiKappaleViiteDto sisalto;
}
