package fi.vm.sade.eperusteet.ylops.domain.validation;

import com.google.common.base.CharMatcher;
import fi.vm.sade.eperusteet.ylops.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.ylops.domain.teksti.LokalisoituTeksti;
import org.apache.commons.validator.routines.EmailValidator;
import org.apache.commons.validator.routines.UrlValidator;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Safelist;
import org.jsoup.select.Elements;

import java.util.Map;

public abstract class ValidHtmlValidatorBase {

    private Safelist whitelist;
    private UrlValidator urlValidator = new UrlValidator(UrlValidator.ALLOW_LOCAL_URLS);
    private EmailValidator emailValidator = EmailValidator.getInstance(true, true);

    protected void setupValidator(ValidHtml constraintAnnotation) {
        whitelist = constraintAnnotation.whitelist().getWhitelist();
    }

    protected boolean isValid(LokalisoituTeksti lokalisoituTeksti) {
        if (lokalisoituTeksti != null) {
            Map<Kieli, String> tekstit = lokalisoituTeksti.getTeksti();
            if (tekstit != null) {
                return tekstit.values().stream()
                        .allMatch(teksti -> Jsoup.isValid(teksti, whitelist));
            }
        }
        return true;
    }

    @Deprecated
    private boolean isValidUrls(String teksti) {
        Document doc = Jsoup.parse(teksti);
        Elements links = doc.select("a[href]");
        return links.stream().allMatch(link ->
                !link.attr("routenode").isEmpty()
                        || urlValidator.isValid(CharMatcher.whitespace().trimFrom(link.attr("abs:href")))
                        || emailValidator.isValid(CharMatcher.whitespace().trimFrom(link.attr("href").replace("mailto:", "")))
        );
    }
}
