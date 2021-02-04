package fi.vm.sade.eperusteet.ylops.dto.lops2019;

import fi.vm.sade.eperusteet.ylops.dto.ops.OrganisaationKoodi;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Lops2019OpintojaksoListaDto {
    private List<OrganisaationKoodi.Opintojakso> opintojaksot;
}
