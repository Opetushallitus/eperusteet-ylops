package fi.vm.sade.eperusteet.ylops.dto.ops;

import fi.vm.sade.eperusteet.ylops.dto.Reference;
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
public class OpetussuunnitelmaLuontiDto extends OpetussuunnitelmaBaseDto {
    private Reference pohja;
    private TekstiKappaleViiteDto.Puu tekstit;
    private Set<OpsVuosiluokkakokonaisuusDto> vuosiluokkakokonaisuudet;
    private Set<OpsOppiaineDto> oppiaineet;
    private Luontityyppi luontityyppi = Luontityyppi.LEGACY;

    public enum Luontityyppi {
        KOPIO,
        VIITTEILLA,
        LEGACY
    }
}
