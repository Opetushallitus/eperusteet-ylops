package fi.vm.sade.eperusteet.ylops.dto.peruste;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import fi.vm.sade.eperusteet.ylops.dto.peruste.aipe.PerusteAIPEVaiheDto;
import fi.vm.sade.eperusteet.ylops.service.external.impl.perustedto.TekstiKappaleViiteDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AipePerusteenSisaltoDto {
    private TekstiKappaleViiteDto sisalto;
    private List<PerusteLaajaalainenosaaminenDto> laajaalaisetosaamiset = new ArrayList<>();
    private List<PerusteAIPEVaiheDto> vaiheet = new ArrayList<>();
}
