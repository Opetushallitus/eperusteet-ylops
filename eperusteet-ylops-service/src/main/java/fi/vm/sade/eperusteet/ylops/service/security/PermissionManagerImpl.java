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
import fi.vm.sade.eperusteet.ylops.repository.ops.JulkaisuRepository;
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
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author mikkom
 */
@Profile("!developmentPermissionOverride")
@Service
public class PermissionManagerImpl extends AbstractPermissionManager {

    @Autowired
    private JulkaisuRepository julkaisuRepository;

    @Transactional(readOnly = true)
    public boolean hasPermission(Authentication authentication, Serializable targetId, TargetType target,
                                 Permission perm) {

        if (perm == Permission.HALLINTA && targetId == null && target == TargetType.TARKASTELU &&
                hasRole(authentication, RolePrefix.ROLE_APP_EPERUSTEET_YLOPS, RolePermission.CRUD, Organization.OPH)) {
            return true;
        }

        Pair<Tyyppi, Tila> tyyppiJaTila = targetId instanceof Long
                ? opetussuunnitelmaRepository.findTyyppiAndTila((long) targetId)
                : null;

        if (perm == Permission.LUKU && tyyppiJaTila != null) {
            if (tyyppiJaTila.getSecond() == Tila.JULKAISTU || julkaisuRepository.countByOpetussuunnitelmaId((long) targetId) > 0) {
                return true;
            } else if (opetussuunnitelmaRepository.isEsikatseltavissa((long) targetId)) {
                return true;
            } else if (SecurityUtil.isUserAdmin()) {
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

}
