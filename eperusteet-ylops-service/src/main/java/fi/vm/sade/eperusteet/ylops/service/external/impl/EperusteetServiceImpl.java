/*
 * Copyright (c) 2013 The Finnish Board of Education - Opetushallitus
 *
 * This program is free software: Licensed under the EUPL, Version 1.1 or - as
 * soon as they will be approved by the European Commission - subsequent versions
 * of the EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at: http://ec.europa.eu/idabc/eupl
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * European Union Public Licence for more details.
 */
package fi.vm.sade.eperusteet.ylops.service.external.impl;

import com.fasterxml.jackson.databind.JsonNode;
import fi.vm.sade.eperusteet.ylops.domain.KoulutusTyyppi;
import fi.vm.sade.eperusteet.ylops.domain.cache.PerusteCache;
import fi.vm.sade.eperusteet.ylops.domain.teksti.LokalisoituTeksti;
import fi.vm.sade.eperusteet.ylops.dto.dokumentti.LokalisointiDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.PerusteDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.PerusteInfoDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.TiedoteQueryDto;
import fi.vm.sade.eperusteet.ylops.repository.cache.PerusteCacheRepository;
import fi.vm.sade.eperusteet.ylops.service.exception.BusinessRuleViolationException;
import fi.vm.sade.eperusteet.ylops.service.exception.NotExistsException;
import fi.vm.sade.eperusteet.ylops.service.external.EperusteetService;
import fi.vm.sade.eperusteet.ylops.service.external.impl.perustedto.EperusteetPerusteDto;
import fi.vm.sade.eperusteet.ylops.service.mapping.DtoMapper;
import static fi.vm.sade.eperusteet.ylops.service.util.ExceptionUtil.wrapRuntime;
import fi.vm.sade.eperusteet.ylops.service.util.JsonMapper;
import java.io.IOException;
import java.util.*;
import static java.util.Collections.singletonList;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import javax.annotation.PostConstruct;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

/**
 * @author nkala
 */
@Slf4j
@Service
@Profile("!test")
@SuppressWarnings("TransactionalAnnotations")
public class EperusteetServiceImpl implements EperusteetService {

    @Value("${fi.vm.sade.eperusteet.ylops.eperusteet-service: ''}")
    private String eperusteetServiceUrl;
    @Value("${fi.vm.sade.eperusteet.ylops.koulutustyyppi_perusopetus:koulutustyyppi_16}")
    private String koulutustyyppiPerusopetus;
    @Value("${fi.vm.sade.eperusteet.ylops.koulutustyyppi_lukiokoilutus:koulutustyyppi_2}")
    private String koulutustyyppiLukiokoulutus;

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
            PerusteInfoWrapperDto wrapperDto
                    = client.exchange(eperusteetServiceUrl + "/api/perusteet?tyyppi={koulutustyyppi}&sivukoko={sivukoko}",
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
        try {
            EperusteetPerusteDto peruste = client.exchange(eperusteetServiceUrl
                    + "/api/perusteet/{id}/kaikki", HttpMethod.GET, httpEntity, EperusteetPerusteDto.class, id).getBody();

            Date newest = perusteCacheRepository.findNewestEntryAikaleimaForPeruste(id);
            if (peruste.getGlobalVersion() != null // not all backend environments may return this info yet
                    && (newest == null || newest.compareTo(peruste.getGlobalVersion().getAikaleima()) < 0)) {
                savePerusteCahceEntry(peruste);
            }
            return peruste;
        } catch (Exception e) {
            if (forceRefresh) {
                throw e;
            }
            log.warn("Could not fetch newest peruste from ePerusteet: " + e.getMessage()
                    + " Trying from DB-cache.");
            PerusteCache found = perusteCacheRepository.findNewestEntryForPeruste(id);
            if (found == null) {
                log.warn("No cache entry for Peruste id=" + id);
                throw e;
            }
            try {
                return found.getPerusteJson(jsonMapper);
            } catch (IOException e1) {
                log.error("Failed to fallback-unserialize PerusteCache entry: " + found.getId()
                        + " for peruste id=" + id, e1);
                throw e;
            }
        }
    }

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

    @Override
    @Cacheable("perusteet")
    @Transactional
    public PerusteDto getPeruste(String diaarinumero) throws NotExistsException {
        return getPerusteByDiaari(diaarinumero, false);
    }

    @Override
    @CachePut("perusteet")
    @Transactional
    public PerusteDto getPerusteUpdateCache(String diaarinumero) throws NotExistsException {
        return getPerusteByDiaari(diaarinumero, true);
    }

    private PerusteDto getPerusteByDiaari(String diaarinumero, boolean forceRefresh) throws NotExistsException {
        PerusteInfoDto perusteInfoDto = findPerusteet(forceRefresh).stream()
                .filter(p -> diaarinumero.equals(p.getDiaarinumero()))
                .findAny()
                .orElseThrow(() -> new NotExistsException("Perustetta ei löytynyt"));

        return getEperusteetPeruste(perusteInfoDto.getId(), forceRefresh);
    }

    @Override
    public JsonNode getTiedotteet(Long jalkeen) {
        String params = "";
        if (jalkeen != null) {
            params = "?alkaen=" + String.valueOf(jalkeen);
        }
        return client.exchange(eperusteetServiceUrl + "/api/tiedotteet" + params, HttpMethod.GET, httpEntity, JsonNode.class).getBody();
    }

    @Override
    public JsonNode getTiedotteetHaku(TiedoteQueryDto queryDto) {
        String uri = eperusteetServiceUrl.concat("/api/tiedotteet/haku").concat(queryDto.toRequestParams());
        JsonNode result = client.exchange(uri, HttpMethod.GET, httpEntity, JsonNode.class).getBody();
        return result;
    }

    @Override
    public byte[] getLiite(Long perusteId, UUID id) {
        return client.exchange(eperusteetServiceUrl + "/api/perusteet/{perusteId}/kuvat/{id}", HttpMethod.GET, httpEntity, byte[].class, perusteId, id).getBody();
    }

    @Getter
    @Setter
    private static class PerusteInfoWrapperDto {
        private List<PerusteInfoDto> data;
    }
}
