package fi.vm.sade.eperusteet.ylops.resource.config;

import fi.vm.sade.eperusteet.ylops.domain.Tila;
import fi.vm.sade.eperusteet.ylops.domain.Tyyppi;
import fi.vm.sade.eperusteet.ylops.domain.oppiaine.OppiaineTyyppi;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;

@ControllerAdvice
public class InitBinderControllerAdvice {

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Tyyppi.class, new EnumToUpperCaseEditor<>(Tyyppi.class));
        binder.registerCustomEditor(Tila.class, new EnumToUpperCaseEditor<>(Tila.class));
        binder.registerCustomEditor(OppiaineTyyppi.class, new EnumToUpperCaseEditor<>(OppiaineTyyppi.class));
    }

}
