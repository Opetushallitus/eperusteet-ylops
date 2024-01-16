package fi.vm.sade.eperusteet.ylops.service.external.impl.perustedto;

import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import fi.vm.sade.eperusteet.ylops.dto.ReferenceableDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OppiaineenVuosiluokkaKokonaisuusDto implements ReferenceableDto {
    private Long id;
    @JsonIdentityReference(alwaysAsId = true)
    @JsonProperty("_vuosiluokkaKokonaisuus")
    private VuosiluokkakokonaisuusDto vuosiluokkaKokonaisuus;
    private TekstiOsaDto tehtava;
    private TekstiOsaDto tyotavat;
    private TekstiOsaDto ohjaus;
    private TekstiOsaDto arviointi;
    private TekstiOsaDto tavoitteistaJohdetutOppimisenTavoitteet;
    private TekstiOsaDto sisaltoalueinfo;
    private List<TekstiKappaleDto> vapaatTekstit;
    private PerusteenLokalisoituTekstiDto opetuksenTavoitteetOtsikko;
    private PerusteenLokalisoituTekstiDto vapaaTeksti;
    private List<OpetuksenTavoiteDto> tavoitteet;
    private List<KeskeinenSisaltoalueDto> sisaltoalueet;
    private List<OppiaineenTavoitteenOpetuksenTavoiteDto> oppiaineenTavoitteenOpetuksenTavoitteet;
}
