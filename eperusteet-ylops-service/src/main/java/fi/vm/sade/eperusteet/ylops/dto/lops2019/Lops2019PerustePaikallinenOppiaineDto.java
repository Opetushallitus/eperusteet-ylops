package fi.vm.sade.eperusteet.ylops.dto.lops2019;

import fi.vm.sade.eperusteet.ylops.dto.peruste.lops2019.oppiaineet.Lops2019OppiaineKaikkiDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Lops2019PerustePaikallinenOppiaineDto {
    private Integer jarjestys;
    private Lops2019OppiaineKevytDto oaKevyt;
    private Lops2019OppiaineKaikkiDto oa;
    private Lops2019PaikallinenOppiaineDto poa;
}
