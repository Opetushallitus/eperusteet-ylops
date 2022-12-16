package fi.vm.sade.eperusteet.ylops.dto.ops;

import fi.vm.sade.eperusteet.ylops.dto.navigation.NavigationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OpetussuunnitelmanMuokkaustietoLisaparametritDto {

    private NavigationType kohde;
    private Long kohdeId;
}
