package fi.vm.sade.eperusteet.ylops.dto.aipe;

import fi.vm.sade.eperusteet.ylops.dto.teksti.LokalisoituTekstiDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AIPEKurssiKevytDto {
    private Long id;
    private Long perusteenKurssiId;
    private LokalisoituTekstiDto nimi;
    private boolean piilotettu;
}
