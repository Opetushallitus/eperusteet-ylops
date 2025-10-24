package fi.vm.sade.eperusteet.ylops.service.ops;

import fi.vm.sade.eperusteet.ylops.domain.Vuosiluokka;
import fi.vm.sade.eperusteet.ylops.domain.oppiaine.OppiaineTyyppi;
import fi.vm.sade.eperusteet.ylops.dto.RevisionDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.KopioOppimaaraDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpetuksenTavoiteDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OppiaineDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OppiaineLaajaDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OppiainePalautettuDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OppiaineenVuosiluokkaDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OppiaineenVuosiluokkakokonaisuusDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpsOppiaineDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.PoistettuOppiaineDto;
import fi.vm.sade.eperusteet.ylops.service.locking.LockService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public interface OppiaineService extends LockService<OpsOppiaineCtx> {
    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')")
    void updateVuosiluokkienTavoitteet(@P("opsId") Long opsId, Long oppiaineId, Long vlkId, Map<Vuosiluokka, Set<UUID>> tavoitteet);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    List<OppiaineDto> getAll(@P("opsId") Long opsId);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    List<OppiaineDto> getAll(@P("opsId") Long opsId, boolean valinnaiset);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    List<OppiaineDto> getAll(@P("opsId") Long opsId, OppiaineTyyppi tyyppi);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    OpsOppiaineDto get(@P("opsId") Long opsId, Long id);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    <T> T getPohjanVastaavaOppiaine(@P("opsId") Long opsId, Long id, Class<T> clazz);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    OppiaineDto getParent(@P("opsId") Long opsId, Long id);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')")
    OppiaineDto add(@P("opsId") Long opsId, OppiaineDto oppiaineDto);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')")
    OppiaineDto addCopyOppimaara(@P("opsId") Long opsId, Long oppiaineId, KopioOppimaaraDto oppiaineDto);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')")
    OppiaineLaajaDto add(@P("opsId") Long opsId, OppiaineLaajaDto oppiaineDto);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')")
    OppiaineDto addValinnainen(@P("opsId") Long opsId, OppiaineDto oppiaineDto, Long vlkId,
                               Set<Vuosiluokka> vuosiluokat, List<OpetuksenTavoiteDto> tavoitteetDto, Integer oldJnro,
                               OppiaineenVuosiluokkakokonaisuusDto oaVlk, boolean updateOld);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')")
    OpsOppiaineDto update(@P("opsId") Long opsId, OppiaineDto oppiaineDto);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')")
    OpsOppiaineDto update(Long opsId, Long vuosiluokkakokonaisuusId, OppiaineDto oppiaineDto, boolean valutaAlaOpetussuunnitelmiin);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')")
    OppiaineDto updateValinnainen(@P("opsId") Long opsId, OppiaineDto oppiaineDto, Long vlkId,
                                  Set<Vuosiluokka> vuosiluokat, List<OpetuksenTavoiteDto> tavoitteet);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')")
    OppiaineDto updateYksinkertainen(@P("opsId") Long opsId, OppiaineDto oppiaineDto, Long vlkId,
                                  Set<Vuosiluokka> vuosiluokat, List<OpetuksenTavoiteDto> tavoitteet);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'POISTO')")
    List<OppiaineLaajaDto> getAllVersions(@P("opsId") Long opsId, Long oppiaineId);

    @PreAuthorize("hasPermission(null, 'pohja', 'LUONTI') || hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')")
    OppiainePalautettuDto restore(@P("opsId") Long opsId, Long oppiaineId, Long oppimaaraId);

    @PreAuthorize("hasPermission(null, 'pohja', 'LUONTI') || hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')")
    OppiainePalautettuDto restore(@P("opsId") Long opsId, Long oppiaineId, Long oppimaaraId, Integer versio);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')")
    OpsOppiaineDto kopioiMuokattavaksi(@P("opsId") Long opsId, Long id, boolean asetaPohjanOppiaine);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')")
    PoistettuOppiaineDto delete(@P("opsId") Long opsId, Long id, boolean pakotettuPoistoYlaOpsista);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')")
    PoistettuOppiaineDto delete(@P("opsId") Long opsId, Long id);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')")
    OppiaineenVuosiluokkakokonaisuusDto updateVuosiluokkakokonaisuudenSisalto(@P("opsId") Long opsId, Long id, OppiaineenVuosiluokkakokonaisuusDto dto);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    OppiaineenVuosiluokkaDto getVuosiluokka(@P("opsId") Long opsId, Long oppiaineId, Long vuosiluokkaId);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')")
    OppiaineenVuosiluokkaDto updateVuosiluokanSisalto(@P("opsId") Long opsId, Long id, Long kokonaisuusId, OppiaineenVuosiluokkaDto dto, boolean valutaAlaOpetussuunnitelmiin);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')")
    OppiaineenVuosiluokkaDto updateValinnaisenVuosiluokanSisalto(@P("opsId") Long opsId, Long id,
                                                                 Long oppiaineenVuosiluokkaId,
                                                                 List<OpetuksenTavoiteDto> tavoitteet);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'POISTO')")
    @Deprecated
    OpsOppiaineDto palautaYlempi(@P("opsId") Long opsId, Long id);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    List<RevisionDto> getVersions(@P("opsId") Long opsId, Long id);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    OpsOppiaineDto getVersion(@P("opsId") Long opsId, Long id, Integer versio);

    @PreAuthorize("hasPermission(null, 'pohja', 'LUONTI')")
    OppiaineDto getRevision(Long opsId, Long id, Integer versio);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')")
    OpsOppiaineDto revertTo(@P("opsId") Long opsId, Long id, Integer versio);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    List<PoistettuOppiaineDto> getRemoved(@P("opsId") Long opsId);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    boolean oppimaaraKaytossaKaikissaAlaOpetussuunnitelmissa(@P("opsId") Long opsId, Long oppiaineId);
}
