package fi.vm.sade.eperusteet.ylops.dto.lops2019;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class Lops2019OppiaineDto extends Lops2019OppiaineBaseDto {
    private List<Lops2019OppiaineDto> oppimaarat = new ArrayList<>();
    private List<Lops2019ModuuliDto> moduulit = new ArrayList<>();
}
