package fi.vm.sade.eperusteet.ylops.dto.peruste.aipe;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import fi.vm.sade.eperusteet.ylops.dto.peruste.PerusteOpetuksenkohdealueDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.PerusteTekstiOsaDto;
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
public class PerusteAIPEVaiheDto {
    private Long id;
    private UUID tunniste;
    private LokalisoituTekstiDto nimi;
    private PerusteTekstiOsaDto siirtymaEdellisesta;
    private PerusteTekstiOsaDto tehtava;
    private PerusteTekstiOsaDto siirtymaSeuraavaan;
    private PerusteTekstiOsaDto paikallisestiPaatettavatAsiat;
    private List<PerusteOpetuksenkohdealueDto> opetuksenKohdealueet = new ArrayList<>();
    private List<PerusteAIPEOppiaineDto> oppiaineet = new ArrayList<>();
    private List<PerusteKevytTekstiKappaleDto> vapaatTekstit = new ArrayList<>();
}
