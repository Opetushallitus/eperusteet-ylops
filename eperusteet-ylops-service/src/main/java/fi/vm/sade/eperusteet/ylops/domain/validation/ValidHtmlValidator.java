package fi.vm.sade.eperusteet.ylops.domain.validation;

import fi.vm.sade.eperusteet.ylops.domain.teksti.LokalisoituTeksti;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

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
}
