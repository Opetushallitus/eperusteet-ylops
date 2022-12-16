package fi.vm.sade.eperusteet.ylops.dto;

import fi.vm.sade.eperusteet.ylops.dto.teksti.LokalisoituTekstiDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

/**
 * Created by autio on 24.2.2016.
 */
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
