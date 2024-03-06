package fi.vm.sade.eperusteet.ylops.dto.lukio;

import fi.vm.sade.eperusteet.ylops.domain.Tila;
import fi.vm.sade.eperusteet.ylops.domain.oppiaine.OppiaineTyyppi;
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
public class LukioOppimaaraPerusTiedotDto implements Serializable {
    private Long id;
    private Long oppiaineId;
    private Date muokattu;
    private UUID tunniste;
    private Tila tila;
    private boolean oma;
    private boolean maariteltyPohjassa;
    private Integer jarjestys;
    private OppiaineTyyppi tyyppi;
    private String laajuus;
    private boolean koosteinen;
    private LokalisoituTekstiDto nimi;
    private Boolean abstrakti;
    private String koodiUri;
    private String koodiArvo;
    private String kieliKoodiUri;
    private String kieliKoodiArvo;
}
