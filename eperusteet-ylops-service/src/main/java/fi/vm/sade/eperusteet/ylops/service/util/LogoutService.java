package fi.vm.sade.eperusteet.ylops.service.util;

import org.springframework.security.access.prepost.PreAuthorize;

import javax.servlet.http.HttpServletRequest;

public interface LogoutService {

    @PreAuthorize("isAuthenticated()")
    String logout(HttpServletRequest request);
    
}
