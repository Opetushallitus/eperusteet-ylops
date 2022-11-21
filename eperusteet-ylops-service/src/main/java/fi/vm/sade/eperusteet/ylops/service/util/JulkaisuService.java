package fi.vm.sade.eperusteet.ylops.service.util;

import com.fasterxml.jackson.databind.JsonNode;
import fi.vm.sade.eperusteet.ylops.domain.ops.JulkaistuOpetussuunnitelmaTila;
import fi.vm.sade.eperusteet.ylops.domain.ops.JulkaisuTila;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpetussuunnitelmanJulkaisuDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.UusiJulkaisuDto;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

public interface JulkaisuService {

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    List<OpetussuunnitelmanJulkaisuDto> getJulkaisut(@P("opsId") Long opsId);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    List<OpetussuunnitelmanJulkaisuDto> getJulkaisutKevyt(@P("opsId") Long opsId);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'TILANVAIHTO')")
    void addJulkaisu(@P("opsId") Long opsId, UusiJulkaisuDto julkaisuDto);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'TILANVAIHTO')")
    void addJulkaisuAsync(@P("opsId") Long opsId, UusiJulkaisuDto julkaisuDto);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'TILANVAIHTO')")
    OpetussuunnitelmanJulkaisuDto aktivoiJulkaisu(@P("opsId") Long opsId, int revision);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    JsonNode queryOpetussuunnitelmaJulkaisu(@P("opsId") Long opsId, String query);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    boolean onkoMuutoksia(long opsId);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    JulkaisuTila viimeisinJulkaisuTila(long opsId);

    @PreAuthorize("isAuthenticated()")
    void saveJulkaistuOpetussuunnitelmaTila(JulkaistuOpetussuunnitelmaTila julkaistuOpetussuunnitelmaTila);

}
