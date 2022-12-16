package fi.vm.sade.eperusteet.ylops.dto.lops2019;

import fi.vm.sade.eperusteet.ylops.dto.teksti.LokalisoituTekstiDto;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = { "koodiUri" })
@Builder
public class Lops2019OpintojaksonModuuliDto {
    private String koodiUri;
    private LokalisoituTekstiDto kuvaus;
}
