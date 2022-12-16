package fi.vm.sade.eperusteet.ylops.dto.lops2019;

import fi.vm.sade.eperusteet.ylops.dto.teksti.LokalisoituTekstiDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Lops2019PaikallinenOppiaineKevytDto implements Lops2019SortablePaikallinenOppiaineDto {
    private Long id;
    private String koodi;
    private LokalisoituTekstiDto nimi;
    private String perusteenOppiaineUri;
}
