package fi.vm.sade.eperusteet.ylops.service.external.impl.perustedto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OppiaineDto {

    private Long id;
    private UUID tunniste;
    private Boolean koosteinen;
    private Boolean abstrakti;
    private PerusteenLokalisoituTekstiDto nimi;
    private TekstiOsaDto tehtava;
    private List<TekstiKappaleDto> vapaatTekstit;
    private Set<OppiaineDto> oppimaarat;
    private Set<OpetuksenKohdealueDto> kohdealueet;
    private Set<OppiaineenVuosiluokkaKokonaisuusDto> vuosiluokkakokonaisuudet;
    private String koodiUri;
    private String koodiArvo;
}
