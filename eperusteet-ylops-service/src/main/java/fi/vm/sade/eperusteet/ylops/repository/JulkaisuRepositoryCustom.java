package fi.vm.sade.eperusteet.ylops.repository;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface JulkaisuRepositoryCustom {
    JsonNode querySisalto(Long julkaisu, String query);
}
