package fi.vm.sade.eperusteet.ylops.repository.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import fi.vm.sade.eperusteet.ylops.repository.JulkaisuRepositoryCustom;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceException;
import java.io.IOException;

@Profile("!test")
@Repository
public class JulkaisuRepositoryImpl implements JulkaisuRepositoryCustom {

    @PersistenceContext
    private EntityManager em;


    @Override
    public JsonNode querySisalto(Long julkaisu, String query) {
        try {
            String value = (String) em.createNativeQuery(
                    "SELECT CAST((data.opsdata #> CAST(:query AS text[])) AS text) " +
                            "FROM opetussuunnitelman_julkaisu julkaisu " +
                            "INNER JOIN opetussuunnitelman_julkaisu_data data ON julkaisu.data_id = data.id " +
                            "WHERE julkaisu.id = :id LIMIT 1")
                    .setParameter("id", julkaisu)
                    .setParameter("query", query)
                    .getSingleResult();

            ObjectMapper objectMapper = new ObjectMapper();
            try {
                return objectMapper.readTree(value);
            } catch (IOException | NullPointerException ignored) {}
        }
        catch (PersistenceException ignored) {}
        return JsonNodeFactory.instance.nullNode();
    }
}
