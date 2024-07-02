package fi.vm.sade.eperusteet.ylops.service.ops;

import fi.vm.sade.eperusteet.ylops.domain.oppiaine.Oppiaineenvuosiluokkakokonaisuus;
import fi.vm.sade.eperusteet.ylops.domain.ops.Opetussuunnitelma;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpsVuosiluokkakokonaisuusDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.VuosiluokkakokonaisuusDto;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;

import java.util.UUID;

public interface VuosiluokkakokonaisuusService {

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')")
    VuosiluokkakokonaisuusDto add(@P("opsId") Long opsId, VuosiluokkakokonaisuusDto dto);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    OpsVuosiluokkakokonaisuusDto get(@P("opsId") Long opsId, Long kokonaisuusId);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    OpsVuosiluokkakokonaisuusDto getPohjanVuosiluokkakokonaisuus(@P("opsId") Long opsId, UUID tunniste);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')")
    OpsVuosiluokkakokonaisuusDto update(@P("opsId") Long opsId, VuosiluokkakokonaisuusDto dto);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')")
    OpsVuosiluokkakokonaisuusDto kopioiMuokattavaksi(@P("opsId") Long opsId, Long kokonaisuusId);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')")
    void delete(@P("opsId") Long opsId, Long kokonaisuusId);

    @PreAuthorize("hasPermission(#opetussuunnitelma.getId(), 'opetussuunnitelma', 'MUOKKAUS') or (#opetussuunnitelma.getPohja() != null and hasPermission(#opetussuunnitelma.getPohja().getId(), 'opetussuunnitelma', 'MUOKKAUS'))")
    void removeSisaltoalueetInKeskeinensisaltoalueet(Opetussuunnitelma opetussuunnitelma, Oppiaineenvuosiluokkakokonaisuus vuosiluokkakokonaisuus, boolean clearSisaltoalueet);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')")
    void piilotaOppiaine(Long opsId, Long oppiaineId, Long vuosiluokkakokonaisuusId, boolean piilota);
}
