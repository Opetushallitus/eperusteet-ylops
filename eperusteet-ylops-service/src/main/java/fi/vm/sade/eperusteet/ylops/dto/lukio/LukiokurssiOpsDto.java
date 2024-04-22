package fi.vm.sade.eperusteet.ylops.dto.lukio;

import fi.vm.sade.eperusteet.ylops.dto.peruste.lukio.LukiokurssiPerusteDto;
import fi.vm.sade.eperusteet.ylops.dto.teksti.LokalisoituTekstiDto;
import fi.vm.sade.eperusteet.ylops.dto.teksti.TekstiosaDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class LukiokurssiOpsDto extends LukiokurssiListausOpsDto
        implements PerusteeseenViittaava<LukiokurssiPerusteDto> {
    private LukiokurssiPerusteDto perusteen;
    private LokalisoituTekstiDto kuvaus;
    private TekstiosaDto tavoitteet;
    private TekstiosaDto keskeinenSisalto;
    private TekstiosaDto tavoitteetJaKeskeinenSisalto;
}
