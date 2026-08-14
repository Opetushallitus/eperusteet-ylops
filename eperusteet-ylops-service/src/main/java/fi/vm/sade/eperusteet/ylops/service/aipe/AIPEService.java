package fi.vm.sade.eperusteet.ylops.service.aipe;

import fi.vm.sade.eperusteet.ylops.dto.aipe.AIPEKurssiDto;
import fi.vm.sade.eperusteet.ylops.dto.aipe.AIPEOppiaineDto;
import fi.vm.sade.eperusteet.ylops.dto.aipe.AIPEPerusteVaiheKevytDto;
import fi.vm.sade.eperusteet.ylops.dto.aipe.AIPESisaltoDto;
import fi.vm.sade.eperusteet.ylops.dto.aipe.export.AIPESisaltoExportDto;
import fi.vm.sade.eperusteet.ylops.dto.aipe.AIPEVaiheDto;
import fi.vm.sade.eperusteet.ylops.dto.aipe.AIPEVaiheKevytDto;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;

import java.util.List;

public interface AIPEService {

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    List<AIPEPerusteVaiheKevytDto> getPerusteVaiheet(@P("opsId") Long opsId);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    AIPESisaltoDto getSisalto(@P("opsId") Long opsId);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    AIPESisaltoExportDto getExportSisalto(@P("opsId") Long opsId);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    List<AIPEVaiheKevytDto> getVaiheet(@P("opsId") Long opsId);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')")
    AIPEVaiheDto addVaihe(@P("opsId") Long opsId, Long perusteenVaiheId);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    AIPEVaiheDto getVaihe(@P("opsId") Long opsId, Long vaiheId);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')")
    AIPEVaiheDto updateVaihe(@P("opsId") Long opsId, Long vaiheId, AIPEVaiheDto dto);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')")
    void removeVaihe(@P("opsId") Long opsId, Long vaiheId);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    AIPEOppiaineDto getOppiaine(@P("opsId") Long opsId, Long oppiaineId);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')")
    AIPEOppiaineDto updateOppiaine(@P("opsId") Long opsId, Long oppiaineId, AIPEOppiaineDto dto);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    AIPEKurssiDto getKurssi(@P("opsId") Long opsId, Long kurssiId);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')")
    AIPEKurssiDto updateKurssi(@P("opsId") Long opsId, Long kurssiId, AIPEKurssiDto dto);
}
