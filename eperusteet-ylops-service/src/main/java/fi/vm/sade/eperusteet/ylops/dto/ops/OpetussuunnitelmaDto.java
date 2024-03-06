package fi.vm.sade.eperusteet.ylops.dto.ops;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import fi.vm.sade.eperusteet.ylops.dto.teksti.TekstiKappaleViiteDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class OpetussuunnitelmaDto extends OpetussuunnitelmaBaseDto {
    private OpetussuunnitelmaNimiDto pohja;
    private TekstiKappaleViiteDto.Puu tekstit;
    private Set<OpsVuosiluokkakokonaisuusDto> vuosiluokkakokonaisuudet;
    private Set<OpsOppiaineDto> oppiaineet;
}
