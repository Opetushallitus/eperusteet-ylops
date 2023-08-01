package fi.vm.sade.eperusteet.ylops.service.external.impl;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import fi.vm.sade.eperusteet.ylops.domain.KoulutusTyyppi;
import fi.vm.sade.eperusteet.ylops.domain.cache.PerusteCache;
import fi.vm.sade.eperusteet.ylops.domain.teksti.LokalisoituTeksti;
import fi.vm.sade.eperusteet.ylops.dto.PalauteDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.TermiDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.PerusteDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.PerusteInfoDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.TiedoteQueryDto;
import fi.vm.sade.eperusteet.ylops.repository.cache.PerusteCacheRepository;
import fi.vm.sade.eperusteet.ylops.service.exception.BusinessRuleViolationException;
import fi.vm.sade.eperusteet.ylops.service.external.EperusteetService;
import fi.vm.sade.eperusteet.ylops.service.external.impl.perustedto.EperusteetPerusteDto;
import fi.vm.sade.eperusteet.ylops.service.mapping.DtoMapper;
import fi.vm.sade.eperusteet.ylops.service.util.JsonMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;


@Profile("test")
@Transactional
public class EperusteetLocalService implements EperusteetService {

    @Override
    public List<PerusteInfoDto> findPerusteet(boolean forceRefresh) {
        return null;
    }

    @Autowired
    private PerusteCacheRepository perusteCacheRepository;

    @Autowired
    private JsonMapper jsonMapper;

    @Autowired
    private DtoMapper mapper;

    private ObjectMapper objectMapper = new ObjectMapper();

    private List<JsonNode> perusteet = new ArrayList<>();
    private JsonNode tiedotteet = null;

    private Optional<JsonNode> readJson(String file) {
        try {
            return Optional.ofNullable(objectMapper.readTree(getClass().getResourceAsStream("/fakedata/tiedotteet.json")));
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    @PostConstruct
    public void init() {
        objectMapper.configure(JsonGenerator.Feature.IGNORE_UNKNOWN, true);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        perusteet.add(openFakeData("/fakedata/varhaiskasvatus.json"));
        perusteet.add(openFakeData("/fakedata/peruste.json"));
        perusteet.add(openFakeData("/fakedata/lops.json"));
        perusteet.add(openFakeData("/fakedata/lops2019.json"));
        perusteet.add(openFakeData("/fakedata/peruste-arvioinninkohteet.json"));
        perusteet.add(openFakeData("/fakedata/peruste-perusopetus.json"));
        perusteet.add(openFakeData("/fakedata/peruste-tpo.json"));
        tiedotteet = readJson("/fakedata/tiedotteet.json")
            .orElse((new JsonNodeFactory(false)).objectNode());
    }

    private JsonNode openFakeData(String file) {
        try {
            JsonNode result = objectMapper.readTree(getClass().getResourceAsStream(file));
            PerusteCache peruste = perusteCacheRepository.findNewestEntryForPerusteByDiaarinumero(result.get("diaarinumero").asText());
            if (peruste == null) {
                savePerusteCahceEntry(objectMapper.treeToValue(result, EperusteetPerusteDto.class));
            }
            return result;
        } catch (IOException e) {
            throw new BusinessRuleViolationException("datan-hakeminen-epaonnistui", e);
        }
    }

    // FIXME pilko service kahteen osaan
    private void savePerusteCahceEntry(EperusteetPerusteDto peruste) {
        PerusteCache cache = new PerusteCache();
        cache.setAikaleima(peruste.getGlobalVersion().getAikaleima());
        cache.setPerusteId(peruste.getId());
        cache.setKoulutustyyppi(peruste.getKoulutustyyppi());
        cache.setDiaarinumero(peruste.getDiaarinumero());
        cache.setVoimassaoloAlkaa(peruste.getVoimassaoloAlkaa());
        cache.setVoimassaoloLoppuu(peruste.getVoimassaoloLoppuu());
        cache.setNimi(LokalisoituTeksti.of(peruste.getNimi().getTekstit()));
        try {
            cache.setPerusteJson(peruste, jsonMapper);
        } catch (IOException e) {
            // Should not happen (EperusteetPerusteDto parsed from JSON to begin with)
            throw new IllegalStateException("Could not serialize EperusteetPerusteDto for cache.", e);
        }
        perusteCacheRepository.saveAndFlush(cache);
    }

    private PerusteDto jsonToPerusteDto(JsonNode perusteJson) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(JsonGenerator.Feature.IGNORE_UNKNOWN, true);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        try {
            EperusteetPerusteDto eperusteetPerusteDto = objectMapper.treeToValue(perusteJson, EperusteetPerusteDto.class);
            return mapper.map(eperusteetPerusteDto, PerusteDto.class);
        } catch (JsonProcessingException e) {
            throw new BusinessRuleViolationException("perusteen-parsinta-epaonnistui", e);
        }
    }

    private PerusteDto getPeruste(Predicate<JsonNode> cmp) {
        for (JsonNode peruste : perusteet) {
            if (cmp.test(peruste)) {
                return jsonToPerusteDto(peruste);
            }
        }
        return null;
    }

    @Override
    public String getYllapitoAsetus(String key) {
        return null;
    }

    @Override
    public PerusteDto getPeruste(String diaariNumero) {
        return getPeruste((peruste) ->
                peruste.get("diaarinumero") != null
                        && Objects.equals(diaariNumero, peruste.get("diaarinumero").asText()));
    }

    @Override
    public PerusteDto getPerusteUpdateCache(String diaarinumero) {
        throw new UnsupportedOperationException("ei-toteutettu");
    }

    @Override
    public List<PerusteInfoDto> findPerusteet() {
        ObjectMapper objectMapper = new ObjectMapper();
        return perusteet.stream()
                .map(p -> {
                    try {
                        return objectMapper.treeToValue(p, PerusteInfoDto.class);
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                        throw new BusinessRuleViolationException("perusteen-parsinta-epaonnistui");
                    }
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<PerusteInfoDto> findPerusteet(Set<KoulutusTyyppi> tyypit) {
        return findPerusteet().stream()
                .filter(peruste -> tyypit.contains(peruste.getKoulutustyyppi()))
                .collect(Collectors.toList());
    }

    @Override
    @Deprecated
    public List<PerusteInfoDto> findPerusopetuksenPerusteet() {
        throw new UnsupportedOperationException("ei-toteutettu");
    }

    @Override
    @Deprecated
    public List<PerusteInfoDto> findLukiokoulutusPerusteet() {
        throw new UnsupportedOperationException("ei-toteutettu");
    }

    @Override
    public PerusteDto getPerusteById(Long id) {
        return getPeruste((peruste) -> peruste.get("id") != null
               && Objects.equals(
                       id,
                       peruste.get("id").asLong()));
    }

    @Override
    public JsonNode getTiedotteet(Long jalkeen) {
        return tiedotteet;
    }

    @Override
    public JsonNode getTiedotteetHaku(TiedoteQueryDto queryDto) {
        return tiedotteet;
    }

    @Override
    public byte[] getLiite(Long perusteId, UUID id) {
        return new byte[0];
    }

    @Override
    public PalauteDto lahetaPalaute(PalauteDto palaute) throws JsonProcessingException {
        return null;
    }

    @Override
    public TermiDto getTermi(Long perusteId, String avain) {
        return null;
    }

    @Override
    public Date viimeisinPerusteenJulkaisuaika(Long perusteId) {
        return null;
    }

}
