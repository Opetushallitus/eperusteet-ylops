package fi.vm.sade.eperusteet.ylops.dto.tpo;

import fi.vm.sade.eperusteet.ylops.dto.KooditettuDto;
import fi.vm.sade.eperusteet.ylops.dto.teksti.LokalisoituTekstiDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaiteenalaDto implements KooditettuDto {
    private Long id;
    private String koodi;
    private LokalisoituTekstiDto nimi;
    private LokalisoituTekstiDto paikallinenTarkennus;
    private List<TaiteenosaDto> taiteenosat = new ArrayList<>();

    @Override
    public void setKooditettu(LokalisoituTekstiDto kooditettu) {
        this.nimi = kooditettu;
    }
}
