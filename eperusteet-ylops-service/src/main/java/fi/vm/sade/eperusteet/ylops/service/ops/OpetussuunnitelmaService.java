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
package fi.vm.sade.eperusteet.ylops.service.ops;

import com.fasterxml.jackson.databind.JsonNode;
import fi.vm.sade.eperusteet.ylops.domain.Tila;
import fi.vm.sade.eperusteet.ylops.domain.Tyyppi;
import fi.vm.sade.eperusteet.ylops.dto.JarjestysDto;
import fi.vm.sade.eperusteet.ylops.dto.OpetussuunnitelmaExportDto;
import fi.vm.sade.eperusteet.ylops.dto.OppiaineOpintojaksoDto;
import fi.vm.sade.eperusteet.ylops.dto.navigation.NavigationNodeDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.*;
import fi.vm.sade.eperusteet.ylops.dto.peruste.PerusteDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.PerusteInfoDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.PerusteLaajaalainenosaaminenDto;
import fi.vm.sade.eperusteet.ylops.dto.teksti.TekstiKappaleViiteDto;
import fi.vm.sade.eperusteet.ylops.service.util.Validointi;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @author mikkom
 */
public interface OpetussuunnitelmaService {

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    Set<PerusteLaajaalainenosaaminenDto> getLaajaalaisetosaamiset(@P("opsId") Long opsId);

    @PreAuthorize("hasPermission(null, 'tarkastelu', 'HALLINTA') ||" +
            "(#tyyppi == T(fi.vm.sade.eperusteet.ylops.domain.Tyyppi).OPS and (hasPermission(null, 'opetussuunnitelma', 'LUKU'))) || " +
            "(#tyyppi == T(fi.vm.sade.eperusteet.ylops.domain.Tyyppi).POHJA and hasPermission(null, 'pohja', 'LUKU'))")
    List<OpetussuunnitelmaInfoDto> getAll(Tyyppi tyyppi, Tila tila);

    @PreAuthorize("permitAll()")
    Long getAmount(Tyyppi tyyppi, Set<Tila> tila);

    @PreAuthorize("permitAll()")
    List<OpetussuunnitelmaJulkinenDto> getAllJulkiset(OpetussuunnitelmaQuery query);

    @PreAuthorize("permitAll()")
    OpetussuunnitelmaJulkinenDto getOpetussuunnitelmaJulkinen(Long opsId);

    @PreAuthorize("isAuthenticated()")
    List<OpetussuunnitelmaInfoDto> getAll(Tyyppi tyyppi);

    @PreAuthorize("hasPermission(null, 'pohja', 'LUONTI')")
    OpetussuunnitelmaStatistiikkaDto getStatistiikka();

    @PreAuthorize("hasPermission(null, 'pohja', 'LUONTI')")
    List<OpetussuunnitelmaInfoDto> getAdminList();

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    OpetussuunnitelmaKevytDto getOpetussuunnitelma(@P("opsId") Long opsId);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    OpetussuunnitelmaDto getOpetussuunnitelmaKaikki(@P("opsId") Long opsId);

    @PreAuthorize("isAuthenticated()")
    List<OpetussuunnitelmaInfoDto> getOpetussuunnitelmaOpsPohjat();

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    OpetussuunnitelmaLaajaDto getOpetussuunnitelmaEnempi(@P("opsId") Long opsId);

    @PreAuthorize("hasPermission(#ops.id, 'opetussuunnitelma', 'LUKU')")
    void fetchKuntaNimet(@P("ops") OpetussuunnitelmaBaseDto opetussuunnitelmaDto);

    @PreAuthorize("hasPermission(#ops.id, 'opetussuunnitelma', 'LUKU')")
    void fetchOrganisaatioNimet(@P("ops") OpetussuunnitelmaBaseDto opetussuunnitelmaDto);

    @PreAuthorize("hasPermission(null, 'opetussuunnitelma', 'LUONTI')")
    OpetussuunnitelmaDto addOpetussuunnitelma(OpetussuunnitelmaLuontiDto opetussuunnitelmaDto);

    @PreAuthorize("hasPermission(null, 'pohja', 'LUONTI')")
    OpetussuunnitelmaDto addPohja(OpetussuunnitelmaLuontiDto opetussuunnitelmaDto);

    @PreAuthorize("hasPermission(#ops.id, 'opetussuunnitelma', 'POISTO')")
    OpetussuunnitelmaDto updateOpetussuunnitelma(@P("ops") OpetussuunnitelmaDto opetussuunnitelmaDto);

    @PreAuthorize("hasPermission(#id, 'opetussuunnitelma', 'MUOKKAUS')")
    OpetussuunnitelmaDto importPerusteTekstit(@P("id") Long id);

    @PreAuthorize("hasPermission(#id, 'opetussuunnitelma', 'MUOKKAUS')")
    OpetussuunnitelmaDto importPerusteTekstit(@P("id") Long id, boolean skip);

    @PreAuthorize("hasPermission(#id, 'opetussuunnitelma', 'TILANVAIHTO')")
    OpetussuunnitelmaDto updateTila(@P("id") Long id, Tila tila);

    @PreAuthorize("hasPermission(#id, 'opetussuunnitelma', 'HALLINTA')")
    OpetussuunnitelmaDto restore(@P("id") Long id);

    @PreAuthorize("hasPermission(#id, 'opetussuunnitelma', 'MUOKKAUS')")
    List<OpetussuunnitelmaInfoDto> getLapsiOpetussuunnitelmat(@P("id") Long id);

    @PreAuthorize("hasPermission(#id, 'opetussuunnitelma', 'MUOKKAUS')")
    List<Validointi> validoiOpetussuunnitelma(@P("id") Long id);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')")
    void updateLapsiOpetussuunnitelmat(@P("opsId") Long opsId);

    @PreAuthorize("hasPermission(#pohjaId, 'opetussuunnitelma', 'HALLINTA')")
    void syncPohja(@P("pohjaId") Long pohjaId);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'POISTO')")
    void updateOppiainejarjestys(@P("opsId") Long opsId, List<JarjestysDto> oppiainejarjestys);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'POISTO')")
    void updateOppiaineJaOpintojaksojarjestys(
            @P("opsId") Long opsId,
            List<OppiaineOpintojaksoDto> oppiaineopintojaksojarjestys
    );

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    <T> T getTekstit(@P("opsId") final Long opsId, Class<T> t);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')")
    TekstiKappaleViiteDto.Matala addTekstiKappale(@P("opsId") final Long opsId, TekstiKappaleViiteDto.Matala viite);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'MUOKKAUS')")
    TekstiKappaleViiteDto.Matala addTekstiKappaleLapsi(@P("opsId") final Long opsId, final Long parentId,
                                                       TekstiKappaleViiteDto.Matala viite);

    /**
     * Hakee opetussuunnitelmaan liittyvän opetussuunnitelman perusteen
     *
     * @param opsId
     * @return Peruste
     */
    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    PerusteDto getPeruste(@P("opsId") Long opsId);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    PerusteInfoDto getPerusteBase(@P("opsId") Long opsId);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    List<OpetussuunnitelmanJulkaisuDto> getJulkaisut(@P("opsId") Long opsId);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    List<OpetussuunnitelmanJulkaisuDto> getJulkaisutKevyt(@P("opsId") Long opsId);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'TILANVAIHTO')")
    OpetussuunnitelmanJulkaisuDto addJulkaisu(@P("opsId") Long opsId, UusiJulkaisuDto julkaisuDto);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    JsonNode queryOpetussuunnitelmaJulkaisu(@P("opsId") Long opsId, String query);

    @Cacheable("ops-navigation")
    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    <T extends NavigationBuilder> NavigationNodeDto buildNavigationWithDate(@P("opsId") Long opsId, Date pvm, String kieli, Class<T> clazz);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    NavigationNodeDto buildNavigation(@P("opsId") Long opsId, String kieli);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    NavigationNodeDto buildNavigationPublic(@P("opsId") Long opsId, String kieli);

    @PreAuthorize("hasPermission(null, 'pohja', 'LUONTI')")
    void vaihdaPohja(@P("opsId") Long id, Long pohjaId);

    @PreAuthorize("hasPermission(null, 'pohja', 'LUONTI')")
    Set<OpetussuunnitelmaInfoDto> vaihdettavatPohjat(@P("opsId") Long id);

    @PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'LUKU')")
    OpetussuunnitelmaExportDto getExportedOpetussuunnitelma(@P("opsId") Long opsId);
}
