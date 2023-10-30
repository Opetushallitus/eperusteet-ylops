package fi.vm.sade.eperusteet.ylops.resource.util;

import fi.vm.sade.eperusteet.ylops.resource.config.InternalApi;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Optional;

@RestController
@RequestMapping
@Api("Logout")
@InternalApi
public class LogoutController {

    @PostMapping(value = "/logout")
    public void logoutPOST(HttpServletRequest request, HttpServletResponse response) throws IOException {
        deleteCookies(request, response);
    }

    @GetMapping(value = "/logout")
    public void logoutGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        deleteCookies(request, response);

        String url = request.getRequestURL().toString().replace(request.getRequestURI(),"");
        response.sendRedirect(url + "/service-provider-app/saml/logout");
    }

    private static void deleteCookies(HttpServletRequest request, HttpServletResponse response) {
        Optional.ofNullable(request.getSession(false)).ifPresent(HttpSession::invalidate);
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                cookie.setValue("");
                cookie.setPath("/");
                cookie.setMaxAge(0);
                response.addCookie(cookie);
            }
        }
    }
}
