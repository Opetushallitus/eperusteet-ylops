package fi.vm.sade.eperusteet.ylops.dto.tpo;

import fi.vm.sade.eperusteet.ylops.dto.teksti.LokalisoituTekstiDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaiteenosaDto {
    private Long id;
    private Long perusteenTaiteenosanId;
    private LokalisoituTekstiDto paikallinenTarkennus;
}
