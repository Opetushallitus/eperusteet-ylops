package fi.vm.sade.eperusteet.ylops.service.dokumentti.impl;

import fi.vm.sade.eperusteet.ylops.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.ylops.dto.dokumentti.LokalisointiDto;
import fi.vm.sade.eperusteet.ylops.service.dokumentti.LokalisointiService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Slf4j
@Service
public class LokalisointiServiceImpl implements LokalisointiService {

    private static final Logger LOG = LoggerFactory.getLogger(LokalisointiService.class);

    @Value("${lokalisointi.service.url:https://virkailija.opintopolku.fi/lokalisointi/cxf/rest/v1/localisation?}")
    private String lokalisointiServiceUrl;

    @Value("${lokalisointi.service.category:eperusteet}")
    private String category;

    @Autowired
    private HttpEntity httpEntity;

    @Override
    @Cacheable("lokalisoinnit")
    public LokalisointiDto get(String key, String locale) {
        RestTemplate restTemplate = new RestTemplate();
        String url = lokalisointiServiceUrl + "category=" + category + "&locale=" + locale + "&key=" + key;
        LokalisointiDto[] re;
        try {
            re = restTemplate.exchange(url, HttpMethod.GET, httpEntity, LokalisointiDto[].class).getBody();
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
    public Map<Kieli, List<LokalisointiDto>> getAll() {
        Map<Kieli, List<LokalisointiDto>> result = new HashMap<>();
        extractCategoryToMap(result, "eperusteet-ylops");
        extractCategoryToMap(result, "eperusteet");
        return result;
    }

    private void extractCategoryToMap(Map<Kieli, List<LokalisointiDto>> result, String category) {
        for (LokalisointiDto l : getAllByCategoryAndLocale(category)) {
            Kieli locale = Kieli.of(l.getLocale());
            if (!result.containsKey(locale)) {
                result.put(locale, new ArrayList<>());
            }
            result.get(locale).add(l);
        }
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
            LokalisointiDto[] result = restTemplate.exchange(url, HttpMethod.GET, httpEntity, LokalisointiDto[].class).getBody();
            return Arrays.asList(result);
        } catch (RestClientException e) {
            log.error(e.getLocalizedMessage());
            return new ArrayList<>();
        }
    }
}
