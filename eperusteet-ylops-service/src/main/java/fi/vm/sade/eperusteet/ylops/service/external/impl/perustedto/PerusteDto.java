package fi.vm.sade.eperusteet.ylops.service.external.impl.perustedto;

import fi.vm.sade.eperusteet.ylops.domain.KoulutusTyyppi;
import fi.vm.sade.eperusteet.ylops.domain.KoulutustyyppiToteutus;
import fi.vm.sade.eperusteet.ylops.domain.teksti.Kieli;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PerusteDto {
    private Long id;
    private PerusteenLokalisoituTekstiDto nimi;
    private KoulutusTyyppi koulutustyyppi;
    private Set<KoulutusDto> koulutukset;
    private Set<Kieli> kielet;
    private PerusteenLokalisoituTekstiDto kuvaus;
    private String diaarinumero;
    private Date voimassaoloAlkaa;
    private Date siirtymaPaattyy;
    private Date voimassaoloLoppuu;
    private Date muokattu;
    private String tila;
    private String tyyppi;
    private KoulutustyyppiToteutus toteutus;
    private Set<String> korvattavatDiaarinumerot;
}
