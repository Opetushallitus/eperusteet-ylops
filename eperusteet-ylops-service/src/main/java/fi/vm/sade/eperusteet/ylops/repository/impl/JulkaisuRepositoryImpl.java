package fi.vm.sade.eperusteet.ylops.repository.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import fi.vm.sade.eperusteet.ylops.repository.JulkaisuRepositoryCustom;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import java.io.IOException;

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
