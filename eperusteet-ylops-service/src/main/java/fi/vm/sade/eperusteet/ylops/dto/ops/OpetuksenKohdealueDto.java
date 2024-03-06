package fi.vm.sade.eperusteet.ylops.dto.ops;

import fi.vm.sade.eperusteet.ylops.dto.ReferenceableDto;
import fi.vm.sade.eperusteet.ylops.dto.teksti.LokalisoituTekstiDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OpetuksenKohdealueDto implements ReferenceableDto {
    private Long id;
    private LokalisoituTekstiDto nimi;

    public OpetuksenKohdealueDto(LokalisoituTekstiDto nimi) {
        this.nimi = nimi;
    }
}
