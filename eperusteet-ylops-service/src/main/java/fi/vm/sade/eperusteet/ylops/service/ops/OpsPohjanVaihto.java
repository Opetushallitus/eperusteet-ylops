package fi.vm.sade.eperusteet.ylops.service.ops;

import fi.vm.sade.eperusteet.ylops.dto.navigation.NavigationNodeDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpetussuunnitelmaInfoDto;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.Set;

public interface OpsPohjanVaihto extends OpsToteutus {
    @Override
    default Class getImpl() {
        return OpsPohjanVaihto.class;
    }

    @PreAuthorize("hasPermission(null, 'pohja', 'LUONTI')")
    void vaihdaPohja(@P("opsId") Long opsId, Long pohjaId);

    @PreAuthorize("hasPermission(null, 'pohja', 'LUONTI')")
    Set<OpetussuunnitelmaInfoDto> haeVaihtoehdot(@P("opsId") Long opsId);
}
