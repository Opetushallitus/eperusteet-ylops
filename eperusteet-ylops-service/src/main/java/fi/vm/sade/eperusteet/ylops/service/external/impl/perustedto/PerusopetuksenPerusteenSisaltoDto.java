package fi.vm.sade.eperusteet.ylops.service.external.impl.perustedto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PerusopetuksenPerusteenSisaltoDto implements Serializable {
    private TekstiKappaleViiteDto sisalto;
    private Set<LaajaalainenOsaaminenDto> laajaalaisetosaamiset;
    private Set<VuosiluokkakokonaisuusDto> vuosiluokkakokonaisuudet;
    private Set<OppiaineDto> oppiaineet;
}
