package fi.vm.sade.eperusteet.ylops.dto.peruste.lops2019.oppiaineet;

import com.fasterxml.jackson.annotation.JsonInclude;
import fi.vm.sade.eperusteet.ylops.dto.KoodiDto;
import fi.vm.sade.eperusteet.ylops.dto.Reference;
import fi.vm.sade.eperusteet.ylops.dto.ReferenceableDto;
import fi.vm.sade.eperusteet.ylops.dto.teksti.LokalisoituTekstiDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Lops2019OppiaineBaseDto implements ReferenceableDto {
    private Long id;
    private LokalisoituTekstiDto nimi;
    private KoodiDto koodi;
    private Reference oppiaine;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Lops2019ArviointiDto arviointi;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Lops2019TehtavaDto tehtava;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Lops2019OppiaineLaajaAlainenOsaaminenDto laajaAlaisetOsaamiset;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Lops2019OppiaineTavoitteetDto tavoitteet;
}
