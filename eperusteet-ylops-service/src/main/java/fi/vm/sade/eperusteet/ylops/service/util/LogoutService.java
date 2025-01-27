package fi.vm.sade.eperusteet.ylops.service.util;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.access.prepost.PreAuthorize;

public interface LogoutService {

    @PreAuthorize("isAuthenticated()")
    String logout(HttpServletRequest request);
    
}
