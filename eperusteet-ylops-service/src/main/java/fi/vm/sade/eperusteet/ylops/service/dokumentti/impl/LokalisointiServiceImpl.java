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

package fi.vm.sade.eperusteet.ylops.service.dokumentti.impl;

import fi.vm.sade.eperusteet.ylops.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.ylops.dto.dokumentti.LokalisointiDto;
import fi.vm.sade.eperusteet.ylops.service.dokumentti.LokalisointiService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author iSaul
 */
@Slf4j
@Service
public class LokalisointiServiceImpl implements LokalisointiService {

    private static final Logger LOG = LoggerFactory.getLogger(LokalisointiService.class);

    @Value("${lokalisointi.service.url:https://virkailija.opintopolku.fi/lokalisointi/cxf/rest/v1/localisation?}")
    private String lokalisointiServiceUrl;

    @Value("${lokalisointi.service.category:eperusteet}")
    private String category;


    @Override
    @Cacheable("lokalisoinnit")
    public LokalisointiDto get(String key, String locale) {
        RestTemplate restTemplate = new RestTemplate();
        String url = lokalisointiServiceUrl + "category=" + category + "&locale=" + locale + "&key=" + key;
        LokalisointiDto[] re;
        try {
            re = restTemplate.getForObject(url, LokalisointiDto[].class);
        } catch (RestClientException ex) {
            LOG.error("Rest client error: {}", ex.getLocalizedMessage());
            re = new LokalisointiDto[]{};
        }

        if (re.length > 1) {
            LOG.warn("Got more than one object: {} from {}", re, url);
        }
        if (re.length > 0) {
            return re[0];
        }

        return null;
    }

    @Override
    @Cacheable("ylops-lokalisoinnit")
    public Map<Kieli, List<LokalisointiDto>> getAll() {
        Map<Kieli, List<LokalisointiDto>> result = new HashMap<>();
        for (LokalisointiDto l : getAllByCategoryAndLocale("eperusteet-ylops")) {
            Kieli locale = Kieli.of(l.getLocale());
            if (!result.containsKey(locale)) {
                result.put(locale, new ArrayList<>());
            }
            result.get(locale).add(l);
        }
        return result;
    }

    public List<LokalisointiDto> getAllByCategoryAndLocale(String category) {
        return getAllByCategoryAndLocale(category, null);
    }

    public List<LokalisointiDto> getAllByCategoryAndLocale(String category, String locale) {
        RestTemplate restTemplate = new RestTemplate();
        String url = lokalisointiServiceUrl + "category=" + category;
        if (locale != null) {
           url += "&locale=" + locale;
        }

        try {
            LokalisointiDto[] result = restTemplate.getForObject(url, LokalisointiDto[].class);
            return Arrays.asList(result);
        } catch (RestClientException e) {
            log.error(e.getLocalizedMessage());
            return new ArrayList<>();
        }
    }
}
