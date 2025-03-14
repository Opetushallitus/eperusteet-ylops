package fi.vm.sade.eperusteet.ylops.dto.ops;

import fi.vm.sade.eperusteet.ylops.dto.ReferenceableDto;
import fi.vm.sade.eperusteet.ylops.dto.teksti.LokalisoituTekstiDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OpetuksenKeskeinensisaltoalueDto implements ReferenceableDto {
    private Long id;
    private KeskeinenSisaltoalueDto sisaltoalueet;
    private LokalisoituTekstiDto omaKuvaus;
}
