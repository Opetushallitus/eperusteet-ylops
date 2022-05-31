package fi.vm.sade.eperusteet.ylops.service.ops;

import fi.vm.sade.eperusteet.ylops.dto.ops.OpetussuunnitelmaInfoDto;
import java.util.Set;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PreAuthorize;

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
