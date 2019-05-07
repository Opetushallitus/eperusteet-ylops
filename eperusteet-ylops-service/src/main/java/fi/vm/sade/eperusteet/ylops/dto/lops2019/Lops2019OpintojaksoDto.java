package fi.vm.sade.eperusteet.ylops.dto.lops2019;

import fi.vm.sade.eperusteet.ylops.dto.teksti.LokalisoituTekstiDto;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Lops2019OpintojaksoDto extends Lops2019OpintojaksoBaseDto {
    private LokalisoituTekstiDto kuvaus;
    private LokalisoituTekstiDto tavoitteet;
    private LokalisoituTekstiDto keskeisetSisallot;
    private LokalisoituTekstiDto laajaAlainenOsaaminen;

    private Set<Lops2019OpintojaksonOppiaineDto> oppiaineet = new HashSet<>();

    @Singular("moduuli")
    private Set<Lops2019OpintojaksonModuuliDto> moduulit;
}
