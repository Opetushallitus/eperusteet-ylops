package fi.vm.sade.eperusteet.ylops.dto.lops2019;

import fi.vm.sade.eperusteet.ylops.dto.teksti.LokalisoituTekstiDto;
import lombok.*;

@Getter
@Setter
@EqualsAndHashCode(of = { "koodiUri" })
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Lops2019OpintojaksonModuuliDto {
    private String koodiUri;
    private LokalisoituTekstiDto kuvaus;
}
