package fi.vm.sade.eperusteet.ylops.dto.lops2019;

import fi.vm.sade.eperusteet.ylops.dto.teksti.LokalisoituTekstiDto;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Lops2019PaikallinenOppiaineDto {
    private Long id;
    private String koodi;
    private LokalisoituTekstiDto nimi;
    private LokalisoituTekstiDto kuvaus;
    private Lops2019LaajaAlainenOsaaminenDto laajaAlainenOsaaminen;
    private Lops2019TehtavaDto tehtava;
    private Lops2019ArviointiDto arviointi;
    private Lops2019OppiaineenTavoitteetDto tavoitteet;
    private String perusteenOppiaineUri;
}
