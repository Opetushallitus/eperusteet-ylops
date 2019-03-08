package fi.vm.sade.eperusteet.ylops.dto.lops2019;

import fi.vm.sade.eperusteet.ylops.dto.peruste.PerusteTekstiKappaleViiteDto;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Lops2019SisaltoDto {
    private PerusteTekstiKappaleViiteDto sisalto;
    private Lops2019LaajaAlainenOsaaminenDto laajaAlainenOsaaminen;
    private List<Lops2019OppiaineDto> oppiaineet = new ArrayList<>();
}
