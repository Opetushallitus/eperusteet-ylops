package fi.vm.sade.eperusteet.ylops.dto.peruste.lops2019.oppiaineet;

import com.fasterxml.jackson.annotation.JsonInclude;
import fi.vm.sade.eperusteet.ylops.dto.peruste.lops2019.oppiaineet.moduuli.Lops2019ModuuliBaseDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Lops2019OppiaineDto extends Lops2019OppiaineBaseDto {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<Lops2019ModuuliBaseDto> moduulit = new ArrayList<>();

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<Lops2019OppiaineDto> oppimaarat = new ArrayList<>();
}
