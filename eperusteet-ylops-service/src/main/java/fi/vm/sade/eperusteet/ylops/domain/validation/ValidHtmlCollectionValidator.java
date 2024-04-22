package fi.vm.sade.eperusteet.ylops.domain.validation;

import fi.vm.sade.eperusteet.ylops.domain.teksti.LokalisoituTeksti;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Collection;

public class ValidHtmlCollectionValidator extends ValidHtmlValidatorBase implements
        ConstraintValidator<ValidHtml, Collection<LokalisoituTeksti>> {
    @Override
    public void initialize(ValidHtml constraintAnnotation) {
        setupValidator(constraintAnnotation);
    }

    @Override
    public boolean isValid(Collection<LokalisoituTeksti> value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        return value.stream().allMatch(this::isValid);
    }
}
