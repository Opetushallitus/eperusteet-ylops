package fi.vm.sade.eperusteet.ylops.service.mocks;

import com.fasterxml.jackson.databind.JsonNode;
import fi.vm.sade.eperusteet.ylops.dto.koodisto.OrganisaatioLaajaDto;
import fi.vm.sade.eperusteet.ylops.dto.koodisto.OrganisaatioQueryDto;
import fi.vm.sade.eperusteet.ylops.service.external.OrganisaatioService;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

@Service
public class OrganisaatioServiceMock implements OrganisaatioService {
    @Override
    public JsonNode getOrganisaatio(String organisaatioOid) {
        return null;
    }

    @Override
    public JsonNode getOrganisaatioVirkailijat(Set<String> organisaatioOids) {
        return null;
    }

    @Override
    public JsonNode getPeruskoulutByKuntaId(String kuntaId) {
        return null;
    }

    @Override
    public JsonNode getLukiotByKuntaId(String kuntaId) {
        return null;
    }

    @Override
    public JsonNode getPeruskoulutByOid(String oid) {
        return null;
    }

    @Override
    public JsonNode getPeruskoulutoimijat(List<String> kuntaIdt) {
        return null;
    }

    @Override
    public JsonNode getLukioByOid(String oid) {
        return null;
    }

    @Override
    public JsonNode getLukiotoimijat(List<String> kuntaIdt) {
        return null;
    }

    @Override
    public List<OrganisaatioLaajaDto> getKoulutustoimijat(OrganisaatioQueryDto query) {
        return new ArrayList<>();
    }

    @Override
    public List<JsonNode> getRyhmat() {
        return new ArrayList<>();
    }

    @Override
    public <T> T getOrganisaatio(String oid, Class<T> clz) {
        return null;
    }
}
