package fi.vm.sade.eperusteet.ylops.dto.peruste.tpo;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class TpoSisaltoViiteDto {
    private Long id;
    private PerusteenOsaDto.Laaja perusteenOsa;
    private List<TpoSisaltoViiteDto> lapset = new ArrayList<>();

    @JsonIgnore
    public List<TpoPerusteenTaiteenalaDto> getTaiteenalat() {
        return flatten()
                .map(TpoSisaltoViiteDto::getPerusteenOsa)
                .filter(TpoPerusteenTaiteenalaDto.class::isInstance)
                .map(TpoPerusteenTaiteenalaDto.class::cast)
                .collect(Collectors.toList());
    }

    /**
     * Taiteenalat eivät ole tekstikappaleita, joten ne jätetään tekstikappalepuun ulkopuolelle.
     */
    @JsonIgnore
    public TekstiKappaleViiteDto getTekstiKappaleViiteSisalto() {
        return new TekstiKappaleViiteDto(
                id,
                perusteenOsa instanceof TekstiKappaleDto ? (TekstiKappaleDto) perusteenOsa : null,
                lapset.stream()
                        .filter(lapsi -> !(lapsi.getPerusteenOsa() instanceof TpoPerusteenTaiteenalaDto))
                        .map(TpoSisaltoViiteDto::getTekstiKappaleViiteSisalto)
                        .collect(Collectors.toList()));
    }

    private Stream<TpoSisaltoViiteDto> flatten() {
        return Stream.concat(
                Stream.of(this),
                lapset.stream().flatMap(TpoSisaltoViiteDto::flatten));
    }
}
