package fi.vm.sade.eperusteet.ylops.dto.teksti;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TekstiosaDto {
    private Long id;
    private LokalisoituTekstiDto otsikko;
    private LokalisoituTekstiDto teksti;

    public TekstiosaDto(LokalisoituTekstiDto otsikko, LokalisoituTekstiDto teksti) {
        this.otsikko = otsikko;
        this.teksti = teksti;
    }
}
