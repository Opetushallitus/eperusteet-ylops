package fi.vm.sade.eperusteet.ylops.dto.lops2019;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Lops2019PerustePaikallinenOppiaineDto {
    private Integer jarjestys;
    private Lops2019SortableOppiaineDto oa;
    private Lops2019SortablePaikallinenOppiaineDto poa;
    private boolean paikallinen;
}
