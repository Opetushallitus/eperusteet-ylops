package fi.vm.sade.eperusteet.ylops.dto.lops2019;

import fi.vm.sade.eperusteet.ylops.dto.KoodiDto;
import fi.vm.sade.eperusteet.ylops.dto.Reference;
import fi.vm.sade.eperusteet.ylops.dto.ReferenceableDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.lops2019.oppiaineet.moduuli.Lops2019ModuuliDto;
import fi.vm.sade.eperusteet.ylops.dto.teksti.LokalisoituTekstiDto;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Lops2019OppiaineKevytDto implements ReferenceableDto, Lops2019SortableOppiaineDto {

    private Long id;
    private LokalisoituTekstiDto nimi;
    private KoodiDto koodi;
    private Reference oppiaine;
    private List<Lops2019ModuuliDto> moduulit = new ArrayList<>();
    private List<Lops2019OppiaineKevytDto> oppimaarat = new ArrayList<>();
}
