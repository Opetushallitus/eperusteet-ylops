package fi.vm.sade.eperusteet.ylops.service.external.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Throwables;
import fi.vm.sade.eperusteet.utils.client.OphClientHelper;
import fi.vm.sade.eperusteet.utils.client.RestClientFactory;
import fi.vm.sade.eperusteet.ylops.domain.KoulutusTyyppi;
import fi.vm.sade.eperusteet.ylops.domain.cache.PerusteCache;
import fi.vm.sade.eperusteet.ylops.domain.teksti.LokalisoituTeksti;
import fi.vm.sade.eperusteet.ylops.dto.ops.TermiDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.PerusteDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.PerusteInfoDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.PerusteJulkaisuKevytDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.TiedoteQueryDto;
import fi.vm.sade.eperusteet.ylops.repository.cache.PerusteCacheRepository;
import fi.vm.sade.eperusteet.ylops.service.exception.BusinessRuleViolationException;
import fi.vm.sade.eperusteet.ylops.service.exception.NotExistsException;
import fi.vm.sade.eperusteet.ylops.service.external.EperusteetService;
import fi.vm.sade.eperusteet.ylops.service.external.impl.perustedto.EperusteetPerusteDto;
import fi.vm.sade.eperusteet.ylops.service.mapping.DtoMapper;
import fi.vm.sade.eperusteet.ylops.service.util.JsonMapper;
import fi.vm.sade.javautils.http.OphHttpClient;
import fi.vm.sade.javautils.http.OphHttpRequest;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static fi.vm.sade.eperusteet.ylops.service.util.ExceptionUtil.wrapRuntime;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static javax.servlet.http.HttpServletResponse.SC_OK;

@Slf4j
@Service
@Profile("!test")
@SuppressWarnings("TransactionalAnnotations")
public class EperusteetServiceImpl implements EperusteetService {

    @Value("${fi.vm.sade.eperusteet.ylops.eperusteet-service: ''}")
    private String eperusteetServiceUrl;

    @Value("${fi.vm.sade.eperusteet.ylops.eperusteet-service.internal:${fi.vm.sade.eperusteet.ylops.eperusteet-service:''}}")
    private String eperusteetServiceInternalUrl;

    // feature that could be used to populate data and turned off after all existing
    // perusteet in the environment has been synced:
    @Value("${fi.vm.sade.eperusteet.ylops.update-peruste-cache-for-all-missing: false}")
    private boolean updateMissingToCache;

    @Autowired
    private PerusteCacheRepository perusteCacheRepository;

    @Autowired
    private JsonMapper jsonMapper;

    @Autowired
    private DtoMapper mapper;

    private RestTemplate client;

    @Autowired
    private HttpEntity httpEntity;

    @Autowired
    private RestClientFactory restClientFactory;

    @Autowired
    private OphClientHelper ophClientHelper;

    @PostConstruct
    protected void init() {
        client = new RestTemplate(singletonList(jsonMapper.messageConverter().orElseThrow(IllegalStateException::new)));
        ByteArrayHttpMessageConverter converter = new ByteArrayHttpMessageConverter();
        converter.setSupportedMediaTypes(Arrays.asList(MediaType.IMAGE_JPEG, MediaType.IMAGE_PNG));
        client.getMessageConverters().add(converter);
    }

    private Set<KoulutusTyyppi> getKoulutuskoodit() {
        KoulutusTyyppi[] vaihtoehdot = {
                KoulutusTyyppi.ESIOPETUS,
                KoulutusTyyppi.PERUSOPETUS,
                KoulutusTyyppi.LISAOPETUS,
                KoulutusTyyppi.VARHAISKASVATUS,
                KoulutusTyyppi.LUKIOKOULUTUS,
                KoulutusTyyppi.LUKIOVALMISTAVAKOULUTUS,
                KoulutusTyyppi.PERUSOPETUSVALMISTAVA,
                KoulutusTyyppi.AIKUISLUKIOKOULUTUS,
                KoulutusTyyppi.TPO,
                KoulutusTyyppi.AIKUISTENPERUSOPETUS
        };
        return new HashSet<>(Arrays.asList(vaihtoehdot));
    }

    @Override
    public List<PerusteInfoDto> findPerusteet() {
        return findPerusteet(getKoulutuskoodit(), false);
    }

    @Override
    public List<PerusteInfoDto> findPerusteet(boolean forceRefresh) {
        return findPerusteet(getKoulutuskoodit(), forceRefresh);
    }

    @Override
    public List<PerusteInfoDto> findPerusteet(Set<KoulutusTyyppi> tyypit) {
        return findPerusteet(tyypit, false);
    }

    private List<PerusteInfoDto> findPerusteet(Set<KoulutusTyyppi> tyypit, boolean forceRefresh) {
        try {
            return updateMissingToCache(findPerusteetFromEperusteService(tyypit), tyypit);
        } catch (Exception e) {
            if (forceRefresh) {
                throw e;
            }
            log.warn("Could not fetch newest peruste from ePerusteet: " + e.getMessage()
                    + " Trying from DB-cache.", e);
            List<PerusteInfoDto> result = perusteCacheRepository.findNewestEntrieByKoulutustyyppis(tyypit).stream()
                    .map(wrapRuntime(
                            c -> c.getPerusteJson(jsonMapper),
                            (IOException e1) -> new IllegalStateException("Failed deserialize DB-fallback peruste: " + e1.getMessage(), e)))
                    .map(f -> mapper.map(f, PerusteInfoDto.class))
                    .collect(toList());
            return result;
        }
    }

    private <C extends Collection<PerusteInfoDto>> C updateMissingToCache(C perusteet, Set<KoulutusTyyppi> tyypit) {
        if (updateMissingToCache) {
            List<PerusteCache> currentList = perusteCacheRepository.findNewestEntrieByKoulutustyyppis(tyypit);
            Map<Long, PerusteCache> byId = currentList.stream().collect(toMap(PerusteCache::getPerusteId, c -> c));
            perusteet.stream()
                    .filter(p -> p.getGlobalVersion() != null)
                    .forEach(p -> {
                        PerusteCache current = byId.get(p.getId());
                        if (current == null
                                || current.getAikaleima().compareTo(p.getGlobalVersion().getAikaleima()) < 0
                                || !Objects.equals(current.getKoulutustyyppi(), p.getKoulutustyyppi())) {
                            getPerusteById(p.getId());
                        }
                    });
        }
        return perusteet;
    }

    private List<PerusteInfoDto> findPerusteetFromEperusteService(Set<KoulutusTyyppi> tyypit) {
        List<PerusteInfoDto> infot = new ArrayList<>();
        for (KoulutusTyyppi tyyppi : tyypit) {
            String url = eperusteetServiceInternalUrl + "/api/perusteet?tyyppi={koulutustyyppi}&sivukoko={sivukoko}&julkaistu=true";
            PerusteInfoWrapperDto wrapperDto = client.exchange(
                    url,
                    HttpMethod.GET, httpEntity, PerusteInfoWrapperDto.class, tyyppi.toString(), 100).getBody();

            for (PerusteInfoDto peruste : wrapperDto.getData()) {
                try {
                    log.debug("Perustepohja:", peruste.getId(), peruste.getDiaarinumero(), peruste.getVoimassaoloAlkaa());
                } catch (Exception e) {
                    // Just in case...
                }
            }

            // Filtteröi pois perusteet jotka eivät enää ole voimassa
            Date now = new Date();
            infot.addAll(wrapperDto.getData().stream()
                    .filter(peruste -> peruste.getVoimassaoloLoppuu() == null || peruste.getVoimassaoloLoppuu().after(now))
                    .collect(Collectors.toList()));
        }

        return infot;
    }

    private List<PerusteInfoDto> cacheToInfo(List<PerusteCache> caches) {
        return caches.stream().map(wrapRuntime(c -> c.getPerusteJson(jsonMapper),
                (IOException e1) -> new IllegalStateException("Failed deserialize DB-fallback peruste: " + e1.getMessage(), e1)))
                .map(f -> mapper.map(f, PerusteInfoDto.class))
                .collect(toList());
    }

    @Override
    public List<PerusteInfoDto> findPerusopetuksenPerusteet() {
        return findPerusteet(new HashSet<>(singletonList(KoulutusTyyppi.PERUSOPETUS)));
    }

    @Override
    public List<PerusteInfoDto> findLukiokoulutusPerusteet() {
        return findPerusteet(new HashSet<>(Arrays.asList(
                KoulutusTyyppi.LUKIOKOULUTUS,
                KoulutusTyyppi.LUKIOVALMISTAVAKOULUTUS,
                KoulutusTyyppi.AIKUISLUKIOKOULUTUS)));
    }

    @Override
    @Cacheable("perusteet")
    @Transactional
    public PerusteDto getPerusteById(final Long id) {
        return getEperusteetPeruste(id, false);
    }

    private PerusteDto getEperusteetPeruste(final Long id, boolean forceRefresh) {
        EperusteetPerusteDto peruste = getNewestPeruste(id, forceRefresh);
        if (peruste == null || !getKoulutuskoodit().contains(peruste.getKoulutustyyppi())) {
            throw new BusinessRuleViolationException("Perustetta ei löytynyt tai se ei ole perusopetuksen peruste");
        }
        return mapper.map(peruste, PerusteDto.class);
    }

    private EperusteetPerusteDto getNewestPeruste(final long id, boolean forceRefresh) {
        PerusteCache found = perusteCacheRepository.findNewestEntryForPeruste(id);
        if (found == null || forceRefresh) {
            try {
                EperusteetPerusteDto peruste = client.exchange(eperusteetServiceInternalUrl
                        + "/api/perusteet/{id}/kaikki", HttpMethod.GET, httpEntity, EperusteetPerusteDto.class, id).getBody();

                Date newest = perusteCacheRepository.findNewestEntryAikaleimaForPeruste(id);
                if (forceRefresh
                        || (peruste.getGlobalVersion() != null && (newest == null || newest.compareTo(peruste.getGlobalVersion().getAikaleima()) < 0))) {
                    savePerusteCahceEntry(peruste);
                }
                return peruste;
            } catch (Exception e) {
                Throwables.getStackTraceAsString(e);
                throw new BusinessRuleViolationException("Virhe haettaessa perustetta ePerusteista.");
            }
        }

        try {
            return found.getPerusteJson(jsonMapper);
        } catch (IOException e) {
            log.error("Failed to fallback-unserialize PerusteCache entry: " + found.getId()
                    + " for peruste id=" + id, e);
            return null;
        }
    }

    private void savePerusteCahceEntry(EperusteetPerusteDto peruste) {
        PerusteCache cache = new PerusteCache();
        cache.setAikaleima(new Date());
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

    @Override
    @Cacheable("perusteet")
    @Transactional
    public PerusteDto getPeruste(String diaarinumero) throws NotExistsException {
        try {
            return getPerusteByDiaari(diaarinumero, false);
        } catch(NotExistsException e) {
            PerusteCache perusteCache = perusteCacheRepository.findNewestEntryForPerusteByDiaarinumero(diaarinumero);
            return getPerusteById(perusteCache.getPerusteId());
        }
    }

    @Override
    @CachePut("perusteet")
    @Transactional
    public PerusteDto getPerusteUpdateCache(String diaarinumero) throws NotExistsException {
        return getPerusteByDiaari(diaarinumero, true);
    }

    private PerusteDto getPerusteByDiaari(String diaarinumero, boolean forceRefresh) throws NotExistsException {
        Optional<PerusteInfoDto> perusteInfoDto = findPerusteet(forceRefresh).stream()
                .filter(p -> diaarinumero.equals(p.getDiaarinumero()))
                .findAny();

        Long perusteId = null;
        if (perusteInfoDto.isPresent()) {
            perusteId = perusteInfoDto.get().getId();
        } else if (forceRefresh) {
            PerusteCache perusteCache = perusteCacheRepository.findNewestEntryForPerusteByDiaarinumero(diaarinumero);
            if (perusteCache != null) {
                perusteId = perusteCache.getPerusteId();
            }
        }

        if (perusteId == null) {
            throw new NotExistsException("Perustetta ei löytynyt");
        }

        return getEperusteetPeruste(perusteId, forceRefresh);
    }

    @Override
    public JsonNode getTiedotteet(Long jalkeen) {
        String params = "";
        if (jalkeen != null) {
            params = "?alkaen=" + String.valueOf(jalkeen);
        }
        return client.exchange(eperusteetServiceInternalUrl + "/api/tiedotteet" + params, HttpMethod.GET, httpEntity, JsonNode.class).getBody();
    }

    @Override
    public JsonNode getTiedotteetHaku(TiedoteQueryDto queryDto) {
        String url = eperusteetServiceInternalUrl.concat("/api/tiedotteet/haku").concat(queryDto.toRequestParams());
        OphHttpClient client = restClientFactory.get(eperusteetServiceUrl, true);
        OphHttpRequest request = OphHttpRequest.Builder
                .get(url)
                .build();

        return client.<JsonNode>execute(request)
                .expectedStatus(SC_OK)
                .mapWith(text -> {
                    try {
                        return new ObjectMapper().readTree(text);
                    } catch (IOException ex) {
                        throw new BusinessRuleViolationException("Tiedotteiden tietojen hakeminen epäonnistui", ex);
                    }
                })
                .orElse(null);
    }

    @Override
    public byte[] getLiite(Long perusteId, UUID id) {
        return client.exchange(eperusteetServiceInternalUrl + "/api/perusteet/{perusteId}/kuvat/{id}", HttpMethod.GET, httpEntity, byte[].class, perusteId, id).getBody();
    }

    @Override
    public TermiDto getTermi(Long perusteId, String avain) {
        return client.exchange(eperusteetServiceInternalUrl + "/api/perusteet/{perusteId}/termisto/{id}", HttpMethod.GET, httpEntity, TermiDto.class, perusteId, avain).getBody();
    }

    @Override
    public Date viimeisinPerusteenJulkaisuaika(Long perusteId) {
        return client.exchange(eperusteetServiceInternalUrl + "/api/perusteet/{perusteId}/viimeisinjulkaisuaika", HttpMethod.GET, httpEntity, Date.class, perusteId).getBody();
    }

    @Override
    public JsonNode getPerusteenJulkaisuByGlobalversionMuutosaika(Long perusteId, Date globalVersionMuutosaika) {
        Integer revision = getPerusteRevisionByGlobalVersionMuutosaika(perusteId, globalVersionMuutosaika);
        if (revision == null) {
            return null;
        }

        return getPerusteByRevision(perusteId, revision);
    }

    private Integer getPerusteRevisionByGlobalVersionMuutosaika(Long perusteId, Date globalVersionMuutosaika) {
        String url = eperusteetServiceInternalUrl + "/api/perusteet/" + perusteId + "/julkaisut/kaikki";
        PerusteJulkaisuKevytDto[] julkaisut = client.exchange(url, HttpMethod.GET, httpEntity, PerusteJulkaisuKevytDto[].class).getBody();
        if (julkaisut == null) {
            return null;
        }

        Optional<PerusteJulkaisuKevytDto> perusteJulkaisuKevytDto = Arrays.stream(julkaisut)
                .filter(julkaisu -> julkaisu.getLuotu().compareTo(globalVersionMuutosaika) >= 0)
                .min(Comparator.comparing(PerusteJulkaisuKevytDto::getLuotu));

        if (perusteJulkaisuKevytDto.isEmpty()) {
            return null;
        }

        return perusteJulkaisuKevytDto.get().getRevision();
    }

    @Override
    public JsonNode getPerusteByRevision(Long perusteId, Integer revision) {
        String url = eperusteetServiceInternalUrl + "/api/perusteet/" + perusteId + "/kaikki/?rev=" + revision;
        return client.exchange(url, HttpMethod.GET, httpEntity, JsonNode.class).getBody();
    }

    @Override
    public EperusteetPerusteDto getPerusteDtoByRevision(Long perusteId, Integer revision) {
        return client.exchange(eperusteetServiceInternalUrl + "/api/perusteet/{id}/kaikki?rev={revision}",
                    HttpMethod.GET,
                    httpEntity,
                    EperusteetPerusteDto.class,
                    perusteId,
                    revision)
                .getBody();
    }

    @Getter
    @Setter
    private static class PerusteInfoWrapperDto {
        private List<PerusteInfoDto> data;
    }

    @Override
    public PerusteDto getPerusteenJulkaisuByGlobalversionMuutosaikaAsDto(Long perusteId, Date globalVersionMuutosaika) {
        Integer revision = getPerusteRevisionByGlobalVersionMuutosaika(perusteId, globalVersionMuutosaika);
        EperusteetPerusteDto eperusteetPerusteDto = getPerusteDtoByRevision(perusteId, revision);
        return mapper.map(eperusteetPerusteDto, PerusteDto.class);
    }
}
