package fi.vm.sade.eperusteet.ylops.dto.ops;

import fi.vm.sade.eperusteet.ylops.dto.PoistettuDto;
import fi.vm.sade.eperusteet.ylops.dto.teksti.TekstiKappaleDto;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * Created by autio on 24.2.2016.
 */
@Getter
@Setter
@Deprecated
public class PoistettuOppiaineDto extends PoistettuDto {
    private Long oppiaine;
}
