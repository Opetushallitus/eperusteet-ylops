package fi.vm.sade.eperusteet.ylops.resource.util;

import org.springframework.http.HttpHeaders;

public final class Etags {

    private static final String WEAK_ETAG_PREFIX = "\"W/";

    private Etags() {
        //apuluokka
    }

    public static Integer revisionOf(String eTag) {
        if (eTag == null) {
            return null;
        }
        if (eTag.startsWith(WEAK_ETAG_PREFIX)) {
            return Integer.parseInt(eTag.substring(3, eTag.length() - 1));
        }
        throw new IllegalArgumentException("virheellinen eTag");
    }

    public static HttpHeaders eTagHeader(Integer revision) {
        return addETag(new HttpHeaders(), revision);
    }

    public static HttpHeaders addETag(HttpHeaders headers, Integer revision) {
        if (revision != null) {
            headers.setETag(wrap(String.valueOf(revision)));
        }
        return headers;
    }

    private static String wrap(String value) {
        return WEAK_ETAG_PREFIX + value + "\"";
    }

}
