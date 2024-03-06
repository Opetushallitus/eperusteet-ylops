package fi.vm.sade.eperusteet.ylops.dto.peruste;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import fi.vm.sade.eperusteet.ylops.domain.KoulutusTyyppi;
import fi.vm.sade.eperusteet.ylops.domain.KoulutustyyppiToteutus;
import fi.vm.sade.eperusteet.ylops.dto.teksti.LokalisoituTekstiDto;

import java.util.Date;

import fi.vm.sade.eperusteet.ylops.service.external.impl.perustedto.PerusteVersionDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PerusteInfoDto {
    private Long id;
    private PerusteVersionDto globalVersion;
    private LokalisoituTekstiDto nimi;
    private String diaarinumero;
    private Date voimassaoloAlkaa;
    private Date voimassaoloLoppuu;
    private Date muokattu;
    private String tila;
    private KoulutusTyyppi koulutustyyppi;
    private KoulutustyyppiToteutus toteutus;
}
