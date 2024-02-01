package fi.vm.sade.eperusteet.ylops.dto.peruste;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import fi.vm.sade.eperusteet.ylops.domain.Vuosiluokka;
import fi.vm.sade.eperusteet.ylops.dto.ReferenceableDto;
import fi.vm.sade.eperusteet.ylops.dto.teksti.LokalisoituTekstiDto;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import fi.vm.sade.eperusteet.ylops.service.external.impl.perustedto.TekstiKappaleDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "tunniste")
public class PerusteVuosiluokkakokonaisuusDto implements ReferenceableDto {
    private Long id;
    private UUID tunniste;
    private Set<Vuosiluokka> vuosiluokat;
    private LokalisoituTekstiDto nimi;
    private PerusteTekstiOsaDto siirtymaEdellisesta;
    private PerusteTekstiOsaDto tehtava;
    private PerusteTekstiOsaDto siirtymaSeuraavaan;
    private PerusteTekstiOsaDto laajaalainenOsaaminen;
    @JsonProperty("laajaalaisetosaamiset")
    private Set<PerusteVuosiluokkakokonaisuudenLaajaalainenosaaminenDto> laajaalaisetOsaamiset;
    private PerusteTekstiOsaDto paikallisestiPaatettavatAsiat;
    private List<TekstiKappaleDto> vapaatTekstit;
}
