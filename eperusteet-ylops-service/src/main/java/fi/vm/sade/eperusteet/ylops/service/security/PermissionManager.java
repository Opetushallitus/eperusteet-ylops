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

import fi.vm.sade.eperusteet.ylops.domain.Tila;
import fi.vm.sade.eperusteet.ylops.domain.Tyyppi;
import fi.vm.sade.eperusteet.ylops.domain.ops.Opetussuunnitelma;
import fi.vm.sade.eperusteet.ylops.domain.ukk.Kysymys;
import fi.vm.sade.eperusteet.ylops.dto.koodisto.OrganisaatioDto;
import fi.vm.sade.eperusteet.ylops.dto.ukk.KysymysDto;
import fi.vm.sade.eperusteet.ylops.repository.ops.OpetussuunnitelmaRepository;
import fi.vm.sade.eperusteet.ylops.repository.ukk.KysymysRepository;
import fi.vm.sade.eperusteet.ylops.service.exception.NotExistsException;
import fi.vm.sade.eperusteet.ylops.service.security.PermissionEvaluator.Organization;
import fi.vm.sade.eperusteet.ylops.service.security.PermissionEvaluator.RolePermission;
import fi.vm.sade.eperusteet.ylops.service.security.PermissionEvaluator.RolePrefix;
import fi.vm.sade.eperusteet.ylops.service.util.CollectionUtil;
import fi.vm.sade.eperusteet.ylops.service.util.Pair;
import fi.vm.sade.eperusteet.ylops.service.util.SecurityUtil;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author mikkom
 */
@Service
public class PermissionManager {

    public enum TargetType {

        POHJA("pohja"),
        TARKASTELU("tarkastelu"),
        KYSYMYS("kysymys"),
        OPETUSSUUNNITELMA("opetussuunnitelma");

        private final String target;

        private TargetType(String target) {
            this.target = target;
        }

        @Override
        public String toString() {
            return target;
        }
    }

    public enum Permission {

        LUKU("luku"),
        MUOKKAUS("muokkaus"),
        KOMMENTOINTI("kommentointi"),
        LUONTI("luonti"),
        POISTO("poisto"),
        TILANVAIHTO("tilanvaihto"),
        HALLINTA("hallinta");

        private final String permission;

        private Permission(String permission) {
            this.permission = permission;
        }

        @Override
        public String toString() {
            return permission;
        }
    }

    private static final String MSG_OPS_EI_OLEMASSA = "Pyydettyä opetussuunnitelmaa ei ole olemassa";

    @Autowired
    private OpetussuunnitelmaRepository opetussuunnitelmaRepository;

    @Autowired
    private KysymysRepository kysymysRepository;

    @Autowired
    private Environment env;

    @Transactional(readOnly = true)
    public boolean hasPermission(Authentication authentication, Serializable targetId, TargetType target,
                                 Permission perm) {

        if (Arrays.stream(env.getActiveProfiles()).anyMatch(profile -> profile.equals("local"))) {
            return true;
        }

        if (perm == Permission.HALLINTA && targetId == null && target == TargetType.TARKASTELU &&
                hasRole(authentication, RolePrefix.ROLE_APP_EPERUSTEET_YLOPS, RolePermission.CRUD, Organization.OPH)) {
            return true;
        }

        Pair<Tyyppi, Tila> tyyppiJaTila = targetId instanceof Long
                ? opetussuunnitelmaRepository.findTyyppiAndTila((long) targetId)
                : null;

        if (perm == Permission.LUKU && tyyppiJaTila != null) {
            if (tyyppiJaTila.getSecond() == Tila.JULKAISTU) {
                return true;
            } else if (opetussuunnitelmaRepository.isEsikatseltavissa((long) targetId)) {
                return true;
            }
        }

        // Salli valmiiden pohjien lukeminen kaikilta joilla on CRUD-oikeus tai ADMIN-oikeus
        if (perm == Permission.LUKU && targetId != null &&
                (hasRole(authentication, RolePrefix.ROLE_APP_EPERUSTEET_YLOPS, RolePermission.CRUD, Organization.ANY) ||
                        hasRole(authentication, RolePrefix.ROLE_APP_EPERUSTEET_YLOPS, RolePermission.ADMIN, Organization.ANY))) {
            if (tyyppiJaTila == null) {
                throw new NotExistsException();
            }
            if (tyyppiJaTila.getFirst() == Tyyppi.POHJA && tyyppiJaTila.getSecond() == Tila.VALMIS) {
                return true;
            }
        }

        Set<RolePermission> permissions;
        switch (perm) {
            case LUKU:
            case KOMMENTOINTI:
                permissions = EnumSet.allOf(RolePermission.class);
                break;
            case TILANVAIHTO:
            case LUONTI:
            case POISTO:
                permissions = EnumSet.of(RolePermission.CRUD, RolePermission.ADMIN);
                break;
            case MUOKKAUS:
                permissions = EnumSet.of(RolePermission.CRUD, RolePermission.READ_UPDATE, RolePermission.ADMIN);
                break;
            case HALLINTA:
                permissions = EnumSet.of(RolePermission.ADMIN);
                break;
            default:
                permissions = EnumSet.noneOf(RolePermission.class);
                break;
        }

        switch (target) {
            case POHJA:
            case OPETUSSUUNNITELMA:
                if (targetId != null) {
                    List<String> opsOrganisaatiot = opetussuunnitelmaRepository.findOrganisaatiot((Long) targetId);
                    if (opsOrganisaatiot.isEmpty()) {
                        throw new NotExistsException(MSG_OPS_EI_OLEMASSA);
                    }
                    Set<String> kayttajaOrganisaatiot = SecurityUtil.getOrganizations(authentication, permissions);
                    return !CollectionUtil.intersect(opsOrganisaatiot, kayttajaOrganisaatiot).isEmpty();
                } else {
                    return hasAnyRole(authentication, RolePrefix.ROLE_APP_EPERUSTEET_YLOPS,
                            permissions, Organization.ANY);
                }
            case KYSYMYS:
                Set<String> kayttajaOrganisaatiot = SecurityUtil.getOrganizations(authentication, permissions);
                // Uuden kysymyksen luominen
                if (targetId instanceof KysymysDto) {
                    KysymysDto dto = (KysymysDto) targetId;
                    Set<String> oids = dto.getOrganisaatiot().stream()
                            .map(OrganisaatioDto::getOid)
                            .collect(Collectors.toSet());
                    // Jos käyttäjällä on oikeus kaikkiin liitettyihin organisaatioihin, sallitaan operaatio
                    return CollectionUtil.intersect(oids, kayttajaOrganisaatiot).size() == oids.size();
                }

                // Olemassa olevan kysymyksen päivittäminen ja poistaminen
                if (targetId instanceof Long) {
                    long kysymysId = (long) targetId;
                    Kysymys kysymys = kysymysRepository.findOne(kysymysId);
                    if (kysymys != null) {
                        Set<String> oids = kysymys.getOrganisaatiot();
                        // Jos käyttäjällä on oikeus kaikkiin liitettyihin organisaatioihin, sallitaan operaatio
                        return CollectionUtil.intersect(oids, kayttajaOrganisaatiot).size() == oids.size();
                    }
                }

                return false;
            default:
                return hasAnyRole(authentication, RolePrefix.ROLE_APP_EPERUSTEET_YLOPS,
                        permissions, Organization.ANY);
        }
    }

    private static boolean hasRole(Authentication authentication, RolePrefix prefix,
                                   RolePermission permission, Organization org) {
        return hasAnyRole(authentication, prefix, Collections.singleton(permission), org);
    }

    private static boolean hasAnyRole(Authentication authentication, RolePrefix prefix,
                                      Set<RolePermission> permission, Organization org) {
        return authentication.getAuthorities().stream()
                .anyMatch(a -> permission.stream().anyMatch(p -> roleEquals(a.getAuthority(), prefix, p, org)));
    }

    private static boolean roleEquals(String authority, RolePrefix prefix,
                                      RolePermission permission, Organization org) {
        if (Organization.ANY.equals(org)) {
            return authority.equals(prefix.name() + "_" + permission.name());
        }
        return authority.equals(prefix.name() + "_" + permission.name() + "_" + org.getOrganization().get());
    }

    @Transactional(readOnly = true)
    @PreAuthorize("isAuthenticated()")
    public Map<TargetType, Set<Permission>> getOpsPermissions() {

        Map<TargetType, Set<Permission>> permissionMap = new HashMap<>();
        Set<Permission> opsPermissions =
                EnumSet.allOf(RolePermission.class).stream()
                        .map(p -> new Pair<>(p, SecurityUtil.getOrganizations(Collections.singleton(p))))
                        .filter(pair -> !pair.getSecond().isEmpty())
                        .flatMap(pair -> fromRolePermission(pair.getFirst()).stream())
                        .collect(Collectors.toSet());
        permissionMap.put(TargetType.OPETUSSUUNNITELMA, opsPermissions);

        Set<Permission> pohjaPermissions =
                EnumSet.allOf(RolePermission.class).stream()
                        .map(p -> new Pair<>(p, SecurityUtil.getOrganizations(Collections.singleton(p))))
                        .filter(pair -> pair.getSecond().contains(SecurityUtil.OPH_OID))
                        .flatMap(pair -> fromRolePermission(pair.getFirst()).stream())
                        .collect(Collectors.toSet());
        permissionMap.put(TargetType.POHJA, pohjaPermissions);

        return permissionMap;
    }

    @Transactional(readOnly = true)
    @PreAuthorize("isAuthenticated()")
    public Map<TargetType, Set<Permission>> getOpsPermissions(Long id) {

        Map<TargetType, Set<Permission>> permissionMap = new HashMap<>();
        Opetussuunnitelma ops = opetussuunnitelmaRepository.findOne(id);
        if (ops == null) {
            throw new NotExistsException(MSG_OPS_EI_OLEMASSA);
        }
        boolean isPohja = ops.getTyyppi() == Tyyppi.POHJA;
        Set<String> organisaatiot = ops.getOrganisaatiot();
        Set<Permission> permissions
                = EnumSet.allOf(RolePermission.class).stream()
                .map(p -> new Pair<>(p, SecurityUtil.getOrganizations(Collections.singleton(p))))
                .filter(pair -> !CollectionUtil.intersect(pair.getSecond(), organisaatiot).isEmpty())
                .flatMap(pair -> fromRolePermission(pair.getFirst()).stream())
                .filter(permission -> ops.getTila() == Tila.LUONNOS ||
                        (permission == Permission.TILANVAIHTO &&
                                !ops.getTila().mahdollisetSiirtymat(isPohja).isEmpty()) ||
                        fromRolePermission(RolePermission.READ).contains(permission))
                .collect(Collectors.toSet());

        permissionMap.put(TargetType.OPETUSSUUNNITELMA, permissions);
        if (ops.getTyyppi() == Tyyppi.POHJA) {
            permissionMap.put(TargetType.POHJA, permissions);
        }

        return permissionMap;
    }

    private static Set<Permission> fromRolePermission(RolePermission rolePermission) {
        Set<Permission> permissions = new HashSet<>();
        switch (rolePermission) {
            case ADMIN:
                permissions.add(Permission.HALLINTA);
            case CRUD:
                permissions.add(Permission.LUONTI);
                permissions.add(Permission.POISTO);
                permissions.add(Permission.TILANVAIHTO);
            case READ_UPDATE:
                permissions.add(Permission.LUKU);
                permissions.add(Permission.MUOKKAUS);
            case READ:
                permissions.add(Permission.LUKU);
                permissions.add(Permission.KOMMENTOINTI);
                break;
        }
        return permissions;
    }
}
