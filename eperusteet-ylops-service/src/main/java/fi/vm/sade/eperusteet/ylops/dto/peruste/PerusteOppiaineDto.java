package fi.vm.sade.eperusteet.ylops.dto.peruste;

import fi.vm.sade.eperusteet.ylops.dto.Reference;
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
public class PerusteOppiaineDto implements ReferenceableDto {

    private Long id;
    private UUID tunniste;
    private String koodiUri;
    private String koodiArvo;
    private Boolean koosteinen;
    private Boolean abstrakti;
    private LokalisoituTekstiDto nimi;
    private PerusteTekstiOsaDto tehtava;
    private List<TekstiKappaleDto> vapaatTekstit;
    private Set<PerusteOppiaineDto> oppimaarat;
    private Set<PerusteOpetuksenkohdealueDto> kohdealueet;
    private List<PerusteOppiaineenVuosiluokkakokonaisuusDto> vuosiluokkakokonaisuudet;

    public Optional<PerusteOppiaineenVuosiluokkakokonaisuusDto> getVuosiluokkakokonaisuus(Reference tunniste) {
        return getVuosiluokkakokonaisuus(UUID.fromString(tunniste.toString()));
    }

    public Optional<PerusteOppiaineenVuosiluokkakokonaisuusDto> getVuosiluokkakokonaisuus(UUID tunniste) {
        return vuosiluokkakokonaisuudet.stream()
                .filter(v -> v.getVuosiluokkaKokonaisuus().getTunniste().equals(tunniste))
                .findAny();
    }
}
