package fi.vm.sade.eperusteet.ylops.service.lops2019;

import fi.vm.sade.eperusteet.ylops.domain.MuokkausTapahtuma;
import fi.vm.sade.eperusteet.ylops.dto.RevisionDto;
import fi.vm.sade.eperusteet.ylops.dto.lops2019.Lops2019OpintojaksoDto;
import fi.vm.sade.eperusteet.ylops.dto.lops2019.Lops2019OpintojaksoPerusteDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpetussuunnitelmaDto;
import fi.vm.sade.eperusteet.ylops.service.util.UpdateWrapperDto;
import java.util.List;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PreAuthorize;

public interface Lops2019OpintojaksoService {
    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    <T extends Lops2019OpintojaksoDto> List<T> getAll(@P("opsId") Long opsId, Class<T> clz);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    <T extends Lops2019OpintojaksoDto> List<T> getTuodut(@P("opsId") Long opsId, Class<T> clz);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    <T extends Lops2019OpintojaksoDto> List<T> getAllTuodut(@P("opsId") Long opsId, Class<T> clz);

    List<Lops2019OpintojaksoDto> getAll(@P("opsId") Long opsId);

    List<Lops2019OpintojaksoDto> getTuodut(@P("opsId") Long opsId);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    Lops2019OpintojaksoDto getOne(@P("opsId") Long opsId, Long opintojaksoId);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    Lops2019OpintojaksoDto getTuotu(@P("opsId") Long opsId, Long opintojaksoId);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')")
    Lops2019OpintojaksoDto addOpintojakso(@P("opsId") Long opsId, Lops2019OpintojaksoDto opintojaksoDto);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')")
    Lops2019OpintojaksoDto addOpintojakso(@P("opsId") Long opsId,
                                          Lops2019OpintojaksoDto opintojaksoDto,
                                          MuokkausTapahtuma tapahtuma);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')")
    Lops2019OpintojaksoDto updateOpintojakso(@P("opsId") Long opsId,
                                             Long opintojaksoId,
                                             UpdateWrapperDto<Lops2019OpintojaksoDto> opintojaksoDto);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')")
    Lops2019OpintojaksoDto updateOpintojakso(@P("opsId") Long opsId,
                                             Long opintojaksoId,
                                             UpdateWrapperDto<Lops2019OpintojaksoDto> opintojaksoDto,
                                             MuokkausTapahtuma tapahtuma);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')")
    void removeOne(@P("opsId") Long opsId, Long opintojaksoId);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    List<RevisionDto> getVersions(@P("opsId") Long opsId, Long opintojaksoId);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')")
    Lops2019OpintojaksoDto getVersion(@P("opsId") Long opsId, Long opintojaksoId, Integer versio);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')")
    Lops2019OpintojaksoDto revertTo(@P("opsId") Long opsId, Long opintojaksoId, Integer versio);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    Lops2019OpintojaksoPerusteDto getOpintojaksonPeruste(@P("opsId") Long opsId, Long opintojaksoId);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    long getOpintojaksonLaajuus(@P("opsId") Long opsId, Lops2019OpintojaksoDto opintojakso);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')")
    boolean tarkistaOpintojaksot(@P("opsId") Long opsId);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    OpetussuunnitelmaDto getOpintojaksonOpetussuunnitelma(@P("opsId") Long opsId, Long opintojaksoId);
}
