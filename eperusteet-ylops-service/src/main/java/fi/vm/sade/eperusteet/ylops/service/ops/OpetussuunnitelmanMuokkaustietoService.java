package fi.vm.sade.eperusteet.ylops.service.ops;

import fi.vm.sade.eperusteet.ylops.domain.HistoriaTapahtuma;
import fi.vm.sade.eperusteet.ylops.domain.MuokkausTapahtuma;
import fi.vm.sade.eperusteet.ylops.domain.ops.OpetussuunnitelmanMuokkaustietoLisaparametrit;
import fi.vm.sade.eperusteet.ylops.dto.navigation.NavigationType;
import fi.vm.sade.eperusteet.ylops.dto.ops.MuokkaustietoKayttajallaDto;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;

import java.util.Date;
import java.util.List;
import java.util.Set;

public interface OpetussuunnitelmanMuokkaustietoService {

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    List<MuokkaustietoKayttajallaDto> getOpsMuokkausTietos(@P("opsId") Long opsId, Date viimeisinLuontiaika, int lukumaara);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS') or hasPermission(null, 'pohja', 'LUONTI')")
    void addOpsMuokkausTieto(@P("opsId") Long opsId, HistoriaTapahtuma historiaTapahtuma, MuokkausTapahtuma muokkausTapahtuma);

    @PreAuthorize("isAuthenticated()")
    void addOpsMuokkausTietoPermissionSkip(@P("opsId") Long opsId, HistoriaTapahtuma historiaTapahtuma, MuokkausTapahtuma muokkausTapahtuma);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS') or hasPermission(null, 'pohja', 'LUONTI')")
    void addOpsMuokkausTieto(@P("opsId") Long opsId, HistoriaTapahtuma historiaTapahtuma, MuokkausTapahtuma muokkausTapahtuma, String lisatieto);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')")
    void addOpsMuokkausTieto(@P("opsId") Long opsId, HistoriaTapahtuma historiaTapahtuma, MuokkausTapahtuma muokkausTapahtuma, NavigationType navigationType);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')")
    void addOpsMuokkausTieto(@P("opsId") Long opsId, HistoriaTapahtuma historiaTapahtuma, MuokkausTapahtuma muokkausTapahtuma, NavigationType navigationType, String lisatieto);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')")
    void addOpsMuokkausTieto(@P("opsId") Long opsId, HistoriaTapahtuma historiaTapahtuma, MuokkausTapahtuma muokkausTapahtuma, NavigationType navigationType, String lisatieto, Set<OpetussuunnitelmanMuokkaustietoLisaparametrit> lisaparametrit);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    MuokkaustietoKayttajallaDto getViimeisinPohjatekstiSync(Long opsId);
}
