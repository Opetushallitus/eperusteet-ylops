package fi.vm.sade.eperusteet.ylops.service.external.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fi.vm.sade.eperusteet.utils.client.OphClientHelper;
import fi.vm.sade.eperusteet.utils.client.RestClientFactory;
import fi.vm.sade.eperusteet.ylops.dto.kayttaja.KayttajanTietoDto;
import fi.vm.sade.eperusteet.ylops.service.external.KayttajaClient;
import fi.vm.sade.javautils.http.OphHttpClient;
import fi.vm.sade.javautils.http.OphHttpRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static fi.vm.sade.eperusteet.ylops.service.external.impl.KayttajanTietoParser.parsiKayttaja;
import static jakarta.servlet.http.HttpServletResponse.SC_FORBIDDEN;
import static jakarta.servlet.http.HttpServletResponse.SC_OK;
import static jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;

@Service
public class KayttajaClientImpl implements KayttajaClient {

    @Autowired
    OphClientHelper ophClientHelper;

    @Value("${cas.service.oppijanumerorekisteri-service:''}")
    private String onrServiceUrl;

    private static final String HENKILO_API = "/henkilo/";
    private static final String HENKILOT_BY_LIST = HENKILO_API + "henkilotByHenkiloOidList";

    @Autowired
    private RestClientFactory restClientFactory;

    private final ObjectMapper mapper = new ObjectMapper();

    @Cacheable("kayttajat")
    public KayttajanTietoDto hae(String oid) {
        OphHttpClient client = restClientFactory.get(onrServiceUrl, true);
        String url = onrServiceUrl + HENKILO_API + oid;

        OphHttpRequest request = OphHttpRequest.Builder
                .get(url)
                .build();

        try {
            return client.<KayttajanTietoDto>execute(request)
                    .handleErrorStatus(SC_UNAUTHORIZED, SC_FORBIDDEN)
                    .with((res) -> Optional.of(new KayttajanTietoDto(oid)))
                    .expectedStatus(SC_OK)
                    .mapWith(text -> {
                        try {
                            JsonNode json = mapper.readTree(text);
                            return parsiKayttaja(json);
                        } catch (IOException e) {
                            return new KayttajanTietoDto(oid);
                        }
                    })
                    .orElse(new KayttajanTietoDto(oid));
        }
        catch (RuntimeException ex) {
            return new KayttajanTietoDto(oid);
        }

    }

    @Override
    public List<KayttajanTietoDto> haeKayttajatiedot(List<String> oid) {
        return ophClientHelper.postAsList(onrServiceUrl, onrServiceUrl+HENKILOT_BY_LIST, oid, KayttajanTietoDto.class);
    }
}
