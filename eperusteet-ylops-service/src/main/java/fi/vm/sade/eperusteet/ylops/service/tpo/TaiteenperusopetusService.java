package fi.vm.sade.eperusteet.ylops.service.tpo;

import fi.vm.sade.eperusteet.ylops.dto.peruste.tpo.TpoPerusteenTaiteenalaDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.tpo.TpoPerusteenTaiteenalaVaiheDto;
import fi.vm.sade.eperusteet.ylops.dto.tpo.TaiteenOpintokokonaisuusDto;
import fi.vm.sade.eperusteet.ylops.dto.tpo.TaiteenalaDto;
import fi.vm.sade.eperusteet.ylops.dto.tpo.TaiteenalaKevytDto;
import fi.vm.sade.eperusteet.ylops.dto.tpo.TaiteenalanVaiheDto;
import fi.vm.sade.eperusteet.ylops.dto.tpo.export.TpoSisaltoExportDto;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;

import java.util.List;

public interface TaiteenperusopetusService {

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    List<TpoPerusteenTaiteenalaDto> getPerusteTaiteenalat(@P("opsId") Long opsId);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    TpoPerusteenTaiteenalaDto getPerusteTaiteenala(@P("opsId") Long opsId, Long perusteenTaiteenalaId);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    TpoPerusteenTaiteenalaVaiheDto getPerusteVaihe(@P("opsId") Long opsId, Long perusteenVaiheId);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    List<TaiteenalaKevytDto> getTaiteenalat(@P("opsId") Long opsId);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')")
    List<TaiteenalaKevytDto> addTaiteenalat(@P("opsId") Long opsId, List<Long> perusteenTaiteenalaIdt);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    TaiteenalaDto getTaiteenala(@P("opsId") Long opsId, Long taiteenalaId);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')")
    TaiteenalaDto updateTaiteenala(@P("opsId") Long opsId, Long taiteenalaId, TaiteenalaDto dto);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')")
    void removeTaiteenala(@P("opsId") Long opsId, Long taiteenalaId);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    TaiteenalanVaiheDto getVaihe(@P("opsId") Long opsId, Long vaiheId);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')")
    TaiteenalanVaiheDto updateVaihe(@P("opsId") Long opsId, Long vaiheId, TaiteenalanVaiheDto dto);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')")
    TaiteenOpintokokonaisuusDto addOpintokokonaisuus(@P("opsId") Long opsId, Long vaiheId, TaiteenOpintokokonaisuusDto dto);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    TaiteenOpintokokonaisuusDto getOpintokokonaisuus(@P("opsId") Long opsId, Long opintokokonaisuusId);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')")
    TaiteenOpintokokonaisuusDto updateOpintokokonaisuus(@P("opsId") Long opsId, Long opintokokonaisuusId, TaiteenOpintokokonaisuusDto dto);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')")
    void removeOpintokokonaisuus(@P("opsId") Long opsId, Long opintokokonaisuusId);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    TpoSisaltoExportDto getExportSisalto(@P("opsId") Long opsId);
}
