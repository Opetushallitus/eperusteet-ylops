package fi.vm.sade.eperusteet.ylops.service.tpo;

import fi.vm.sade.eperusteet.ylops.dto.peruste.TPOOpetuksenSisaltoDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.tpo.PerusteTaiteenosaDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.tpo.TpoPerusteenTaiteenalaDto;
import fi.vm.sade.eperusteet.ylops.dto.tpo.TaiteenalaDto;
import fi.vm.sade.eperusteet.ylops.dto.tpo.TaiteenosaDto;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;

import java.util.List;

public interface TaiteenperusopetusService {

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    List<TaiteenalaDto> getTaiteenalat(@P("opsId") Long opsId);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    TaiteenalaDto getTaiteenala(@P("opsId") Long opsId, Long taiteenalaId);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    TaiteenosaDto getTaiteenosa(@P("opsId") Long opsId, Long taiteenosaId);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    TPOOpetuksenSisaltoDto getPerusteSisalto(@P("opsId") Long opsId);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    TpoPerusteenTaiteenalaDto getPerusteenTaiteenala(@P("opsId") Long opsId, String koodiUri);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    PerusteTaiteenosaDto getPerusteenTaiteenosa(@P("opsId") Long opsId, Long perusteenTaiteenosanId);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')")
    TaiteenalaDto addTaiteenala(@P("opsId") Long opsId, TaiteenalaDto taiteenalaDto);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')")
    TaiteenalaDto updateTaiteenala(@P("opsId") Long opsId, Long taiteenalaId, TaiteenalaDto taiteenalaDto);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')")
    TaiteenosaDto updateTaiteenosa(@P("opsId") Long opsId, Long taiteenosaId, TaiteenosaDto taiteenosaDto);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')")
    void removeTaiteenala(@P("opsId") Long opsId, Long taiteenalaId);
}
