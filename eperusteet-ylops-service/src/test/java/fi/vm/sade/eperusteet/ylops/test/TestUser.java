package fi.vm.sade.eperusteet.ylops.test;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;

import java.util.List;

/**
 * Two-arg {@link UsernamePasswordAuthenticationToken} is unauthenticated in Spring Security 7.
 * Authorities match {@code it-test-context.xml} in-memory users.
 */
public final class TestUser {

    private TestUser() {
    }

    public static UsernamePasswordAuthenticationToken authenticated(String username) {
        return new UsernamePasswordAuthenticationToken(username, "test", authoritiesFor(username));
    }

    private static List<GrantedAuthority> authoritiesFor(String username) {
        if ("testAdmin".equals(username)) {
            return AuthorityUtils.createAuthorityList(
                    "ROLE_USER",
                    "ROLE_APP_EPERUSTEET_YLOPS",
                    "ROLE_APP_EPERUSTEET_YLOPS_CRUD",
                    "ROLE_APP_EPERUSTEET_YLOPS_ADMIN_1.2.246.562.10.00000000001");
        }
        if ("test8".equals(username)) {
            return AuthorityUtils.createAuthorityList(
                    "ROLE_USER",
                    "ROLE_APP_EPERUSTEET_YLOPS",
                    "ROLE_APP_EPERUSTEET_YLOPS_CRUD",
                    "ROLE_APP_EPERUSTEET_YLOPS_CRUD_1.2.246.562.10.83037752777",
                    "ROLE_APP_EPERUSTEET_YLOPS_CRUD_1.2.246.562.10.83037752778");
        }
        if ("test".equals(username)) {
            return AuthorityUtils.createAuthorityList(
                    "ROLE_USER",
                    "ROLE_APP_EPERUSTEET_YLOPS",
                    "ROLE_APP_EPERUSTEET_YLOPS_CRUD",
                    "ROLE_APP_EPERUSTEET_YLOPS_CRUD_1.2.246.562.10.00000000001",
                    "ROLE_APP_EPERUSTEET_YLOPS_CRUD_1.2.246.562.10.83037752777",
                    "ROLE_APP_EPERUSTEET_YLOPS_CRUD_1.2.15252345624572462");
        }
        return AuthorityUtils.createAuthorityList("ROLE_USER");
    }
}
