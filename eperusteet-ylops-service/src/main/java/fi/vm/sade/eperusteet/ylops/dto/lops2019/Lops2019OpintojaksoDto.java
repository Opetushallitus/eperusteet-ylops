package fi.vm.sade.eperusteet.ylops.dto.lops2019;

import fi.vm.sade.eperusteet.ylops.dto.teksti.LokalisoituTekstiDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Singular;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Lops2019OpintojaksoDto extends Lops2019OpintojaksoBaseDto {
    private LokalisoituTekstiDto kuvaus;
    private LokalisoituTekstiDto arviointi;
    private LokalisoituTekstiDto opiskeluymparistoTyotavat;

    private Set<Lops2019OpintojaksonOppiaineDto> oppiaineet = new HashSet<>();

    private List<Lops2019OpintojaksonTavoiteDto> tavoitteet = new ArrayList<>();
    private List<Lops2019OpintojaksonKeskeinenSisaltoDto> keskeisetSisallot = new ArrayList<>();

    private List<Lops2019PaikallinenLaajaAlainenDto> laajaAlainenOsaaminen = new ArrayList<>();

    @Singular("moduuli")
    private List<Lops2019OpintojaksonModuuliDto> moduulit = new ArrayList<>();

    private List<Lops2019OpintojaksoDto> paikallisetOpintojaksot = new ArrayList();
}
