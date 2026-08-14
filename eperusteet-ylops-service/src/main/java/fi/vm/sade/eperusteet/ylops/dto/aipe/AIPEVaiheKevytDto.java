package fi.vm.sade.eperusteet.ylops.dto.aipe;

import fi.vm.sade.eperusteet.ylops.dto.teksti.LokalisoituTekstiDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AIPEVaiheKevytDto {
    private Long id;
    private Long perusteenVaiheId;
    private LokalisoituTekstiDto nimi;
    private boolean piilotettu;
    private List<AIPEOppiaineKevytDto> oppiaineet = new ArrayList<>();
}
