package fi.vm.sade.eperusteet.ylops.service.ops;

import fi.vm.sade.eperusteet.ylops.dto.OpetussuunnitelmaExportDto;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;

public interface OpsExport extends OpsToteutus {
    @Override
    default Class getImpl() {
        return OpsExport.class;
    }

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    default OpetussuunnitelmaExportDto export(@P("opsId") Long opsId) {
        return export(opsId, OpetussuunnitelmaExportDto.class);
    }

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    <T> T export(@P("opsId") Long opsId, Class<T> t);

    default Class<OpetussuunnitelmaExportDto> getExportClass() {
        return OpetussuunnitelmaExportDto.class;
    }
}
