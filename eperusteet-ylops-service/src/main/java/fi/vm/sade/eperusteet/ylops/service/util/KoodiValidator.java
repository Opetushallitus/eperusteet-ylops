package fi.vm.sade.eperusteet.ylops.service.util;

import fi.vm.sade.eperusteet.ylops.service.exception.BusinessRuleViolationException;

public class KoodiValidator {

    public static void validate(String koodi) {
        if (koodi != null && !koodi.matches("^[a-zA-Z0-9äöåÄÖÅ._-]*$")) {
            throw new BusinessRuleViolationException("koodi-virheellinen");
        }
    }
}
