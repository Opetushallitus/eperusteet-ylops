package fi.vm.sade.eperusteet.ylops.dto.lops2019.Validointi;

import fi.vm.sade.eperusteet.ylops.dto.KoodiDto;
import fi.vm.sade.eperusteet.ylops.service.mapping.DtoMapper;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class Lops2019ValidointiDto extends ValidointiDto<Lops2019ValidointiDto> {
    private Set<KoodiDto> kaikkiModuulit = new HashSet<>();
    private Set<ModuuliLiitosDto> liitetytModuulit = new HashSet<>();

    @Override
    protected int kaikki() {
        return kaikkiModuulit.size();
    }

    @Override
    protected int onnistuneet() {
        return liitetytModuulit.size();
    }

    public Lops2019ValidointiDto(DtoMapper mapper) {
        super(mapper);
    }
}
