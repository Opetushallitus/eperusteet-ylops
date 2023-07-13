package fi.vm.sade.eperusteet.ylops.service.external;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import fi.vm.sade.eperusteet.ylops.domain.KoulutusTyyppi;
import fi.vm.sade.eperusteet.ylops.dto.PalauteDto;
import fi.vm.sade.eperusteet.ylops.dto.YllapitoDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.TermiDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.PerusteDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.PerusteInfoDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.TiedoteQueryDto;
import fi.vm.sade.eperusteet.ylops.service.exception.NotExistsException;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface EperusteetService {

    @PreAuthorize("permitAll()")
    List<YllapitoDto> getYllapitoAsetukset();

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
    PalauteDto lahetaPalaute(PalauteDto palaute) throws JsonProcessingException;

    @PreAuthorize("permitAll()")
    TermiDto getTermi(final Long perusteId, String avain);

    @PreAuthorize("permitAll()")
    Date viimeisinPerusteenJulkaisuaika(Long perusteId);
}
