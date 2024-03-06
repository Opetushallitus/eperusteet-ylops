package fi.vm.sade.eperusteet.ylops.dto.peruste.lukio;

import fi.vm.sade.eperusteet.ylops.dto.teksti.LokalisoituTekstiDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AihekokonaisuusDto implements Serializable, PerusteenOsa {
    private UUID tunniste;
    private Long id;
    private LokalisoituTekstiDto otsikko;
    private LokalisoituTekstiDto yleiskuvaus;
    private Long jnro;
    private AihekokonaisuusDto parent;
    private Date muokattu;
    private String muokkaaja;
}
