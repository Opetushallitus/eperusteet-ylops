package fi.vm.sade.eperusteet.ylops.dto.lops2019;

import fi.vm.sade.eperusteet.ylops.dto.ops.OpsSisaltoViite;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Lops2019OpintojaksoListaDto {
    private List<OpsSisaltoViite.Opintojakso> opintojaksot;
}
