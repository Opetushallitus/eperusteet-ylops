package fi.vm.sade.eperusteet.ylops.dto.lops2019.Validointi;

import fi.vm.sade.eperusteet.ylops.dto.lops2019.Lops2019OpintojaksoBaseDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
@NoArgsConstructor
public class ModuuliLiitosDto {
    String moduuliKoodiUri;
    List<Lops2019OpintojaksoBaseDto> opintojaksot;

    public ModuuliLiitosDto(String moduuliKoodiUri, List<Lops2019OpintojaksoBaseDto> opintojaksot) {
        this.moduuliKoodiUri = moduuliKoodiUri;
        this.opintojaksot = opintojaksot;
    }
}
