package fi.vm.sade.eperusteet.ylops.service.util.impl;

import fi.vm.sade.eperusteet.ylops.service.util.LogoutService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LogoutServiceImpl implements LogoutService {

    @Override
    public String logout(HttpServletRequest request) {
        Optional.ofNullable(request.getSession(false)).ifPresent(HttpSession::invalidate);
        return "/service-provider-app/saml/logout";
    }
}
