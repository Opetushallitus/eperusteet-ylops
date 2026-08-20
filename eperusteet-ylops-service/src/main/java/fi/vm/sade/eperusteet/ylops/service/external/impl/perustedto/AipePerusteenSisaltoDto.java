package fi.vm.sade.eperusteet.ylops.service.external.impl.perustedto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import fi.vm.sade.eperusteet.ylops.dto.peruste.aipe.PerusteAIPEVaiheDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AipePerusteenSisaltoDto implements Serializable {
    private TekstiKappaleViiteDto sisalto;
    private List<LaajaalainenOsaaminenDto> laajaalaisetosaamiset;
    private List<PerusteAIPEVaiheDto> vaiheet = new ArrayList<>();
}
