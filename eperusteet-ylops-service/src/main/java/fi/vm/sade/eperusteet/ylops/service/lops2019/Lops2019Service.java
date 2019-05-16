package fi.vm.sade.eperusteet.ylops.service.lops2019;

import fi.vm.sade.eperusteet.ylops.dto.lops2019.*;
import fi.vm.sade.eperusteet.ylops.dto.lops2019.Validointi.Lops2019ValidointiDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.PerusteInfoDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.PerusteTekstiKappaleViiteDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.PerusteTekstiKappaleViiteMatalaDto;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.Set;

public interface Lops2019Service {
    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    List<Lops2019OpintojaksoDto> getOpintojaksot(@P("opsId") Long opsId);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    List<Lops2019OppiaineDto> getPerusteOppiaineet(@P("opsId") Long opsId);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    PerusteInfoDto getPeruste(@P("opsId") Long opsId);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    Lops2019SisaltoDto getSisalto(@P("opsId") Long opsId);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    Lops2019OppiaineDto getPerusteOppiaine(@P("opsId") Long opsId, Long oppiaineId);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    Lops2019ModuuliDto getPerusteModuuli(@P("opsId") Long opsId, Long oppiaineId, Long moduuliId);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    Lops2019ModuuliDto getPerusteModuuli(@P("opsId") Long opsId, String koodiUri);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    Lops2019OppiaineDto getPerusteOppiaine(@P("opsId") Long opsId, String koodiUri);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    List<Lops2019ModuuliDto> getOppiaineenModuulit(@P("opsId") Long opsId, String oppiaineUri);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    PerusteTekstiKappaleViiteDto getPerusteTekstikappaleet(@P("opsId") Long opsId);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    PerusteTekstiKappaleViiteMatalaDto getPerusteTekstikappale(@P("opsId") Long opsId, Long tekstikappaleId);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    Lops2019ValidointiDto getValidointi(Long opsId);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    Set<Lops2019ModuuliDto> getPerusteModuulit(Long opsId, Set<String> koodiUrit);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    Set<Lops2019OppiaineDto> getPerusteenOppiaineet(Long opsId, Set<String> koodiUrit);

}
