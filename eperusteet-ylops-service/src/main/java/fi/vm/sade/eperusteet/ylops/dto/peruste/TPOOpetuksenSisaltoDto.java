package fi.vm.sade.eperusteet.ylops.dto.peruste;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fi.vm.sade.eperusteet.ylops.dto.peruste.tpo.TpoPerusteenTaiteenalaDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.tpo.TpoSisaltoViiteDto;
import fi.vm.sade.eperusteet.ylops.service.external.impl.perustedto.TekstiKappaleViiteDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TPOOpetuksenSisaltoDto {
    private TpoSisaltoViiteDto sisalto;

    @JsonIgnore
    public List<TpoPerusteenTaiteenalaDto> getTaiteenalat() {
        return sisalto != null ? sisalto.getTaiteenalat() : Collections.emptyList();
    }

    @JsonIgnore
    public TekstiKappaleViiteDto getTekstiKappaleViiteSisalto() {
        return sisalto != null ? sisalto.getTekstiKappaleViiteSisalto() : null;
    }
}
