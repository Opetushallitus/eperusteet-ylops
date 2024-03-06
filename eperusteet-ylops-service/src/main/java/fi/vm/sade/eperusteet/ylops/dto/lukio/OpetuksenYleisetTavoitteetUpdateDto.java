package fi.vm.sade.eperusteet.ylops.dto.lukio;

import fi.vm.sade.eperusteet.ylops.dto.teksti.LokalisoituTekstiDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OpetuksenYleisetTavoitteetUpdateDto implements Serializable {
    private LokalisoituTekstiDto otsikko;
    private LokalisoituTekstiDto kuvaus;
}
