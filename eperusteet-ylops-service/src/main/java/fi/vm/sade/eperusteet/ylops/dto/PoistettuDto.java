package fi.vm.sade.eperusteet.ylops.dto;

import fi.vm.sade.eperusteet.ylops.dto.teksti.LokalisoituTekstiDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PoistettuDto {
    private Long id;
    private LokalisoituTekstiDto nimi;
    private Boolean palautettu;
    private String luoja;
    private Date luotu;
    private String muokkaaja;
    private Date muokattu;
}
