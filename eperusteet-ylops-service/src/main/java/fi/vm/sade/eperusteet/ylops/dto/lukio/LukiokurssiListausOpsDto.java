package fi.vm.sade.eperusteet.ylops.dto.lukio;

import fi.vm.sade.eperusteet.ylops.domain.lukio.LukiokurssiTyyppi;
import fi.vm.sade.eperusteet.ylops.dto.teksti.LokalisoituTekstiDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LukiokurssiListausOpsDto implements Serializable {
    private Long id;
    private Date muokattu;
    private UUID tunniste;
    private BigDecimal laajuus;
    private LokalisoituTekstiDto nimi;
    private boolean oma;
    private boolean palautettava;
    private String koodiUri;
    private String koodiArvo;
    private LukiokurssiTyyppi tyyppi;
    private LokalisoituTekstiDto lokalisoituKoodi;
}
