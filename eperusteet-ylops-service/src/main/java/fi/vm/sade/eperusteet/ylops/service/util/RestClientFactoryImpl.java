package fi.vm.sade.eperusteet.ylops.service.util;

import fi.vm.sade.eperusteet.utils.client.RestClientFactory;
import fi.vm.sade.javautils.http.OphHttpClient;
import fi.vm.sade.javautils.http.auth.CasAuthenticator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
public class RestClientFactoryImpl implements RestClientFactory {

    public static final String CALLER_ID = "1.2.246.562.10.00000000001.eperusteet-ylops";

    private static final int TIMEOUT = 60000;

    @Value("${fi.vm.sade.eperusteet.ylops.oph_username:''}")
    private String username;

    @Value("${fi.vm.sade.eperusteet.ylops.oph_password:''}")
    private String password;

    @Value("${web.url.cas:''}")
    private String casUrl;

    private final ConcurrentMap<String, OphHttpClient> cache = new ConcurrentHashMap<>();

    public OphHttpClient get(String service) {
        return get(service, true);
    }

    public OphHttpClient getWithoutCas(String service) {
        return get(service, false);
    }

    public OphHttpClient get(String service, boolean requireCas) {

        if (cache.containsKey(service)) {
            return cache.get(service);
        } else {
            OphHttpClient client;
            if (requireCas) {
                CasAuthenticator casAuthenticator = new CasAuthenticator.Builder()
                        .username(username)
                        .password(password)
                        .webCasUrl(casUrl)
                        .casServiceUrl(service)
                        .build();

                client = new OphHttpClient.Builder(CALLER_ID)
                        .authenticator(casAuthenticator)
                        .timeoutMs(TIMEOUT)
                        .build();
            } else {
                client = new OphHttpClient.Builder(CALLER_ID)
                        .timeoutMs(TIMEOUT)
                        .build();
            }

            cache.putIfAbsent(service, client);
            return cache.get(service);
        }
    }

    @Override
    public String getCallerId() {
        return CALLER_ID;
    }
}
