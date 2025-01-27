package fi.vm.sade.eperusteet.ylops.service.external.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import fi.vm.sade.eperusteet.utils.client.RestClientFactory;
import fi.vm.sade.eperusteet.ylops.dto.VirkailijaCriteriaDto;
import fi.vm.sade.eperusteet.ylops.dto.koodisto.OrganisaatioLaajaDto;
import fi.vm.sade.eperusteet.ylops.dto.koodisto.OrganisaatioQueryDto;
import fi.vm.sade.eperusteet.ylops.service.exception.BusinessRuleViolationException;
import fi.vm.sade.eperusteet.ylops.service.external.OrganisaatioService;
import fi.vm.sade.eperusteet.ylops.service.mapping.DtoMapper;
import fi.vm.sade.javautils.http.OphHttpClient;
import fi.vm.sade.javautils.http.OphHttpEntity;
import fi.vm.sade.javautils.http.OphHttpRequest;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static fi.vm.sade.eperusteet.ylops.service.security.PermissionEvaluator.RolePermission.ADMIN;
import static fi.vm.sade.eperusteet.ylops.service.security.PermissionEvaluator.RolePermission.CRUD;
import static fi.vm.sade.eperusteet.ylops.service.security.PermissionEvaluator.RolePermission.READ;
import static fi.vm.sade.eperusteet.ylops.service.security.PermissionEvaluator.RolePermission.READ_UPDATE;
import static jakarta.servlet.http.HttpServletResponse.SC_OK;

@Slf4j
@Service
public class OrganisaatioServiceImpl implements OrganisaatioService {

    private static final String VIRKAILIJA_HAKU = "/virkailija/haku";
    private static final String ORGANISAATIOT = "/rest/organisaatio/";
    private static final String HIERARKIA_HAKU = "v2/hierarkia/hae?";
    private static final String HAKU = "v2/hae?";
    private static final String KUNTA_KRITEERI = "kunta=";
    private static final String STATUS_KRITEERI = "&aktiiviset=true&suunnitellut=true&lakkautetut=false";
    private static final String ORGANISAATIO_KRITEERI = "oidRestrictionList=";
    private static final String KOULUTUSTOIMIJAT_KRITEERI = "&organisaatiotyyppi=Koulutustoimija";

    @Value("${cas.key}")
    private String casKey;

    @Value("${cas.service:''}")
    private String casService;

    @Value("${cas.sendRenew:false}")
    private boolean casSendRenew;

    @Value("${cas.login:''}")
    private String casLogin;

    @Value("${host.alb:''}")
    private String hostAlb;

    @Value("${web.url.cas:''}")
    private String webUrlCas;

    @Autowired
    private Client client;

    @Autowired
    private DtoMapper dtoMapper;

    @Component
    public static class Client {
        @Autowired
        RestClientFactory restClientFactory;

        @Autowired
        private DtoMapper dtoMapper;

        @Value("${cas.service.organisaatio-service:''}")
        private String serviceUrl;

        @Value("#{'${fi.vm.sade.eperusteet.ylops.organisaatio-service.peruskoulu-oppilaitostyypit}'.split(',')}")
        private List<String> oppilaitostyypit;

        @Value("#{'${fi.vm.sade.eperusteet.ylops.organisaatio-service.lukio-oppilaitostyypit}'.split(',')}")
        private List<String> lukioOppilaitostyypit;

        @Value("${cas.service.kayttooikeus-service:''}")
        private String koServiceUrl;

        private static final Logger LOG = LoggerFactory.getLogger(Client.class);

        private final ObjectMapper mapper = new ObjectMapper();

        private String peruskouluHakuehto;
        private String lukioHakuehto;

        @PostConstruct
        public void init() {
            peruskouluHakuehto = "&organisaatiotyyppi=Oppilaitos";
            lukioHakuehto = "&organisaatiotyyppi=Oppilaitos";
        }

        @Cacheable("organisaatiot")
        public JsonNode getOrganisaatio(String organisaatioOid) {
            OphHttpClient client = restClientFactory.get(serviceUrl, false);
            String url = serviceUrl + ORGANISAATIOT + organisaatioOid;

            OphHttpRequest request = OphHttpRequest.Builder
                    .get(url)
                    .build();

            return client.<JsonNode>execute(request)
                    .expectedStatus(SC_OK)
                    .mapWith(text -> {
                        try {
                            return mapper.readTree(text);
                        } catch (IOException ex) {
                            throw new BusinessRuleViolationException("Organisaation tietojen hakeminen epäonnistui", ex);
                        }
                    })
                    .orElse(null);
        }

        public <T> T getOrganisaatio(String oid, Class<T> clz) {
            JsonNode org = getOrganisaatio(oid);
            if (org == null) {
                return null;
            }

            try {
                return mapper.treeToValue(org, clz);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                return null;
            }
        }

        private ArrayNode flattenTree(JsonNode tree, String childTreeName, Predicate<JsonNode> filter) {
            ArrayNode array = JsonNodeFactory.instance.arrayNode();
            if (tree != null) {
                tree.forEach(node -> {
                    if (filter.test(node)) {
                        array.add(node);
                    }
                    JsonNode childTree = node.get(childTreeName);
                    array.addAll(flattenTree(childTree, childTreeName, filter));
                });
            }
            return array;
        }

        private JsonNode getPeruskoulut(String hakuehto) {
            return getLaitoksetByEhtoAndTyypit(hakuehto + peruskouluHakuehto, oppilaitostyypit);
        }

        private JsonNode getLukiot(String hakuehto) {
            return getLaitoksetByEhtoAndTyypit(hakuehto + lukioHakuehto, lukioOppilaitostyypit);
        }

        private JsonNode get(String hakuehto) {
            OphHttpClient client = restClientFactory.get(serviceUrl, false);
            String url = serviceUrl + ORGANISAATIOT + hakuehto + STATUS_KRITEERI;

            OphHttpRequest request = OphHttpRequest.Builder
                    .get(url)
                    .build();

            return client.<JsonNode>execute(request)
                    .expectedStatus(SC_OK)
                    .mapWith(text -> {
                        try {
                            JsonNode tree = mapper.readTree(text);
                            return tree.get("organisaatiot");
                        } catch (IOException ex) {
                            throw new BusinessRuleViolationException("Organisaation tietojen hakeminen epäonnistui", ex);
                        }
                    })
                    .orElse(null);
        }

        private JsonNode getLaitoksetByEhtoAndTyypit(String hakuehto, Collection<String> tyypit) {
            final String haku = HIERARKIA_HAKU + hakuehto;
            JsonNode organisaatioTree = get(haku);
            return flattenTree(organisaatioTree, "children",
                    node -> node.get("oppilaitostyyppi") != null &&
                            tyypit.stream()
                                    .map(t -> "oppilaitostyyppi_" + t + "#1")
                                    .anyMatch(s -> s.equals(node.get("oppilaitostyyppi").asText())));
        }

        public JsonNode getOrganisaatioVirkailijat(Set<String> organisaatioOids) {
            OphHttpClient client = restClientFactory.get(koServiceUrl, true);
            String url = koServiceUrl + VIRKAILIJA_HAKU;

            VirkailijaCriteriaDto criteriaDto = new VirkailijaCriteriaDto();

            criteriaDto.setDuplikaatti(false);
            criteriaDto.setPassivoitu(false);

            // Rajattu organisaatioon
            criteriaDto.setOrganisaatioOids(organisaatioOids);

            Map<String, Collection<String>> kayttooikeudet = new HashMap<>();
            ArrayList<String> oikeudet = new ArrayList<>();
            oikeudet.add(CRUD.toString());
            oikeudet.add(READ_UPDATE.toString());
            oikeudet.add(READ.toString());
            oikeudet.add(ADMIN.toString());
            kayttooikeudet.put("EPERUSTEET_YLOPS", oikeudet);
            criteriaDto.setKayttooikeudet(kayttooikeudet);

            try {
                String jsonContent = mapper.writeValueAsString(criteriaDto);
                OphHttpEntity entity = new OphHttpEntity.Builder()
                        .content(jsonContent)
                        .contentType("application/json", "UTF-8")
                        .build();

                OphHttpRequest request = OphHttpRequest.Builder
                        .post(url)
                        .setEntity(entity)
                        .build();

                return client.<JsonNode>execute(request)
                        .expectedStatus(SC_OK)
                        .mapWith(text -> {
                            try {
                                return mapper.readTree(text);
                            } catch (IOException ex) {
                                throw new BusinessRuleViolationException("Organisaation kuuluvien henkilöiden hakeminen epäonnistui", ex);
                            }
                        })
                        .orElse(null);
            } catch (JsonProcessingException e) {
                return null;
            }
        }

        public List<OrganisaatioLaajaDto> getKoulutustoimijat(String kunta) {
            final String haku = HIERARKIA_HAKU + KUNTA_KRITEERI + kunta;
            JsonNode resultJson = get(haku);
            List<OrganisaatioLaajaDto> result = new ArrayList<>();
            for (JsonNode orgJson : resultJson) {
                try {
                    result.add(mapper.treeToValue(orgJson, OrganisaatioLaajaDto.class));
                } catch (JsonProcessingException ex) {
                }
            }
            return result;
        }

        public JsonNode getPeruskoulutByKuntaId(String kuntaId) {
            return getPeruskoulut(KUNTA_KRITEERI + kuntaId);
        }

        public JsonNode getLukiotByKuntaId(String kuntaId) {
            return getLukiot(KUNTA_KRITEERI + kuntaId);
        }

        @Cacheable("organisaation-peruskoulut")
        public JsonNode getPeruskoulutByOid(String oid) {
            return getPeruskoulut(ORGANISAATIO_KRITEERI + oid);
        }

        @Cacheable("organisaation-lukiot")
        public JsonNode getLukiotByOid(String oid) {
            return getLukiot(ORGANISAATIO_KRITEERI + oid);
        }

        public List<JsonNode> getRyhmat() {
            OphHttpClient client = restClientFactory.get(serviceUrl, false);
            String url = serviceUrl + ORGANISAATIOT + "v3/ryhmat";

            OphHttpRequest request = OphHttpRequest.Builder
                    .get(url)
                    .build();

            List<JsonNode> result = new ArrayList<>();
            client.<JsonNode>execute(request)
                    .expectedStatus(SC_OK)
                    .mapWith(text -> {
                        try {
                            return mapper.readTree(text);
                        } catch (IOException ex) {
                            throw new BusinessRuleViolationException("Työryhmätietojen hakeminen epäonnistui", ex);
                        }
                    }).ifPresent(tree -> {
                        for (JsonNode ryhma : tree) {
                            for (JsonNode tyyppi : ryhma.get("ryhmatyypit")) {
                                if ("ryhmatyypit_5#1".equals(tyyppi.asText())) {
                                    result.add(ryhma);
                                    break;
                                }
                            }
                        }
                    });

            return result;
        }
    }

    @Override
    public <T> T getOrganisaatio(String oid, Class<T> clz) {
        return client.getOrganisaatio(oid, clz);
    }

    @Override
    public JsonNode getOrganisaatio(String organisaatioOid) {
        return client.getOrganisaatio(organisaatioOid);
    }

    @Override
    public JsonNode getOrganisaatioVirkailijat(Set<String> organisaatioOids) {
        return client.getOrganisaatioVirkailijat(organisaatioOids);
    }

    @Override
    public JsonNode getPeruskoulutByKuntaId(String kuntaId) {
        return client.getPeruskoulutByKuntaId(kuntaId);
    }

    @Override
    public JsonNode getLukiotByKuntaId(String kuntaId) {
        return client.getLukiotByKuntaId(kuntaId);
    }

    @Override
    public JsonNode getPeruskoulutByOid(String oid) {
        return client.getPeruskoulutByOid(oid);
    }

    @Override
    public JsonNode getPeruskoulutoimijat(List<String> kuntaIdt) {
        return getToimijatByKuntas(kuntaIdt, this::getPeruskoulutByKuntaId);
    }

    @Override
    public List<JsonNode> getRyhmat() {
        return client.getRyhmat();
    }

    private JsonNode getToimijatByKuntas(List<String> kuntaIdt, Function<String, JsonNode> getByKuntaId) {
        Set<String> toimijaOidit =
                kuntaIdt.stream()
                        .flatMap(kuntaId ->
                                StreamSupport.stream(getByKuntaId.apply(kuntaId).spliterator(), false)
                                        .map(koulu -> koulu.get("parentOid").asText()))
                        .collect(Collectors.toCollection(LinkedHashSet::new));

        ArrayNode toimijat = JsonNodeFactory.instance.arrayNode();
        toimijaOidit.stream().map(this::getOrganisaatio).forEach(toimijat::add);
        return toimijat;
    }

    @Override
    public List<OrganisaatioLaajaDto> getKoulutustoimijat(OrganisaatioQueryDto query) {
        Set<String> sallitutOppilaitostyypit = query.getOppilaitostyyppi().stream()
                .map(id -> "oppilaitostyyppi_" + id.toString() + "#1")
                .collect(Collectors.toSet());
        List<OrganisaatioLaajaDto> result = query.getKunta().stream()
                .map(kunta -> client.getKoulutustoimijat(kunta))
                .map(orgs -> orgs.stream())
                .flatMap(x -> x)
                .filter(toimija -> toimija.getOrganisaatiotyypit() != null && toimija.getChildren() != null)
                .filter(toimija -> toimija.getOrganisaatiotyypit().contains("KOULUTUSTOIMIJA"))
                .map(toimija -> {
                    toimija.setChildren(toimija.getChildren().stream()
                            .filter(alitoimija -> sallitutOppilaitostyypit.contains(alitoimija.getOppilaitostyyppi()))
                            .collect(Collectors.toList()));
                    return toimija;
                })
                .filter(toimija -> !toimija.getChildren().isEmpty())
                .collect(Collectors.toList());
        return result;
    }

    @Override
    public JsonNode getLukioByOid(String oid) {
        return client.getLukiotByOid(oid);
    }

    @Override
    public JsonNode getLukiotoimijat(List<String> kuntaIdt) {
        return getToimijatByKuntas(kuntaIdt, this::getLukiotByKuntaId);
    }
}
