package fi.vm.sade.eperusteet.ylops.dto.aipe.export;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import fi.vm.sade.eperusteet.ylops.dto.KoodiDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.PerusteKeskeinensisaltoalueDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.PerusteTekstiOsaDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.aipe.PerusteKevytTekstiKappaleDto;
import fi.vm.sade.eperusteet.ylops.dto.teksti.LokalisoituTekstiDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PerusteAIPEOppiaineSisaltoDto {
    private Long id;
    private UUID tunniste;
    private LokalisoituTekstiDto nimi;
    private Boolean koosteinen;
    private Boolean abstrakti;
    private KoodiDto koodi;
    private PerusteTekstiOsaDto tehtava;
    private PerusteTekstiOsaDto arviointi;
    private PerusteTekstiOsaDto tyotavat;
    private PerusteTekstiOsaDto ohjaus;
    private PerusteTekstiOsaDto sisaltoalueinfo;
    private LokalisoituTekstiDto pakollinenKurssiKuvaus;
    private LokalisoituTekstiDto syventavaKurssiKuvaus;
    private LokalisoituTekstiDto soveltavaKurssiKuvaus;
    private LokalisoituTekstiDto vapaaTeksti;
    private List<PerusteKevytTekstiKappaleDto> vapaatTekstit = new ArrayList<>();
    private List<PerusteAIPEOpetuksentavoiteSisaltoDto> tavoitteet = new ArrayList<>();
    private List<PerusteKeskeinensisaltoalueDto> sisaltoalueet = new ArrayList<>();
}
