package fi.vm.sade.eperusteet.ylops.service.lops2019;

import fi.vm.sade.eperusteet.ylops.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.ylops.dto.lops2019.Lops2019LaajaAlainenOsaaminenDto;
import fi.vm.sade.eperusteet.ylops.dto.lops2019.Lops2019OpintojaksoDto;
import fi.vm.sade.eperusteet.ylops.dto.lops2019.Lops2019PoistettuDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.PerusteInfoDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.PerusteTekstiKappaleViiteDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.PerusteTekstiKappaleViiteMatalaDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.lops2019.oppiaineet.Lops2019OppiaineKaikkiDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.lops2019.Lops2019SisaltoDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.lops2019.oppiaineet.moduuli.Lops2019ModuuliDto;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface Lops2019Service {
    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    List<Lops2019OpintojaksoDto> getOpintojaksot(@P("opsId") Long opsId);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    List<Lops2019OppiaineKaikkiDto> getPerusteOppiaineet(@P("opsId") Long opsId);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    PerusteInfoDto getPeruste(@P("opsId") Long opsId);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    Lops2019SisaltoDto getPerusteSisalto(@P("opsId") Long opsId);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    List<Lops2019OppiaineKaikkiDto> getPerusteOppiaineetAndOppimaarat(@P("opsId") Long opsId);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    List<Lops2019ModuuliDto> getPerusteModuulit(@P("opsId") Long opsId);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    Lops2019OppiaineKaikkiDto getPerusteOppiaine(@P("opsId") Long opsId, Long oppiaineId);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    Lops2019ModuuliDto getPerusteModuuli(@P("opsId") Long opsId, Long oppiaineId, Long moduuliId);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    Lops2019ModuuliDto getPerusteModuuli(@P("opsId") Long opsId, String koodiUri);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    Lops2019OppiaineKaikkiDto getPerusteOppiaine(@P("opsId") Long opsId, String koodiUri);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    List<Lops2019ModuuliDto> getPerusteOppiaineenModuulit(@P("opsId") Long opsId, String oppiaineUri);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    PerusteTekstiKappaleViiteDto getPerusteTekstikappaleet(@P("opsId") Long opsId);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    PerusteTekstiKappaleViiteMatalaDto getPerusteTekstikappale(@P("opsId") Long opsId, Long tekstikappaleId);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    Set<Lops2019ModuuliDto> getPerusteModuulit(Long opsId, Set<String> koodiUrit);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    Set<Lops2019OppiaineKaikkiDto> getPerusteenOppiaineet(Long opsId, Set<String> koodiUrit);

    Map<String, List<Lops2019OpintojaksoDto>> getModuuliToOpintojaksoMap(List<Lops2019OpintojaksoDto> opintojaksot);

    @PreAuthorize("permitAll()")
    Lops2019LaajaAlainenOsaaminenDto getLaajaAlaisetOsaamiset(Kieli kieli);

    // Hierarkiasta tuotuja opintojaksoja varten
    @PreAuthorize("permitAll()")
    Set<Lops2019ModuuliDto> getPerusteModuulitImpl(Long opsId, Set<String> koodiUrit);
}
