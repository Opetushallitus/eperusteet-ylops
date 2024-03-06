package fi.vm.sade.eperusteet.ylops.dto.lukio;

import fi.vm.sade.eperusteet.ylops.domain.lukio.LukiokurssiTyyppi;
import fi.vm.sade.eperusteet.ylops.dto.teksti.LokalisoituTekstiDto;
import fi.vm.sade.eperusteet.ylops.service.external.impl.perustedto.TekstiOsaDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LukiokurssiSaveDto implements Serializable {
    @NotNull
    private long oppiaineId;
    @NotNull
    private LokalisoituTekstiDto nimi;
    @NotNull
    private LukiokurssiTyyppi.Paikallinen tyyppi;
    @NotNull
    private BigDecimal laajuus = BigDecimal.ONE;
    private LokalisoituTekstiDto kuvaus;
    private String koodiUri;
    private String koodiArvo;
    private LokalisoituTekstiDto lokalisoituKoodi;
    private TekstiOsaDto tavoitteet;
    private TekstiOsaDto keskeinenSisalto;
    private TekstiOsaDto tavoitteetJaKeskeinenSisalto;
}
