package fi.vm.sade.eperusteet.ylops.dto.teksti;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class KommenttiDto {
    private String nimi;
    private String sisalto;
    private String muokkaaja;
    private Date luotu;
    private Date muokattu;
    private Long id;
    private Long ylinId;
    private Long parentId;
    private Long opetussuunnitelmaId;
    private Boolean poistettu;
    private Long tekstiKappaleViiteId;
    private Long oppiaineId;
    private Long vlkId;
    private Long vlId;
}
