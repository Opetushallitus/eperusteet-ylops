package fi.vm.sade.eperusteet.ylops.service.ops;

import fi.vm.sade.eperusteet.ylops.dto.lops2019.Lops2019OpintojaksoDto;
import fi.vm.sade.eperusteet.ylops.service.util.Validointi;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;

import java.util.List;

public interface ValidointiService {

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    List<Validointi> validoiOpetussuunnitelma(Long id);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    List<Validointi> validoiLukio2019(Long opsId);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    List<Validointi.Virhe> tarkistaOpintojaksot(@P("opsId") Long opsId);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    void tarkistaOpintojakso(@P("opsId") Long opsId, Lops2019OpintojaksoDto opintojaksoDto);
}
