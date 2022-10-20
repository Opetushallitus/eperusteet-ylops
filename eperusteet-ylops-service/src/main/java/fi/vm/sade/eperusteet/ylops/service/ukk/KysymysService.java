package fi.vm.sade.eperusteet.ylops.service.ukk;

import fi.vm.sade.eperusteet.ylops.dto.ukk.KysymysDto;
import org.springframework.security.core.parameters.P;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;


public interface KysymysService {

    @PreAuthorize("isAuthenticated()")
    List<KysymysDto> getKysymykset();

    @PreAuthorize("hasPermission(#dto, 'kysymys', 'LUONTI')")
    KysymysDto createKysymys(@P("dto") KysymysDto dto);

    @PreAuthorize("hasPermission(#dto.id, 'kysymys', 'MUOKKAUS')")
    KysymysDto updateKysymys(@P("dto") KysymysDto dto);

    @PreAuthorize("hasPermission(#id, 'kysymys', 'POISTO')")
    void deleteKysymys(@P("id") Long id);

}
