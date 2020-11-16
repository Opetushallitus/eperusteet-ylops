package fi.vm.sade.eperusteet.ylops.dto.lops2019;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Lops2019PerustePaikallinenOppiaineDto {
    private Integer jarjestys;
    private Lops2019SortableOppiaineDto oa;
    private Lops2019PaikallinenOppiaineDto poa;
    private boolean paikallinen;
}
