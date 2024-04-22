package fi.vm.sade.eperusteet.ylops.dto.ops;

import fi.vm.sade.eperusteet.ylops.dto.teksti.LokalisoituTekstiDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TermiDto {
    private Long id;
    private String avain;
    private LokalisoituTekstiDto termi;
    private LokalisoituTekstiDto selitys;
    private boolean alaviite;
}
