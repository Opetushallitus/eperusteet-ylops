package fi.vm.sade.eperusteet.ylops.dto.ops;

import fi.vm.sade.eperusteet.ylops.dto.teksti.LokalisoituTekstiDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VapaatekstiPaikallinentarkennusDto {
    private Long perusteenVapaaTekstiId;
    private LokalisoituTekstiDto paikallinenTarkennus;
}
