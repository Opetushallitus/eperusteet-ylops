package fi.vm.sade.eperusteet.ylops.domain.validation;

import fi.vm.sade.eperusteet.ylops.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.ylops.domain.teksti.LokalisoituTeksti;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

import java.util.Map;

public class ValidHtmlValidator extends ValidHtmlValidatorBase implements
        ConstraintValidator<ValidHtml, LokalisoituTeksti> {

    @Override
    public void initialize(ValidHtml constraintAnnotation) {
        setupValidator(constraintAnnotation);
    }

    @Override
    public boolean isValid(LokalisoituTeksti value, ConstraintValidatorContext context) {
        return isValid(value);
    }

    public static boolean isValid(LokalisoituTeksti lokalisoituTeksti, Safelist whitelist) {
        if (lokalisoituTeksti != null) {
            Map<Kieli, String> tekstit = lokalisoituTeksti.getTeksti();
            if (tekstit != null) {
                return tekstit.values().stream()
                        .allMatch(teksti -> Jsoup.isValid(teksti, whitelist));
            }
        }
        return true;
    }
}
