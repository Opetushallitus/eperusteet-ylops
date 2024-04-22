package fi.vm.sade.eperusteet.ylops.dto.peruste;

import fi.vm.sade.eperusteet.ylops.service.external.impl.perustedto.TekstiKappaleViiteDto;
import fi.vm.sade.eperusteet.ylops.service.util.Nulls;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PerusopetuksenPerusteenSisaltoDto {

    private Set<PerusteLaajaalainenosaaminenDto> laajaalaisetosaamiset;
    private Set<PerusteVuosiluokkakokonaisuusDto> vuosiluokkakokonaisuudet;
    private Set<PerusteOppiaineDto> oppiaineet;
    private TekstiKappaleViiteDto sisalto;

    public Optional<PerusteOppiaineDto> getOppiaine(UUID tunniste) {
        return oppiaineet.stream()
                .flatMap(oa -> Stream.concat(Stream.of(oa), Nulls.nullToEmpty(oa.getOppimaarat()).stream()))
                .filter(oa -> Objects.equals(oa.getTunniste(), tunniste))
                .findAny();

    }

    public Optional<PerusteVuosiluokkakokonaisuusDto> getVuosiluokkakokonaisuudet(UUID tunniste) {
        return vuosiluokkakokonaisuudet.stream()
                .flatMap(Stream::of)
                .filter(vlk -> Objects.equals(vlk.getTunniste(), tunniste))
                .findAny();

    }
}
