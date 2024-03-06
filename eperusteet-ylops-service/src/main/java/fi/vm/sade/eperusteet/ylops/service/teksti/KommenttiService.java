package fi.vm.sade.eperusteet.ylops.service.teksti;

import fi.vm.sade.eperusteet.ylops.dto.teksti.KommenttiDto;
import org.springframework.security.core.parameters.P;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

public interface KommenttiService {
    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    public List<KommenttiDto> getAllByTekstiKappaleViite(@P("opsId") Long opsId, Long tekstiKappaleViiteId);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    public List<KommenttiDto> getAllByOppiaine(@P("opsId") Long opsId, Long vlkId, Long oppiaineId);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    public List<KommenttiDto> getAllByVuosiluokka(@P("opsId") Long opsId, Long vlkId, Long oppiaineId, Long vlId);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    public List<KommenttiDto> getAllByOpetussuunnitelma(@P("opsId") Long opsId);

    @PreAuthorize("isAuthenticated()")
    public List<KommenttiDto> getAllByParent(Long id);

    @PreAuthorize("isAuthenticated()")
    public List<KommenttiDto> getAllByYlin(Long id);

    @PreAuthorize("isAuthenticated()")
    public KommenttiDto get(Long kommenttiId);

    @PreAuthorize("hasPermission(#k.opetussuunnitelmaId, 'opetussuunnitelma', 'LUKU')")
    public KommenttiDto add(@P("k") final KommenttiDto kommenttiDto);

    @PreAuthorize("isAuthenticated()")
    public KommenttiDto update(Long kommenttiId, final KommenttiDto kommenttiDto);

    @PreAuthorize("isAuthenticated()")
    public void delete(Long kommenttiId);

    @PreAuthorize("isAuthenticated()")
    public void deleteReally(Long kommenttiId);
}
