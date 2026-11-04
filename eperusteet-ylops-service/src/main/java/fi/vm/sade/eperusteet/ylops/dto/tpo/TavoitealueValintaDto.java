package fi.vm.sade.eperusteet.ylops.dto.tpo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TavoitealueValintaDto {
    private Long tavoitealueId;
    private String koodiUri;
}
