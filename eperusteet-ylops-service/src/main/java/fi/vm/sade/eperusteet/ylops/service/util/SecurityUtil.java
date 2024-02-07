package fi.vm.sade.eperusteet.ylops.service.util;

import fi.vm.sade.eperusteet.ylops.service.security.PermissionEvaluator.RolePermission;
import fi.vm.sade.eperusteet.ylops.service.security.PermissionEvaluator.RolePrefix;
import java.security.Principal;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityUtil {

    public static final String OPH_OID = "1.2.246.562.10.00000000001";
    public static final String OPH_ADMIN = "ROLE_APP_EPERUSTEET_YLOPS_ADMIN_1.2.246.562.10.00000000001";

    private SecurityUtil() {
        //helper class
    }

    public static Principal getAuthenticatedPrincipal() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    public static void allow(String principalName) {
        Principal p = getAuthenticatedPrincipal();
        if (p == null || !p.getName().equals(principalName)) {
            throw new AccessDeniedException("Pääsy evätty");
        }
    }

    public static Set<String> getOrganizations(Set<RolePermission> permissions) {
        return getOrganizations(SecurityContextHolder.getContext().getAuthentication(), permissions);
    }

    public static Set<String> getOrganizations(Authentication authentication, Set<RolePermission> permissions) {
        return authentication.getAuthorities().stream()
                .map(grantedAuthority -> parseOid(grantedAuthority.getAuthority(),
                        RolePrefix.ROLE_APP_EPERUSTEET_YLOPS,
                        permissions))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());
    }

    public static Set<String> getOrganizations() {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter((String auth) -> {
                    return auth.startsWith(RolePrefix.ROLE_APP_EPERUSTEET_YLOPS.name());
                })
                .collect(Collectors.toSet());
    }

    private static Optional<String> parseOid(String authority, RolePrefix prefix, Set<RolePermission> permissions) {
        return permissions.stream()
                .map(p -> {
                    String authPrefix = prefix.name() + "_" + p.name() + "_";
                    return authority.startsWith(authPrefix) ?
                            Optional.of(authority.substring(authPrefix.length())) : Optional.<String>empty();
                })
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findAny();
    }

    public static boolean isAuthenticated() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null
                && !(authentication instanceof AnonymousAuthenticationToken)
                && authentication.isAuthenticated();
    }

    public static boolean isUserAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).anyMatch(authority -> authority.equals(OPH_ADMIN));
    }
}
