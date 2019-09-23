package fi.vm.sade.eperusteet.ylops.dto;

import fi.vm.sade.eperusteet.ylops.dto.teksti.LokalisoituTekstiDto;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * Created by autio on 24.2.2016.
 */
@Getter
@Setter
public class PoistettuDto {
    private Long id;
    private LokalisoituTekstiDto nimi;
    private Boolean palautettu;
    private String luoja;
    private Date luotu;
    private String muokkaaja;
    private Date muokattu;
}
