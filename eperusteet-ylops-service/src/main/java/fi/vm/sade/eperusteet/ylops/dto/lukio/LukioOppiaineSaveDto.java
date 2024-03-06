package fi.vm.sade.eperusteet.ylops.dto.lukio;

import fi.vm.sade.eperusteet.ylops.domain.lukio.LukiokurssiTyyppi;
import fi.vm.sade.eperusteet.ylops.dto.teksti.LokalisoituTekstiDto;
import fi.vm.sade.eperusteet.ylops.dto.teksti.TekstiosaDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LukioOppiaineSaveDto implements Serializable {
    private Long oppiaineId;
    private LokalisoituTekstiDto nimi;
    private String laajuus;
    private boolean koosteinen;
    private String koodiUri;
    private String koodiArvo;
    private TekstiosaDto tehtava;
    private TekstiosaDto tavoitteet;
    private TekstiosaDto arviointi;
    private Map<LukiokurssiTyyppi, LokalisoituTekstiDto> kurssiTyyppiKuvaukset = new HashMap<>();
}
