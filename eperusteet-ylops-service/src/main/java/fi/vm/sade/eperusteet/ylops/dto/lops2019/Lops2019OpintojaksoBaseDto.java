package fi.vm.sade.eperusteet.ylops.dto.lops2019;

import fi.vm.sade.eperusteet.ylops.dto.teksti.LokalisoituTekstiDto;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Lops2019OpintojaksoBaseDto {
    private boolean tuotu = false;
    private Long id;
    private String koodi;
    private Long laajuus;
    private LokalisoituTekstiDto nimi;
}
