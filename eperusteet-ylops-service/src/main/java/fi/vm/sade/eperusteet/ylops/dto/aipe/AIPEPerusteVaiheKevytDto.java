package fi.vm.sade.eperusteet.ylops.dto.aipe;

import fi.vm.sade.eperusteet.ylops.dto.teksti.LokalisoituTekstiDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AIPEPerusteVaiheKevytDto {
    private Long id;
    private LokalisoituTekstiDto nimi;
}
