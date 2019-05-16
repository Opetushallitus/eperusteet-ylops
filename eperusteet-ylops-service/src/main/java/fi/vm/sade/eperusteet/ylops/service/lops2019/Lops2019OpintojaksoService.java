package fi.vm.sade.eperusteet.ylops.service.lops2019;

import fi.vm.sade.eperusteet.ylops.dto.PoistettuDto;
import fi.vm.sade.eperusteet.ylops.dto.RevisionDto;
import fi.vm.sade.eperusteet.ylops.dto.lops2019.Lops2019OpintojaksoDto;
import fi.vm.sade.eperusteet.ylops.dto.lops2019.Lops2019OpintojaksoPerusteDto;
import fi.vm.sade.eperusteet.ylops.service.util.UpdateWrapperDto;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

public interface Lops2019OpintojaksoService {
    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    List<Lops2019OpintojaksoDto> getAll(@P("opsId") Long opsId);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    Lops2019OpintojaksoDto getOne(@P("opsId") Long opsId, Long opintojaksoId);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')")
    Lops2019OpintojaksoDto addOpintojakso(@P("opsId") Long opsId, Lops2019OpintojaksoDto opintojaksoDto);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')")
    Lops2019OpintojaksoDto updateOpintojakso(@P("opsId") Long opsId, Long opintojaksoId, UpdateWrapperDto<Lops2019OpintojaksoDto> opintojaksoDto);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')")
    void removeOne(@P("opsId") Long opsId, Long opintojaksoId);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')")
    List<RevisionDto> getVersions(@P("opsId") Long opsId, Long opintojaksoId);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')")
    List<PoistettuDto> getRemoved(@P("opsId") Long opsId);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')")
    Lops2019OpintojaksoDto getVersion(@P("opsId") Long opsId, Long opintojaksoId, Integer versio);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')")
    Lops2019OpintojaksoDto revertTo(@P("opsId") Long opsId, Long opintojaksoId, Integer versio);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    Lops2019OpintojaksoPerusteDto getOpintojaksonPeruste(Long opsId, Long opintojaksoId);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    long getOpintojaksonLaajuus(Long opsId, Lops2019OpintojaksoDto opintojakso);
}
