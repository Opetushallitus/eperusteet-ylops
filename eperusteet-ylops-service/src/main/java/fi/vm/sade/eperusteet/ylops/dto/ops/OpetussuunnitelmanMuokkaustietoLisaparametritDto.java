package fi.vm.sade.eperusteet.ylops.dto.ops;

import fi.vm.sade.eperusteet.ylops.dto.navigation.NavigationType;
import lombok.Data;

@Data
public class OpetussuunnitelmanMuokkaustietoLisaparametritDto {

    private NavigationType kohde;
    private Long kohdeId;
}
