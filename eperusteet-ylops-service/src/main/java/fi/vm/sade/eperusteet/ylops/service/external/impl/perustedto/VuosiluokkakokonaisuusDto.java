package fi.vm.sade.eperusteet.ylops.service.external.impl.perustedto;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import fi.vm.sade.eperusteet.ylops.domain.Vuosiluokka;
import fi.vm.sade.eperusteet.ylops.dto.ReferenceableDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class VuosiluokkakokonaisuusDto implements ReferenceableDto {
    private Long id;
    private UUID tunniste;
    private Set<Vuosiluokka> vuosiluokat;
    private PerusteenLokalisoituTekstiDto nimi;
    private TekstiOsaDto siirtymaEdellisesta;
    private TekstiOsaDto tehtava;
    private TekstiOsaDto siirtymaSeuraavaan;
    private TekstiOsaDto laajaalainenOsaaminen;
    private Set<VuosiluokkakokonaisuudenLaajaalainenOsaaminenDto> laajaalaisetOsaamiset;
    private TekstiOsaDto paikallisestiPaatettavatAsiat;
    private List<TekstiKappaleDto> vapaatTekstit;
}
