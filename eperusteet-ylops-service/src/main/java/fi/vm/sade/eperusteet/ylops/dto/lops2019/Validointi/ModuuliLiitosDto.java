package fi.vm.sade.eperusteet.ylops.dto.lops2019.Validointi;

import fi.vm.sade.eperusteet.ylops.dto.lops2019.Lops2019OpintojaksoBaseDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class ModuuliLiitosDto {
    String moduuliKoodiUri;
    List<Lops2019OpintojaksoBaseDto> opintojaksot;
}
