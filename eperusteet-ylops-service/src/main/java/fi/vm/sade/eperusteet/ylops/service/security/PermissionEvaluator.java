/*
 * Copyright (c) 2013 The Finnish Board of Education - Opetushallitus
 *
 * This program is free software: Licensed under the EUPL, Version 1.1 or - as
 * soon as they will be approved by the European Commission - subsequent versions
 * of the EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at: http://ec.europa.eu/idabc/eupl
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * European Union Public Licence for more details.
 */
package fi.vm.sade.eperusteet.ylops.service.security;

import fi.vm.sade.eperusteet.ylops.service.util.SecurityUtil;
import java.io.Serializable;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;

/**
 * Oikeuksien tarkistelu.
 *
 * @author jhyoty
 */
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
