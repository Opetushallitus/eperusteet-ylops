package fi.vm.sade.eperusteet.ylops.resource.util;

import fi.vm.sade.eperusteet.ylops.resource.config.InternalApi;
import fi.vm.sade.eperusteet.ylops.service.util.LogoutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;

@InternalApi
@Controller
public class LogoutController {

    @Autowired
    private LogoutService logoutService;

    @GetMapping("/logout")
    public View logout(HttpServletRequest request) {
        return new RedirectView(logoutService.logout(request));
    }
}
