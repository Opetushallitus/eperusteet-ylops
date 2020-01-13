package fi.vm.sade.eperusteet.ylops.service.ops;

import fi.vm.sade.eperusteet.ylops.dto.ops.OpetussuunnitelmanAikatauluDto;
import java.util.List;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PreAuthorize;

public interface OpetussuunnitelmanAikatauluService {

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    List<OpetussuunnitelmanAikatauluDto> getAll(@P("opsId") Long opsId);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')")
    OpetussuunnitelmanAikatauluDto add(@P("opsId") Long opsId, OpetussuunnitelmanAikatauluDto opetussuunnitelmanAikatauluDto);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')")
    OpetussuunnitelmanAikatauluDto update(@P("opsId") Long opsId, OpetussuunnitelmanAikatauluDto opetussuunnitelmanAikatauluDto);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')")
    void delete(@P("opsId") Long opsId, OpetussuunnitelmanAikatauluDto opetussuunnitelmanAikatauluDto);
}
