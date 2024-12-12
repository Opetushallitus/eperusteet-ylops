package fi.vm.sade.eperusteet.ylops.service.external.impl;

import fi.vm.sade.eperusteet.ylops.dto.koodisto.KoodistoKoodiDto;
import fi.vm.sade.eperusteet.ylops.service.external.KoodistoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class KoodistoServiceImpl implements KoodistoService {

    @Value("${koodisto.service.url:https://virkailija.opintopolku.fi/koodisto-service}")
    private String koodistoServiceUrl;
    
    private static final String KOODISTO_API = "/rest/json/";
    private static final String YLARELAATIO = "relaatio/sisaltyy-ylakoodit/";
    private static final String ALARELAATIO = "relaatio/sisaltyy-alakoodit/";

    @Autowired
    private Client client;

    @Component
    public static class Client {

        private static final Logger LOG = LoggerFactory.getLogger(Client.class);
        private final RestTemplate restTemplate = new RestTemplate();

        @Autowired
        private HttpEntity httpEntity;

        @Cacheable(value = "koodistot", unless = "#result == null")
        public <T> T getForObject(String url, Class<T> responseType) {
            try {
                return restTemplate.exchange(url, HttpMethod.GET, httpEntity, responseType).getBody();
            } catch (HttpServerErrorException | HttpClientErrorException e) {
                LOG.warn(e.getMessage());
                return null;
            }
        }
    }

    @Override
    public List<KoodistoKoodiDto> getAll(String koodisto) {
        String url = koodistoServiceUrl + KOODISTO_API + koodisto + "/koodi/";
        KoodistoKoodiDto[] koodistot = client.getForObject(url, KoodistoKoodiDto[].class);
        List<KoodistoKoodiDto> koodistoLista;
        if ("kunta".equals(koodisto)) {
            koodistoLista
                    = Arrays.stream(koodistot)
                    // Filtteröi pois ex-kunnat
                    .filter(kunta -> kunta.getVoimassaLoppuPvm() == null)
                    // Ja "puuttuva" kunta
                    .filter(kunta -> !"999".equals(kunta.getKoodiArvo()))
                    .collect(Collectors.toList());
        } else {
            koodistoLista = koodistot == null ? Collections.emptyList() : Arrays.asList(koodistot);
        }
        return koodistoLista;
    }

    @Override
    public KoodistoKoodiDto get(String koodisto, String koodi) {
        String url = koodistoServiceUrl + KOODISTO_API + koodisto + "/koodi/" + koodi;
        return client.getForObject(url, KoodistoKoodiDto.class);
    }

    @Override
    public List<KoodistoKoodiDto> filterBy(String koodisto, String haku) {
        List<KoodistoKoodiDto> filter = getAll(koodisto);
        List<KoodistoKoodiDto> tulos = new ArrayList<>();

        Predicate<KoodistoKoodiDto> matches
                = x -> x.getKoodiUri().contains(haku) || Arrays.stream(x.getMetadata())
                .anyMatch(y -> y.getNimi().toLowerCase().contains(haku.toLowerCase()));

        filter.stream()
                .filter(matches)
                .forEach(tulos::add);
        return tulos;
    }

    @Override
    public List<KoodistoKoodiDto> getAlarelaatio(String koodi) {
        String url = koodistoServiceUrl + KOODISTO_API + ALARELAATIO + koodi;
        KoodistoKoodiDto[] koodistot = client.getForObject(url, KoodistoKoodiDto[].class);
        return koodistot == null ? null : Arrays.asList(koodistot);
    }

    @Override
    public List<KoodistoKoodiDto> getYlarelaatio(String koodi) {
        String url = koodistoServiceUrl + KOODISTO_API + YLARELAATIO + koodi;
        KoodistoKoodiDto[] koodistot = client.getForObject(url, KoodistoKoodiDto[].class);
        return koodistot == null ? null : Arrays.asList(koodistot);
    }

}
