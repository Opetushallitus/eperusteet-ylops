package fi.vm.sade.eperusteet.ylops.service.lops2019;

import fi.vm.sade.eperusteet.ylops.domain.MuokkausTapahtuma;
import fi.vm.sade.eperusteet.ylops.domain.lops2019.Lops2019Oppiaine;
import fi.vm.sade.eperusteet.ylops.domain.ops.Opetussuunnitelma;
import fi.vm.sade.eperusteet.ylops.dto.PoistettuDto;
import fi.vm.sade.eperusteet.ylops.dto.RevisionDto;
import fi.vm.sade.eperusteet.ylops.dto.lops2019.Lops2019OppiaineJarjestysDto;
import fi.vm.sade.eperusteet.ylops.dto.lops2019.Lops2019PaikallinenOppiaineDto;
import fi.vm.sade.eperusteet.ylops.service.util.UpdateWrapperDto;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

public interface Lops2019OppiaineService {
    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    <T> List<T> getAll(@P("opsId") Long opsId, Class<T> clz);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    List<Lops2019Oppiaine> getTuodut(Opetussuunnitelma opetussuunnitelma);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    Lops2019PaikallinenOppiaineDto getOne(@P("opsId") Long opsId, Long oppiaineId);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    Lops2019PaikallinenOppiaineDto getTuotu(@P("opsId") Long opsId, Long oppiaineId);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')")
    Lops2019PaikallinenOppiaineDto addOppiaine(@P("opsId") Long opsId, Lops2019PaikallinenOppiaineDto oppiaineDto);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')")
    Lops2019PaikallinenOppiaineDto addOppiaine(@P("opsId") Long opsId,
                                               Lops2019PaikallinenOppiaineDto oppiaineDto,
                                               MuokkausTapahtuma tapahtuma);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')")
    Lops2019PaikallinenOppiaineDto updateOppiaine(@P("opsId") Long opsId,
                                                  Long oppiaineId,
                                                  UpdateWrapperDto<Lops2019PaikallinenOppiaineDto> oppiaineDto);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')")
    Lops2019PaikallinenOppiaineDto updateOppiaine(@P("opsId") Long opsId,
                                                  Long oppiaineId,
                                                  UpdateWrapperDto<Lops2019PaikallinenOppiaineDto> oppiaineDto,
                                                  MuokkausTapahtuma tapahtuma);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    List<Lops2019OppiaineJarjestysDto> getOppiaineJarjestys(@P("opsId") Long opsId);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')")
    void removeOne(@P("opsId") Long opsId, Long oppiaineId);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    List<RevisionDto> getVersions(@P("opsId") Long opsId, Long oppiaineId);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')")
    List<PoistettuDto> getRemoved(@P("opsId") Long opsId);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')")
    Lops2019PaikallinenOppiaineDto getVersion(@P("opsId") Long opsId, Long oppiaineId, Integer versio);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')")
    Lops2019PaikallinenOppiaineDto revertTo(@P("opsId") Long opsId, Long oppiaineId, Integer versio);
}
