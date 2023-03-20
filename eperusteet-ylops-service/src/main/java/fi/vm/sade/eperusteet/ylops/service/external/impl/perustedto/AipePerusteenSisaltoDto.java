package fi.vm.sade.eperusteet.ylops.service.external.impl.perustedto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AipePerusteenSisaltoDto implements Serializable {
    private TekstiKappaleViiteDto sisalto;
    private List<LaajaalainenOsaaminenDto> laajaalaisetosaamiset;
}
