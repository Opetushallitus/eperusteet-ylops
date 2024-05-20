package fi.vm.sade.eperusteet.ylops.service.ops;

import fi.vm.sade.eperusteet.ylops.domain.MuokkausTapahtuma;
import fi.vm.sade.eperusteet.ylops.dto.RevisionKayttajaDto;
import fi.vm.sade.eperusteet.ylops.dto.teksti.PoistettuTekstiKappaleDto;
import fi.vm.sade.eperusteet.ylops.dto.teksti.TekstiKappaleDto;
import fi.vm.sade.eperusteet.ylops.dto.teksti.TekstiKappaleViiteDto;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;

import java.util.List;
import java.util.UUID;

public interface TekstiKappaleViiteService {
    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    TekstiKappaleViiteDto.Matala getTekstiKappaleViite(@P("opsId") Long opsId, Long viiteId);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    <T> T getTekstiKappaleViite(@P("opsId") Long opsId, Long viiteId, Class<T> t);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    fi.vm.sade.eperusteet.ylops.service.external.impl.perustedto.TekstiKappaleViiteDto getPerusteTekstikappale(@P("opsId") Long opsId, Long viiteId);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')")
    TekstiKappaleViiteDto.Matala addTekstiKappaleViite(@P("opsId") Long opsId,
                                                       Long viiteId,
                                                       TekstiKappaleViiteDto.Matala viiteDto);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')")
    TekstiKappaleViiteDto.Matala addTekstiKappaleViite(@P("opsId") Long opsId,
                                                       Long viiteId,
                                                       TekstiKappaleViiteDto.Matala viiteDto,
                                                       MuokkausTapahtuma tapahtuma);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')")
    TekstiKappaleViiteDto updateTekstiKappaleViite(@P("opsId") Long opsId, Long rootViiteId, TekstiKappaleViiteDto uusi);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')")
    void reorderSubTree(@P("opsId") Long opsId, Long rootViiteId, TekstiKappaleViiteDto.Puu uusi);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')")
    void removeTekstiKappaleViite(@P("opsId") Long opsId, Long viiteId);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')")
    TekstiKappaleViiteDto.Puu kloonaaTekstiKappale(@P("opsId") Long opsId, Long viiteId);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')")
    <T> T kloonaaTekstiKappale(@P("opsId") Long opsId, Long viiteId, Class<T> t);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    List<RevisionKayttajaDto> getVersions(@P("opsId") Long opsId, long viiteId);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    TekstiKappaleDto findTekstikappaleVersion(@P("opsId") long opsId, long viiteId, long versio);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')")
    void revertToVersion(@P("opsId") Long opsId, Long viiteId, Integer versio);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    List<PoistettuTekstiKappaleDto> getRemovedTekstikappaleetForOps(@P("opsId") Long opsId);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')")
    TekstiKappaleDto returnRemovedTekstikappale(@P("opsId") Long opsId, Long id);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    TekstiKappaleViiteDto.Matala getTekstiKappaleViiteOriginal(@P("opsId") Long opsId, Long viiteId);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    List<TekstiKappaleViiteDto.Matala> getTekstiKappaleViiteOriginals(@P("opsId") Long opsId, Long viiteId);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    Integer alaOpetussuunnitelmaLukumaaraTekstikappaleTunniste(Long opsId, UUID tunniste);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    TekstiKappaleViiteDto.Matala getTekstiKappaleViiteByTunniste(@P("opsId") Long opsId, UUID tunniste);
}
