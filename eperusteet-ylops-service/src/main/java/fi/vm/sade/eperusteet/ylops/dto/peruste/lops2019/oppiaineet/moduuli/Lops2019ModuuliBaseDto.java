package fi.vm.sade.eperusteet.ylops.dto.peruste.lops2019.oppiaineet.moduuli;

import fi.vm.sade.eperusteet.ylops.dto.KoodiDto;
import fi.vm.sade.eperusteet.ylops.dto.ReferenceableDto;
import fi.vm.sade.eperusteet.ylops.dto.teksti.LokalisoituTekstiDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Lops2019ModuuliBaseDto implements ReferenceableDto {
    private Long id;
    private LokalisoituTekstiDto nimi;
    private boolean pakollinen;
    private KoodiDto koodi;
}
