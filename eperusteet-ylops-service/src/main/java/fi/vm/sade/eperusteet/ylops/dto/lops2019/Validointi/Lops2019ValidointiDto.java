package fi.vm.sade.eperusteet.ylops.dto.lops2019.Validointi;

import fi.vm.sade.eperusteet.ylops.dto.KoodiDto;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class Lops2019ValidointiDto {
    private Set<KoodiDto> kaikkiModuulit = new HashSet<>();
    private Set<ModuuliLiitosDto> liitetytModuulit = new HashSet<>();
}
