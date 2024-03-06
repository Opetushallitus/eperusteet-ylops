package fi.vm.sade.eperusteet.ylops.dto.lops2019;

import fi.vm.sade.eperusteet.ylops.domain.lops2019.PoistetunTyyppi;
import fi.vm.sade.eperusteet.ylops.dto.Reference;
import fi.vm.sade.eperusteet.ylops.dto.teksti.LokalisoituTekstiDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Lops2019PoistettuDto {
    private Long id;
    private Reference opetussuunnitelma;
    private Long poistettuId;
    private PoistetunTyyppi tyyppi;
    private LokalisoituTekstiDto nimi;
    private LokalisoituTekstiDto parent;
    private String luoja;
    private Date luotu;
    private String muokkaaja;
    private Date muokattu;
}
