package fi.vm.sade.eperusteet.ylops.dto.peruste;

import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import fi.vm.sade.eperusteet.ylops.domain.Vuosiluokka;
import fi.vm.sade.eperusteet.ylops.dto.ReferenceableDto;
import fi.vm.sade.eperusteet.ylops.dto.teksti.LokalisoituTekstiDto;
import fi.vm.sade.eperusteet.ylops.service.external.impl.perustedto.TekstiKappaleDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PerusteOppiaineenVuosiluokkakokonaisuusDto implements ReferenceableDto {

    private Long id;
    @JsonIdentityReference(alwaysAsId = true)
    @JsonProperty("_vuosiluokkakokonaisuus")
    private PerusteVuosiluokkakokonaisuusDto vuosiluokkaKokonaisuus;
    private PerusteTekstiOsaDto tehtava;
    private PerusteTekstiOsaDto tyotavat;
    private PerusteTekstiOsaDto ohjaus;
    private PerusteTekstiOsaDto arviointi;
    private PerusteTekstiOsaDto sisaltoalueinfo;
    private LokalisoituTekstiDto opetuksenTavoitteetOtsikko;
    private LokalisoituTekstiDto vapaaTeksti;
    private List<TekstiKappaleDto> vapaatTekstit;
    private List<PerusteKeskeinensisaltoalueDto> sisaltoalueet;
    private List<PerusteOpetuksentavoiteDto> tavoitteet;

    public Set<Vuosiluokka> getVuosiluokat() {
        return vuosiluokkaKokonaisuus.getVuosiluokat();
    }

    @JsonIgnore
    public Optional<PerusteOpetuksentavoiteDto> getTavoite(UUID tunniste) {
        return tavoitteet.stream()
                .filter(t -> t.getTunniste().equals(tunniste))
                .findAny();
    }
}
