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
package fi.vm.sade.eperusteet.ylops.service.external.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Sets;
import fi.vm.sade.eperusteet.utils.client.OphClientHelper;
import fi.vm.sade.eperusteet.ylops.domain.Tila;
import fi.vm.sade.eperusteet.ylops.domain.Tyyppi;
import fi.vm.sade.eperusteet.ylops.dto.kayttaja.EtusivuDto;
import fi.vm.sade.eperusteet.utils.client.RestClientFactory;
import fi.vm.sade.eperusteet.ylops.dto.kayttaja.KayttajanProjektitiedotDto;
import fi.vm.sade.eperusteet.ylops.dto.kayttaja.KayttajanTietoDto;
import fi.vm.sade.eperusteet.ylops.service.external.KayttajaClient;
import fi.vm.sade.eperusteet.ylops.service.external.KayttajanTietoService;
import fi.vm.sade.eperusteet.ylops.service.ops.OpetussuunnitelmaService;

import fi.vm.sade.eperusteet.ylops.service.util.SecurityUtil;
import fi.vm.sade.javautils.http.OphHttpClient;
import fi.vm.sade.javautils.http.OphHttpRequest;
import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Future;
import org.apache.commons.lang.NotImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.*;
import static fi.vm.sade.eperusteet.ylops.service.external.impl.KayttajanTietoParser.parsiKayttaja;
import static fi.vm.sade.eperusteet.ylops.service.security.PermissionEvaluator.RolePermission.ADMIN;
import static fi.vm.sade.eperusteet.ylops.service.security.PermissionEvaluator.RolePermission.CRUD;
import static javax.servlet.http.HttpServletResponse.SC_FORBIDDEN;
import static javax.servlet.http.HttpServletResponse.SC_OK;
import static javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;

/**
 * @author mikkom
 */
@Service
public class KayttajanTietoServiceImpl implements KayttajanTietoService {

    @Autowired
    private OpetussuunnitelmaService opetussuunnitelmaService;

    @Autowired
    private KayttajaClient client;

    @Override
    public KayttajanTietoDto hae(String oid) {
        return client.hae(oid);
    }

    @Override
    public String haeKayttajanimi(String oid) {
        if (oid != null) {
            KayttajanTietoDto tiedot = client.hae(oid);
            if (tiedot != null && tiedot.getKutsumanimi() != null && tiedot.getSukunimi() != null) {
                return tiedot.getKutsumanimi() + " " + tiedot.getSukunimi();
            }
        }
        return oid;
    }

    @Override
    @Async
    public Future<KayttajanTietoDto> haeAsync(String oid) {
        return new AsyncResult<>(hae(oid));
    }

    @Override
    public KayttajanTietoDto haeKirjautaunutKayttaja() {
        Principal ap = SecurityUtil.getAuthenticatedPrincipal();
        KayttajanTietoDto kayttaja = hae(ap.getName());
        if (kayttaja == null) { //"fallback" jos integraatio on rikki eikä löydä käyttäjän tietoja
            kayttaja = new KayttajanTietoDto(ap.getName());
        }
        return kayttaja;
    }

    @Override
    public List<KayttajanProjektitiedotDto> haeOpetussuunnitelmat(String oid) {
        // TODO
        throw new NotImplementedException();
    }

    @Override
    public KayttajanProjektitiedotDto haeOpetussuunnitelma(String oid, Long opsId) {
        // TODO
        throw new NotImplementedException();
    }

    @Override
    public EtusivuDto haeKayttajanEtusivu() {
        EtusivuDto result = new EtusivuDto();
        result.setOpetussuunnitelmatKeskeneraiset(opetussuunnitelmaService.getAmount(Tyyppi.OPS, Tila.julkaisemattomat()));
        result.setOpetussuunnitelmatJulkaistut(opetussuunnitelmaService.getAmount(Tyyppi.OPS, Tila.julkiset()));
        result.setPohjatKeskeneraiset(opetussuunnitelmaService.getAmount(Tyyppi.POHJA, Sets.newHashSet(Tila.LUONNOS)));
        result.setPohjatJulkaistut(opetussuunnitelmaService.getAmount(Tyyppi.POHJA, Sets.newHashSet(Tila.VALMIS)));
        return result;
    }

    @Override
    public Set<String> haeOrganisaatioOikeudet() {
        return SecurityUtil.getOrganizations(new HashSet<>(Arrays.asList(ADMIN, CRUD)));
    }

}
