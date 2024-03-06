package fi.vm.sade.eperusteet.ylops.dto.ops;

import fi.vm.sade.eperusteet.ylops.dto.teksti.TekstiosaDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class OppiainePalautettuDto extends OppiaineBaseDto {
    private Long vlkId;
    private TekstiosaDto tehtava;
    private Set<OppiaineDto> oppimaarat;
    private Set<OpetuksenKohdealueDto> kohdealueet;
    private Set<OppiaineenVuosiluokkakokonaisuusDto> vuosiluokkakokonaisuudet;
    private String koodiUri;
    private String koodiArvo;
}
