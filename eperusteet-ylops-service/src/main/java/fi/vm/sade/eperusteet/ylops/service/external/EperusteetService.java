package fi.vm.sade.eperusteet.ylops.service.external;

import com.fasterxml.jackson.databind.JsonNode;
import fi.vm.sade.eperusteet.ylops.domain.KoulutusTyyppi;
import fi.vm.sade.eperusteet.ylops.dto.ops.TermiDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.PerusteDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.PerusteInfoDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.TiedoteQueryDto;
import fi.vm.sade.eperusteet.ylops.service.exception.NotExistsException;
import fi.vm.sade.eperusteet.ylops.service.external.impl.perustedto.EperusteetPerusteDto;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface EperusteetService {
    @PreAuthorize("permitAll()")
    PerusteDto getPeruste(String diaariNumero) throws NotExistsException;

    @PreAuthorize("permitAll()")
    PerusteDto getPerusteUpdateCache(String diaarinumero) throws NotExistsException;

    @PreAuthorize("permitAll()")
    List<PerusteInfoDto> findPerusteet();

    @PreAuthorize("permitAll()")
    List<PerusteInfoDto> findPerusteet(boolean forceRefresh);

    @PreAuthorize("permitAll()")
    List<PerusteInfoDto> findPerusteet(Set<KoulutusTyyppi> tyypit);

    @PreAuthorize("permitAll()")
    List<PerusteInfoDto> findPerusopetuksenPerusteet();

    @PreAuthorize("permitAll()")
    List<PerusteInfoDto> findLukiokoulutusPerusteet();

    @PreAuthorize("permitAll()")
    PerusteDto getPerusteById(final Long id);

    @PreAuthorize("permitAll()")
    JsonNode getTiedotteet(Long jalkeen);

    @PreAuthorize("permitAll()")
    JsonNode getTiedotteetHaku(TiedoteQueryDto queryDto);

    @PreAuthorize("permitAll()")
    byte[] getLiite(final Long perusteId, final UUID id);

    @PreAuthorize("permitAll()")
    TermiDto getTermi(final Long perusteId, String avain);

    @PreAuthorize("permitAll()")
    Date viimeisinPerusteenJulkaisuaika(Long perusteId);

    @PreAuthorize("permitAll()")
    JsonNode getPerusteenJulkaisuByGlobalversionMuutosaika(Long perusteId, Date globalVersionMuutosaika);

    EperusteetPerusteDto getPerusteDtoByRevision(Long perusteId, Integer revision);

    @PreAuthorize("permitAll()")
    PerusteDto getPerusteenJulkaisuByGlobalversionMuutosaikaAsDto(Long perusteId, Date globalVersionMuutosaika);

    @PreAuthorize("permitAll()")
    JsonNode getPerusteByRevision(Long perusteId, Integer revision);
}
