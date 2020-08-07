package fi.vm.sade.eperusteet.ylops.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OppiaineOpintojaksoDto {
    Long id;
    List<OppiaineOpintojaksoDto> lapset;
}
