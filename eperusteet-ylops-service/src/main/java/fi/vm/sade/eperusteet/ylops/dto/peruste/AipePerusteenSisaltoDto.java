package fi.vm.sade.eperusteet.ylops.dto.peruste;

import fi.vm.sade.eperusteet.ylops.service.external.impl.perustedto.TekstiKappaleViiteDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AipePerusteenSisaltoDto {
    private TekstiKappaleViiteDto sisalto;
    private List<PerusteLaajaalainenosaaminenDto> laajaalaisetosaamiset = new ArrayList<>();
}
