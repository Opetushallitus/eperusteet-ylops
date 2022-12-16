package fi.vm.sade.eperusteet.ylops.dto.peruste.lops2019.oppiaineet;

import fi.vm.sade.eperusteet.ylops.dto.teksti.LokalisoituTekstiDto;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Lops2019ArviointiDto {
    private LokalisoituTekstiDto kuvaus;
}
