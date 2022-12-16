package fi.vm.sade.eperusteet.ylops.service.ops;

import fi.vm.sade.eperusteet.ylops.dto.OpetussuunnitelmaExportDto;
import org.springframework.security.core.parameters.P;
import org.springframework.security.access.prepost.PreAuthorize;

public interface OpsExport extends OpsToteutus {
    @Override
    default Class getImpl() {
        return OpsExport.class;
    }

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    OpetussuunnitelmaExportDto export(@P("opsId") Long opsId);

    default Class<OpetussuunnitelmaExportDto> getExportClass() {
        return OpetussuunnitelmaExportDto.class;
    }
}
