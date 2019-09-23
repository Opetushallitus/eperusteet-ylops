package fi.vm.sade.eperusteet.ylops.dto.kayttaja;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EtusivuDto {
    private Long opetussuunnitelmatJulkaistut;
    private Long opetussuunnitelmatKeskeneraiset;
    private Long pohjatJulkaistut;
    private Long pohjatKeskeneraiset;
}
