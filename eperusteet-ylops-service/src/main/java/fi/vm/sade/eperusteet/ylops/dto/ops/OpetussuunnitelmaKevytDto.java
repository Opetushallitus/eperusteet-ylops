package fi.vm.sade.eperusteet.ylops.dto.ops;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class OpetussuunnitelmaKevytDto extends OpetussuunnitelmaBaseDto {
    private OpetussuunnitelmaBaseDto pohja;
    private Set<OpsVuosiluokkakokonaisuusKevytDto> vuosiluokkakokonaisuudet;
    private Set<OpsOppiaineKevytDto> oppiaineet;
    private List<OpetussuunnitelmaNimiDto> periytyvatPohjat = new ArrayList<>();
}
