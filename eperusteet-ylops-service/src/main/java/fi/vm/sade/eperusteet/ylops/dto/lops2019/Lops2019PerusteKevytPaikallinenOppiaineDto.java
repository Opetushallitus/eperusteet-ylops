package fi.vm.sade.eperusteet.ylops.dto.lops2019;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Lops2019PerusteKevytPaikallinenOppiaineDto {
    private Integer jarjestys;
    private Lops2019OppiaineKevytDto oa;
    private Lops2019PaikallinenOppiaineDto poa;
}
