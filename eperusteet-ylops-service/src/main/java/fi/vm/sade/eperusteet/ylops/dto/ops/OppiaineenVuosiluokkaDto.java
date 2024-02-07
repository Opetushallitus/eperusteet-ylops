package fi.vm.sade.eperusteet.ylops.dto.ops;

import fi.vm.sade.eperusteet.ylops.domain.Vuosiluokka;
import fi.vm.sade.eperusteet.ylops.dto.ReferenceableDto;

import fi.vm.sade.eperusteet.ylops.dto.teksti.LokalisoituTekstiDto;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OppiaineenVuosiluokkaDto implements ReferenceableDto {
    private Long id;
    private Vuosiluokka vuosiluokka;
    private List<KeskeinenSisaltoalueDto> sisaltoalueet;
    private List<OpetuksenTavoiteDto> tavoitteet;
    private LokalisoituTekstiDto vapaaTeksti;
}
