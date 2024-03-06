package fi.vm.sade.eperusteet.ylops.service.external;

import com.fasterxml.jackson.databind.JsonNode;
import fi.vm.sade.eperusteet.ylops.dto.koodisto.OrganisaatioLaajaDto;
import fi.vm.sade.eperusteet.ylops.dto.koodisto.OrganisaatioQueryDto;
import java.util.List;
import java.util.Set;

import org.springframework.security.access.prepost.PreAuthorize;

public interface OrganisaatioService {

    @PreAuthorize("isAuthenticated()")
    <T> T getOrganisaatio(String oid, Class<T> clz);

    @PreAuthorize("permitAll()")
    JsonNode getOrganisaatio(String organisaatioOid);

    @PreAuthorize("isAuthenticated()")
    JsonNode getOrganisaatioVirkailijat(Set<String> organisaatioOids);

    @PreAuthorize("isAuthenticated()")
    JsonNode getPeruskoulutByKuntaId(String kuntaId);

    @PreAuthorize("isAuthenticated()")
    List<JsonNode> getRyhmat();

    @PreAuthorize("isAuthenticated()")
    JsonNode getLukiotByKuntaId(String kuntaId);

    @PreAuthorize("isAuthenticated()")
    JsonNode getPeruskoulutByOid(String oid);

    @PreAuthorize("isAuthenticated()")
    JsonNode getPeruskoulutoimijat(List<String> kuntaIdt);

    @PreAuthorize("isAuthenticated()")
    JsonNode getLukioByOid(String oid);

    @PreAuthorize("isAuthenticated()")
    JsonNode getLukiotoimijat(List<String> kuntaIdt);

    @PreAuthorize("isAuthenticated()")
    List<OrganisaatioLaajaDto> getKoulutustoimijat(OrganisaatioQueryDto query);
}
