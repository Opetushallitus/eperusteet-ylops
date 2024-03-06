package fi.vm.sade.eperusteet.ylops.service.security;

import fi.vm.sade.eperusteet.ylops.service.util.SecurityUtil;
import java.io.Serializable;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;

public class PermissionEvaluator implements org.springframework.security.access.PermissionEvaluator {

    static class Organization {

        private final String organization;

        private Organization() {
            this.organization = null;
        }

        Organization(String organization) {
            this.organization = organization;
        }

        public Optional<String> getOrganization() {
            return Optional.ofNullable(organization);
        }

        public static final Organization OPH = new Organization(SecurityUtil.OPH_OID);
        static final Organization ANY = new Organization();
    }

    public enum RolePrefix {
        ROLE_APP_EPERUSTEET_YLOPS,
        ROLE_VIRKAILIJA
    }

    public enum RolePermission {
        CRUD,
        READ_UPDATE,
        READ,
        ADMIN
    }

    @Autowired
    private PermissionManager permissionManager;

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        throw new UnsupportedOperationException("EI TOTEUTETTU");
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        if (authentication == null || authentication.getAuthorities() == null) {
            LOG.error("Virheellinen autentikaatioparametri");
            return false;
        }

        TargetType target = TargetType.valueOf(targetType.toUpperCase());
        Permission perm = Permission.valueOf(permission.toString().toUpperCase());
        return permissionManager.hasPermission(authentication, targetId, target, perm);
    }

    private static final Logger LOG = LoggerFactory.getLogger(PermissionEvaluator.class);
}
