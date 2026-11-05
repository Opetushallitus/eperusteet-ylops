package fi.vm.sade.eperusteet.ylops.dto.peruste.tpo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import fi.vm.sade.eperusteet.ylops.service.external.impl.perustedto.PerusteenOsaDto;
import fi.vm.sade.eperusteet.ylops.service.external.impl.perustedto.TekstiKappaleDto;
import fi.vm.sade.eperusteet.ylops.service.external.impl.perustedto.TekstiKappaleViiteDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TpoSisaltoViiteDto {
    private Long id;
    private PerusteenOsaDto.Laaja perusteenOsa;
    private List<TpoSisaltoViiteDto> lapset = new ArrayList<>();

    @JsonIgnore
    public List<TpoPerusteenTaiteenalaDto> getTaiteenalat() {
        return flatten()
                .filter(viite -> viite.getPerusteenOsa() instanceof TpoPerusteenTaiteenalaDto)
                .map(viite -> {
                    TpoPerusteenTaiteenalaDto taiteenala = (TpoPerusteenTaiteenalaDto) viite.getPerusteenOsa();
                    taiteenala.setVaiheet(viite.getLapset().stream()
                            .map(TpoSisaltoViiteDto::getPerusteenOsa)
                            .filter(TpoPerusteenTaiteenalaVaiheDto.class::isInstance)
                            .map(TpoPerusteenTaiteenalaVaiheDto.class::cast)
                            .collect(Collectors.toList()));
                    return taiteenala;
                })
                .collect(Collectors.toList());
    }

    /**
     * Taiteenalat vaiheineen eivät ole tekstikappaleita, joten ne jätetään tekstikappalepuun ulkopuolelle.
     */
    @JsonIgnore
    public TekstiKappaleViiteDto getTekstiKappaleViiteSisalto() {
        List<TekstiKappaleViiteDto> tekstiLapset = lapset.stream()
                .filter(lapsi -> !(lapsi.getPerusteenOsa() instanceof TpoPerusteenTaiteenalaDto)
                        && !(lapsi.getPerusteenOsa() instanceof TpoPerusteenTaiteenalaVaiheDto))
                .map(lapsi -> lapsi.getTekstiKappaleViiteSisalto())
                .collect(Collectors.toList());
        TekstiKappaleDto tekstiKappale = perusteenOsa instanceof TekstiKappaleDto ? (TekstiKappaleDto) perusteenOsa : null;
        return new TekstiKappaleViiteDto(id, tekstiKappale, tekstiLapset);
    }

    private Stream<TpoSisaltoViiteDto> flatten() {
        return Stream.concat(Stream.of(this), lapset.stream().flatMap(TpoSisaltoViiteDto::flatten));
    }
}
