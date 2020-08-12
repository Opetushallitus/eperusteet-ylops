package fi.vm.sade.eperusteet.ylops.dto.peruste.lops2019.oppiaineet;

import fi.vm.sade.eperusteet.ylops.dto.KoodiDto;
import fi.vm.sade.eperusteet.ylops.dto.Reference;
import fi.vm.sade.eperusteet.ylops.dto.ReferenceableDto;
import fi.vm.sade.eperusteet.ylops.dto.lops2019.Lops2019SortableOppiaineDto;
import fi.vm.sade.eperusteet.ylops.dto.teksti.LokalisoituTekstiDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Lops2019OppiaineBaseDto implements ReferenceableDto, Lops2019SortableOppiaineDto {
    private Long id;
    private LokalisoituTekstiDto nimi;
    private KoodiDto koodi;
    private Reference oppiaine;
    private Lops2019ArviointiDto arviointi;
    private Lops2019TehtavaDto tehtava;
    private Lops2019OppiaineLaajaAlainenOsaaminenDto laajaAlaisetOsaamiset;
    private Lops2019OppiaineTavoitteetDto tavoitteet;
}
