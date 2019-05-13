package fi.vm.sade.eperusteet.ylops.dto.lops2019.Validointi;

import fi.vm.sade.eperusteet.ylops.dto.KoodiDto;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
public class Lops2019ValidointiDto {
    private Set<KoodiDto> kaikkiModuulit = new HashSet<>();
    private Set<ModuuliLiitosDto> liitetytModuulit = new HashSet<>();
    private List<Lops2019KohdeDto> validoinnit = new ArrayList<>();

    public Lops2019KohdeDto validointi(String kohde) {
        Lops2019KohdeDto result = new Lops2019KohdeDto();
        result.setKohde(kohde);
        validoinnit.add(result);
        return result;
    }
}
