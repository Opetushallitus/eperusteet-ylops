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
package fi.vm.sade.eperusteet.ylops.service.ops.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Sets;
import fi.vm.sade.eperusteet.ylops.domain.KoulutusTyyppi;
import fi.vm.sade.eperusteet.ylops.domain.KoulutustyyppiToteutus;
import fi.vm.sade.eperusteet.ylops.domain.MuokkausTapahtuma;
import fi.vm.sade.eperusteet.ylops.domain.Tila;
import fi.vm.sade.eperusteet.ylops.domain.Tyyppi;
import fi.vm.sade.eperusteet.ylops.domain.Vuosiluokkakokonaisuusviite;
import fi.vm.sade.eperusteet.ylops.domain.cache.PerusteCache;
import fi.vm.sade.eperusteet.ylops.domain.cache.PerusteCache_;
import fi.vm.sade.eperusteet.ylops.domain.lops2019.Lops2019Opintojakso;
import fi.vm.sade.eperusteet.ylops.domain.lops2019.Lops2019OpintojaksonOppiaine;
import fi.vm.sade.eperusteet.ylops.domain.lops2019.Lops2019OppiaineJarjestys;
import fi.vm.sade.eperusteet.ylops.domain.lops2019.Lops2019Sisalto;
import fi.vm.sade.eperusteet.ylops.domain.lukio.Aihekokonaisuudet;
import fi.vm.sade.eperusteet.ylops.domain.lukio.Aihekokonaisuus;
import fi.vm.sade.eperusteet.ylops.domain.lukio.Kurssi;
import fi.vm.sade.eperusteet.ylops.domain.lukio.LukioOppiaineJarjestys;
import fi.vm.sade.eperusteet.ylops.domain.lukio.Lukiokurssi;
import fi.vm.sade.eperusteet.ylops.domain.lukio.LukiokurssiTyyppi;
import fi.vm.sade.eperusteet.ylops.domain.lukio.OpetuksenYleisetTavoitteet;
import fi.vm.sade.eperusteet.ylops.domain.lukio.OppiaineLukiokurssi;
import fi.vm.sade.eperusteet.ylops.domain.ohje.Ohje;
import fi.vm.sade.eperusteet.ylops.domain.oppiaine.Opetuksentavoite;
import fi.vm.sade.eperusteet.ylops.domain.oppiaine.Oppiaine;
import fi.vm.sade.eperusteet.ylops.domain.oppiaine.OppiaineTyyppi;
import fi.vm.sade.eperusteet.ylops.domain.oppiaine.Oppiaineenvuosiluokka;
import fi.vm.sade.eperusteet.ylops.domain.oppiaine.Oppiaineenvuosiluokkakokonaisuus;
import fi.vm.sade.eperusteet.ylops.domain.ops.JulkaistuOpetussuunnitelmaData;
import fi.vm.sade.eperusteet.ylops.domain.ops.Opetussuunnitelma;
import fi.vm.sade.eperusteet.ylops.domain.ops.Opetussuunnitelma_;
import fi.vm.sade.eperusteet.ylops.domain.ops.OpetussuunnitelmanJulkaisu;
import fi.vm.sade.eperusteet.ylops.domain.ops.OpetussuunnitelmanJulkaisu_;
import fi.vm.sade.eperusteet.ylops.domain.ops.OpsOppiaine;
import fi.vm.sade.eperusteet.ylops.domain.ops.OpsVuosiluokkakokonaisuus;
import fi.vm.sade.eperusteet.ylops.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.ylops.domain.teksti.LokalisoituTeksti;
import fi.vm.sade.eperusteet.ylops.domain.teksti.Omistussuhde;
import fi.vm.sade.eperusteet.ylops.domain.teksti.TekstiKappale;
import fi.vm.sade.eperusteet.ylops.domain.teksti.TekstiKappaleViite;
import fi.vm.sade.eperusteet.ylops.domain.vuosiluokkakokonaisuus.Vuosiluokkakokonaisuus;
import fi.vm.sade.eperusteet.ylops.dto.JarjestysDto;
import fi.vm.sade.eperusteet.ylops.dto.OpetussuunnitelmaExportDto;
import fi.vm.sade.eperusteet.ylops.dto.OppiaineOpintojaksoDto;
import fi.vm.sade.eperusteet.ylops.dto.Reference;
import fi.vm.sade.eperusteet.ylops.dto.dokumentti.DokumenttiDto;
import fi.vm.sade.eperusteet.ylops.dto.kayttaja.KayttajanTietoDto;
import fi.vm.sade.eperusteet.ylops.dto.koodisto.KoodistoDto;
import fi.vm.sade.eperusteet.ylops.dto.koodisto.KoodistoKoodiDto;
import fi.vm.sade.eperusteet.ylops.dto.koodisto.KoodistoMetadataDto;
import fi.vm.sade.eperusteet.ylops.dto.koodisto.OrganisaatioDto;
import fi.vm.sade.eperusteet.ylops.dto.koodisto.OrganisaatioLaajaDto;
import fi.vm.sade.eperusteet.ylops.dto.lops2019.Lops2019OpintojaksoDto;
import fi.vm.sade.eperusteet.ylops.dto.lops2019.Lops2019PaikallinenOppiaineDto;
import fi.vm.sade.eperusteet.ylops.dto.lops2019.Validointi.Lops2019ValidointiDto;
import fi.vm.sade.eperusteet.ylops.dto.lukio.LukioAbstraktiOppiaineTuontiDto;
import fi.vm.sade.eperusteet.ylops.dto.navigation.NavigationNodeDto;
import fi.vm.sade.eperusteet.ylops.dto.navigation.NavigationType;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpetussuunnitelmaBaseDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpetussuunnitelmaDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpetussuunnitelmaInfoDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpetussuunnitelmaJulkaisuKevyt;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpetussuunnitelmaJulkinenDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpetussuunnitelmaKevytDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpetussuunnitelmaLuontiDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpetussuunnitelmaNimiDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpetussuunnitelmaQuery;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpetussuunnitelmaStatistiikkaDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpetussuunnitelmanJulkaisuDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.UusiJulkaisuDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.PerusopetuksenPerusteenSisaltoDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.PerusteDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.PerusteInfoDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.PerusteLaajaalainenosaaminenDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.PerusteTekstiKappaleViiteDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.lukio.AihekokonaisuudetDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.lukio.AihekokonaisuusDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.lukio.LukioOpetussuunnitelmaRakenneDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.lukio.LukioPerusteOppiaineDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.lukio.LukiokoulutuksenPerusteenSisaltoDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.lukio.LukiokurssiPerusteDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.lukio.OpetuksenYleisetTavoitteetDto;
import fi.vm.sade.eperusteet.ylops.dto.teksti.LokalisoituTekstiDto;
import fi.vm.sade.eperusteet.ylops.dto.teksti.TekstiKappaleDto;
import fi.vm.sade.eperusteet.ylops.dto.teksti.TekstiKappaleViiteDto;
import fi.vm.sade.eperusteet.ylops.repository.JulkaisuRepositoryCustom;
import fi.vm.sade.eperusteet.ylops.repository.cache.PerusteCacheRepository;
import fi.vm.sade.eperusteet.ylops.repository.lops2019.Lops2019OpintojaksoRepository;
import fi.vm.sade.eperusteet.ylops.repository.lops2019.Lops2019OppiaineJarjestysRepository;
import fi.vm.sade.eperusteet.ylops.repository.ohje.OhjeRepository;
import fi.vm.sade.eperusteet.ylops.repository.ops.JulkaistuOpetussuunnitelmaDataRepository;
import fi.vm.sade.eperusteet.ylops.repository.ops.JulkaisuRepository;
import fi.vm.sade.eperusteet.ylops.repository.ops.OpetussuunnitelmaRepository;
import fi.vm.sade.eperusteet.ylops.repository.ops.VuosiluokkakokonaisuusviiteRepository;
import fi.vm.sade.eperusteet.ylops.repository.teksti.TekstiKappaleRepository;
import fi.vm.sade.eperusteet.ylops.repository.teksti.TekstikappaleviiteRepository;
import fi.vm.sade.eperusteet.ylops.service.dokumentti.DokumenttiService;
import fi.vm.sade.eperusteet.ylops.service.exception.BusinessRuleViolationException;
import fi.vm.sade.eperusteet.ylops.service.exception.DokumenttiException;
import fi.vm.sade.eperusteet.ylops.service.exception.NotExistsException;
import fi.vm.sade.eperusteet.ylops.service.external.EperusteetService;
import fi.vm.sade.eperusteet.ylops.service.external.KayttajanTietoService;
import fi.vm.sade.eperusteet.ylops.service.external.KoodistoService;
import fi.vm.sade.eperusteet.ylops.service.external.OrganisaatioService;
import fi.vm.sade.eperusteet.ylops.service.lops2019.Lops2019OpintojaksoService;
import fi.vm.sade.eperusteet.ylops.service.lops2019.Lops2019OppiaineService;
import fi.vm.sade.eperusteet.ylops.service.lops2019.Lops2019Service;
import fi.vm.sade.eperusteet.ylops.service.mapping.DtoMapper;
import fi.vm.sade.eperusteet.ylops.service.ops.NavigationBuilder;
import fi.vm.sade.eperusteet.ylops.service.ops.NavigationBuilderPublic;
import fi.vm.sade.eperusteet.ylops.service.ops.OpetussuunnitelmaService;
import fi.vm.sade.eperusteet.ylops.service.ops.OpetussuunnitelmanMuokkaustietoService;
import fi.vm.sade.eperusteet.ylops.service.ops.OppiaineService;
import fi.vm.sade.eperusteet.ylops.service.ops.OpsDispatcher;
import fi.vm.sade.eperusteet.ylops.service.ops.OpsExport;
import fi.vm.sade.eperusteet.ylops.service.ops.OpsPohjanVaihto;
import fi.vm.sade.eperusteet.ylops.service.ops.TekstiKappaleViiteService;
import fi.vm.sade.eperusteet.ylops.service.ops.ValidointiService;
import fi.vm.sade.eperusteet.ylops.service.ops.VuosiluokkakokonaisuusService;
import fi.vm.sade.eperusteet.ylops.service.ops.lukio.LukioOpetussuunnitelmaService;
import fi.vm.sade.eperusteet.ylops.service.security.PermissionEvaluator.RolePermission;
import fi.vm.sade.eperusteet.ylops.service.teksti.KommenttiService;
import fi.vm.sade.eperusteet.ylops.service.util.CollectionUtil;
import fi.vm.sade.eperusteet.ylops.service.util.Jarjestetty;
import fi.vm.sade.eperusteet.ylops.service.util.JsonMapper;
import fi.vm.sade.eperusteet.ylops.service.util.LambdaUtil.ConstructedCopier;
import fi.vm.sade.eperusteet.ylops.service.util.LambdaUtil.Copier;
import fi.vm.sade.eperusteet.ylops.service.util.SecurityUtil;
import fi.vm.sade.eperusteet.ylops.service.util.Validointi;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.Stack;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import static fi.vm.sade.eperusteet.ylops.service.util.Nulls.assertExists;
import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

/**
 * @author mikkom
 */
@Service
@Transactional
public class OpetussuunnitelmaServiceImpl implements OpetussuunnitelmaService {

    static private final Logger logger = LoggerFactory.getLogger(OpetussuunnitelmaServiceImpl.class);

    @Autowired
    private DtoMapper mapper;

    @Autowired
    private OpetussuunnitelmaRepository opetussuunnitelmaRepository;

    @Autowired
    private TekstikappaleviiteRepository viiteRepository;

    @Autowired
    private TekstiKappaleRepository tekstiKappaleRepository;

    @Autowired
    private ValidointiService validointiService;

    @Autowired
    private TekstiKappaleViiteService tekstiKappaleViiteService;

    @Autowired
    private OppiaineService oppiaineService;

    @Autowired
    private JulkaisuRepositoryCustom julkaisuRepositoryCustom;

    @Autowired
    private KoodistoService koodistoService;

    @Autowired
    private OrganisaatioService organisaatioService;

    @Autowired
    private JulkaisuRepository julkaisuRepository;

    @Autowired
    private JulkaistuOpetussuunnitelmaDataRepository julkaistuOpetussuunnitelmaDataRepository;

    @Autowired
    private KommenttiService kommenttiService;

    @Autowired
    private VuosiluokkakokonaisuusService vuosiluokkakokonaisuudet;

    @Autowired
    private EperusteetService eperusteetService;

    @Autowired
    private VuosiluokkakokonaisuusviiteRepository vuosiluokkakokonaisuusviiteRepository;

    @Autowired
    private OhjeRepository ohjeRepository;

    @Autowired
    private PerusteCacheRepository perusteCacheRepository;

    @Autowired
    private LukioOpetussuunnitelmaService lukioOpetussuunnitelmaService;

    @Autowired
    private DokumenttiService dokumenttiService;

    @Autowired
    private JsonMapper jsonMapper;

    @Autowired
    private OpsDispatcher dispatcher;

    @Autowired
    private Lops2019OpintojaksoService lops2019OpintojaksoService;

    @Autowired
    private Lops2019Service lops2019Service;

    @Autowired
    private Lops2019OpintojaksoRepository opintojaksoRepository;

    @Autowired
    private Lops2019OppiaineJarjestysRepository lops2019OppiaineJarjestysRepository;

    @Autowired
    private Lops2019OppiaineService lops2019OppiaineService;

    @Autowired
    private OpetussuunnitelmanMuokkaustietoService muokkaustietoService;

    @Autowired
    private KayttajanTietoService kayttajanTietoService;

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private OpetussuunnitelmaService self;

    @Autowired
    private CacheManager cacheManager;

    private List<Opetussuunnitelma> findJulkaistutByQuery(OpetussuunnitelmaQuery pquery) {
        CriteriaQuery<Opetussuunnitelma> query = getJulkaistutQuery(pquery);
        return em.createQuery(query).getResultList();
    }

    private CriteriaQuery<Opetussuunnitelma> getJulkaistutQuery(OpetussuunnitelmaQuery pquery) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Opetussuunnitelma> query = builder.createQuery(Opetussuunnitelma.class);
        Root<Opetussuunnitelma> ops = query.from(Opetussuunnitelma.class);

        List<Predicate> ehdot = new ArrayList<>();

        // VAIN JULKAISTUT
        ehdot.add(builder.or(
                builder.equal(ops.get(Opetussuunnitelma_.tila), Tila.JULKAISTU),
                builder.greaterThan(opsJulkaistuSubQuery(ops, builder), 0l)));

        // Haettu organisaatio löytyy opsilta
        if (pquery.getOrganisaatio() != null) {
            Expression<Set<String>> organisaatiot = ops.get(Opetussuunnitelma_.organisaatiot);
            ehdot.add(builder.and(builder.isMember(pquery.getOrganisaatio(), organisaatiot)));
        }

        // Koulutustyyppi
        if (pquery.getKoulutustyyppi() != null) {
            ehdot.add(builder.and(builder.equal(ops.get(Opetussuunnitelma_.koulutustyyppi), KoulutusTyyppi.of(pquery.getKoulutustyyppi()))));
        }

        // Perusteen tyyppi
        if (pquery.getTyyppi() != null) {
            ehdot.add(builder.and(builder.equal(ops.get(Opetussuunnitelma_.tyyppi), pquery.getTyyppi())));
        }

        // Perusteen id
        if (pquery.getPerusteenId() != null) {
            Path<PerusteCache> cachedPeruste = ops.join(Opetussuunnitelma_.cachedPeruste);
            ehdot.add(builder.and(builder.equal(cachedPeruste.get(PerusteCache_.perusteId), pquery.getPerusteenId())));
        }

        // Perusteen diaarinumero
        if (pquery.getPerusteenDiaarinumero() != null) {
            ehdot.add(builder.and(builder.equal(ops.get(Opetussuunnitelma_.perusteenDiaarinumero), pquery.getPerusteenDiaarinumero())));
        }

        query.where(ehdot.toArray(new Predicate[ehdot.size()]));

        return query.select(ops);
    }

    private Subquery opsJulkaistuSubQuery(Root<Opetussuunnitelma> root, CriteriaBuilder cb) {
        Subquery sub = cb.createQuery(Opetussuunnitelma.class).subquery(Long.class);
        Root subRoot = sub.from(OpetussuunnitelmanJulkaisu.class);
        Join<OpetussuunnitelmanJulkaisu, Opetussuunnitelma> subAuthors = subRoot.join(OpetussuunnitelmanJulkaisu_.opetussuunnitelma);
        sub.select(cb.count(subRoot.get(OpetussuunnitelmanJulkaisu_.id)));
        sub.where(cb.equal(root.get(Opetussuunnitelma_.id), subAuthors.get(Opetussuunnitelma_.id)));
        return sub;
    }

    @Override
    @Transactional(readOnly = true)
    public List<OpetussuunnitelmaJulkinenDto> getAllJulkiset(OpetussuunnitelmaQuery query) {
        List<Opetussuunnitelma> opetussuunnitelmat;
        if (query != null) {
            query.setTyyppi(Tyyppi.OPS);
            opetussuunnitelmat = findJulkaistutByQuery(query).stream()
                    .collect(Collectors.toList());
        } else {
            opetussuunnitelmat = opetussuunnitelmaRepository.findAllByTyyppiAndTilaIsJulkaistu(Tyyppi.OPS);
        }

        final List<OpetussuunnitelmaJulkinenDto> dtot = mapper.mapAsList(opetussuunnitelmat,
                OpetussuunnitelmaJulkinenDto.class);

        dtot.forEach(dto -> {
            for (KoodistoDto koodistoDto : dto.getKunnat()) {
                Map<String, String> tekstit = new HashMap<>();
                KoodistoKoodiDto kunta = koodistoService.get("kunta", koodistoDto.getKoodiUri());
                if (kunta != null) {
                    for (KoodistoMetadataDto metadata : kunta.getMetadata()) {
                        tekstit.put(metadata.getKieli(), metadata.getNimi());
                    }
                }
                koodistoDto.setNimi(new LokalisoituTekstiDto(tekstit));
            }

            for (OrganisaatioDto organisaatioDto : dto.getOrganisaatiot()) {
                Map<String, String> tekstit = new HashMap<>();
                List<String> tyypit = new ArrayList<>();
                JsonNode organisaatio = organisaatioService.getOrganisaatio(organisaatioDto.getOid());
                if (organisaatio != null) {
                    JsonNode nimiNode = organisaatio.get("nimi");
                    if (nimiNode != null) {
                        Iterator<Map.Entry<String, JsonNode>> it = nimiNode.fields();
                        while (it.hasNext()) {
                            Map.Entry<String, JsonNode> field = it.next();
                            tekstit.put(field.getKey(), field.getValue().asText());
                        }
                    }

                    JsonNode tyypitNode = Optional.ofNullable(organisaatio.get("tyypit"))
                            .orElse(organisaatio.get("organisaatiotyypit"));
                    if (tyypitNode != null) {
                        tyypit = StreamSupport.stream(tyypitNode.spliterator(), false)
                                .map(JsonNode::asText)
                                .collect(Collectors.toList());
                    }
                }
                organisaatioDto.setNimi(new LokalisoituTekstiDto(tekstit));
                organisaatioDto.setTyypit(tyypit);
            }
        });
        return dtot;
    }

    @Override
    @Transactional(readOnly = true)
    public OpetussuunnitelmaJulkinenDto getOpetussuunnitelmaJulkinen(Long id) {
        Opetussuunnitelma ops = opetussuunnitelmaRepository.findOne(id);
        assertExists(ops, "Pyydettyä opetussuunnitelmaa ei ole olemassa");
        if (ops.getTila() != Tila.JULKAISTU) {
            throw new NotExistsException("Pyydettyä opetussuunnitelmaa ei ole olemassa");
        }
        return mapper.map(ops, OpetussuunnitelmaJulkinenDto.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OpetussuunnitelmaInfoDto> getAll(Tyyppi tyyppi, Tila tila) {
        Set<String> organisaatiot = SecurityUtil.getOrganizations(EnumSet.allOf(RolePermission.class));
        final List<Opetussuunnitelma> opetussuunnitelmat;

        if (tyyppi == Tyyppi.POHJA) {
            opetussuunnitelmat = opetussuunnitelmaRepository.findPohja(organisaatiot);
        } else {
            opetussuunnitelmat = opetussuunnitelmaRepository.findAllByTyyppi(tyyppi, organisaatiot);
        }

        return mapper.mapAsList(opetussuunnitelmat, OpetussuunnitelmaInfoDto.class).stream()
                .filter(ops -> tila == null || ops.getTila() == tila)
                .map(dto -> {
                    fetchKuntaNimet(dto);
                    fetchOrganisaatioNimet(dto);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OpetussuunnitelmaInfoDto> getSivutettu(Tyyppi tyyppi, Tila tila, KoulutusTyyppi koulutustyyppi, String nimi, int sivu, int sivukoko) {
        Page<Opetussuunnitelma> opetussuunnitelmat;
        Pageable pageable = new PageRequest(sivu, sivukoko, new Sort(Sort.Direction.fromString("DESC"), "luotu"));
        if (SecurityUtil.isUserAdmin()) {
            opetussuunnitelmat = opetussuunnitelmaRepository.findSivutettu(
                    tyyppi,
                    tila.name(),
                    nimi,
                    koulutustyyppi != null ? koulutustyyppi.name() : "",
                    Collections.singletonList("empty"),
                    pageable);
        } else {
            Set<String> organisaatiot = SecurityUtil.getOrganizations(EnumSet.allOf(RolePermission.class));
            opetussuunnitelmat = opetussuunnitelmaRepository.findSivutettu(
                    tyyppi,
                    tila.name(),
                    nimi,
                    koulutustyyppi != null ? koulutustyyppi.name() : "",
                    organisaatiot,
                    pageable);
        }

        return opetussuunnitelmat.map(ops -> mapper.map(ops, OpetussuunnitelmaInfoDto.class));
    }

    @Override
    @Transactional(readOnly = true)
    public Long getAmount(Tyyppi tyyppi, Set<Tila> tilat) {
        Set<String> organisaatiot = SecurityUtil.getOrganizations(EnumSet.allOf(RolePermission.class));
        if (SecurityUtil.isUserAdmin()) {
            if (tilat.contains(Tila.JULKAISTU)) {
                return opetussuunnitelmaRepository.countByTyyppiAndJulkaistut(tyyppi);
            } else {
                return opetussuunnitelmaRepository.countByTyyppi(tyyppi, tilat);
            }
        }

        if (tilat.contains(Tila.JULKAISTU)) {
            return opetussuunnitelmaRepository.countByTyyppiAndJulkaistut(tyyppi, organisaatiot);
        } else {
            return opetussuunnitelmaRepository.countByTyyppi(tyyppi, tilat, organisaatiot);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<OpetussuunnitelmaInfoDto> getAll(Tyyppi tyyppi) {
        return getAll(tyyppi, null);
    }

    @Override
    @Cacheable("tilastot")
    @Transactional(readOnly = true)
    public OpetussuunnitelmaStatistiikkaDto getStatistiikka() {
        List<OpetussuunnitelmaInfoDto> opsit = mapper.mapAsList(opetussuunnitelmaRepository.findAllByTyyppi(Tyyppi.OPS), OpetussuunnitelmaInfoDto.class).stream()
                .map(ops -> {
                    try {
                        fetchOrganisaatioNimet(ops);
                    } catch (BusinessRuleViolationException ex) {
                        logger.error(ex.getLocalizedMessage());
                    }
                    return ops;
                })
                .collect(Collectors.toList());

        OpetussuunnitelmaStatistiikkaDto result = new OpetussuunnitelmaStatistiikkaDto();
        result.getTasoittain().put("seutukunnat", opsit.stream().filter(ops -> ops.getKunnat().size() > 1).count());
        result.getTasoittain().put("kunnat", opsit.stream().filter(ops -> ops.getKunnat().size() == 1).count());
        result.getTasoittain().put("koulujoukko", opsit.stream()
                .filter(ops -> ops.getOrganisaatiot().stream()
                        .filter(org -> !ObjectUtils.isEmpty(org.getTyypit()))
                        .filter(org -> "Oppilaitos".equals(org.getTyypit().get(0)))
                        .count() > 1)
                .count());
        result.getTasoittain().put("koulut", opsit.stream()
                .filter(ops -> ops.getOrganisaatiot().stream()
                        .filter(org -> !ObjectUtils.isEmpty(org.getTyypit()))
                        .filter(org -> "Oppilaitos".equals(org.getTyypit().get(0)))
                        .count() == 1)
                .count());

        result.getKielittain().put("fi", opsit.stream().filter(ops -> ops.getJulkaisukielet().contains(Kieli.FI)).count());
        result.getKielittain().put("sv", opsit.stream().filter(ops -> ops.getJulkaisukielet().contains(Kieli.SV)).count());
        result.getKielittain().put("se", opsit.stream().filter(ops -> ops.getJulkaisukielet().contains(Kieli.SE)).count());
        result.getKielittain().put("en", opsit.stream().filter(ops -> ops.getJulkaisukielet().contains(Kieli.EN)).count());

        result.getTiloittain().put("esikatseltavissa", opsit.stream()
                .filter(OpetussuunnitelmaBaseDto::isEsikatseltavissa)
                .count());

        for (OpetussuunnitelmaInfoDto ops : opsit) {
            result.getKoulutustyypeittain().put(ops.getKoulutustyyppi().toString(), result.getKoulutustyypeittain().getOrDefault(ops.getKoulutustyyppi().toString(), 0L) + 1);
            result.getTiloittain().put(ops.getTila().toString(), result.getTiloittain().getOrDefault(ops.getTila().toString(), 0L) + 1);
        }

        return result;
    }

    @Override
    public List<OpetussuunnitelmaInfoDto> getAdminList() {
        return mapper.mapAsList(opetussuunnitelmaRepository.findAllByTyyppi(Tyyppi.OPS), OpetussuunnitelmaInfoDto.class);
    }

    @Override
    @Transactional(readOnly = true)
    public PerusteDto getPeruste(Long opsId) {
        Opetussuunnitelma ops = opetussuunnitelmaRepository.findOne(opsId);
        assertExists(ops, "Pyydettyä opetussuunnitelmaa ei ole olemassa");
        return eperusteetService.getPeruste(ops.getPerusteenDiaarinumero());
    }

    @Override
    public List<OpetussuunnitelmanJulkaisuDto> getJulkaisut(Long opsId) {
        Opetussuunnitelma ops = opetussuunnitelmaRepository.findOne(opsId);
        assertExists(ops, "Pyydettyä opetussuunnitelmaa ei ole olemassa");
        List<OpetussuunnitelmanJulkaisu> julkaisut = julkaisuRepository.findAllByOpetussuunnitelma(ops);
        return taytaKayttajaTiedot(mapper.mapAsList(julkaisut, OpetussuunnitelmanJulkaisuDto.class));
    }

    @Override
    public List<OpetussuunnitelmanJulkaisuDto> getJulkaisutKevyt(Long opsId) {
        Opetussuunnitelma ops = opetussuunnitelmaRepository.findOne(opsId);
        assertExists(ops, "Pyydettyä opetussuunnitelmaa ei ole olemassa");
        List<OpetussuunnitelmaJulkaisuKevyt> julkaisut = julkaisuRepository.findKevytdataByOpetussuunnitelma(ops);
        return mapper.mapAsList(julkaisut, OpetussuunnitelmanJulkaisuDto.class);
    }

    @Override
    public PerusteInfoDto getPerusteBase(Long id) {
        Opetussuunnitelma ops = opetussuunnitelmaRepository.findOne(id);
        PerusteDto perusteDto = eperusteetService.getPerusteById(ops.getCachedPeruste().getPerusteId());
        return mapper.map(perusteDto, PerusteInfoDto.class);
    }

    @Override
    public JsonNode queryOpetussuunnitelmaJulkaisu(Long opsId, String query) {
        Opetussuunnitelma ops = opetussuunnitelmaRepository.findOne(opsId);
        assertExists(ops, "Pyydettyä opetussuunnitelmaa ei ole olemassa");
        OpetussuunnitelmanJulkaisu julkaisu = julkaisuRepository.findFirstByOpetussuunnitelmaOrderByRevisionDesc(ops);
        assertExists(julkaisu, "Pyydettyä opetussuunnitelmaa ei ole olemassa");
        JsonNode data = julkaisuRepositoryCustom.querySisalto(julkaisu.getId(), query);
        return data;
    }

    @Override
    public <T extends NavigationBuilder> NavigationNodeDto buildNavigationWithDate(Long opsId, Date pvm, String kieli, Class<T> clazz) {
        NavigationNodeDto navigationNodeDto = dispatcher.get(opsId, clazz)
                .buildNavigation(opsId, kieli);
        return siirraLiitteetLoppuun(navigationNodeDto);
    }

    @Override
    public NavigationNodeDto buildNavigation(Long opsId, String kieli) {
        return buildNavigationWithDate(opsId, new Date(), kieli, NavigationBuilder.class);
    }

    @Override
    @Cacheable("ops-navigation")
    public NavigationNodeDto buildNavigationPublic(Long opsId, String kieli) {
        return buildNavigationWithDate(opsId, new Date(), kieli, NavigationBuilderPublic.class);
    }

    @Override
    @CacheEvict("ops-navigation")
    public void publicNavigationEvict(Long opsId, String kieli) {
        // this method doesn't do anything and is only here for evicting the cache
    }

    @Override
    public OpetussuunnitelmanJulkaisuDto addJulkaisu(Long opsId, UusiJulkaisuDto julkaisuDto) {
        Opetussuunnitelma ops = opetussuunnitelmaRepository.findOne(opsId);
        assertExists(ops, "Pyydettyä opetussuunnitelmaa ei ole olemassa");

        if (!Tyyppi.OPS.equals(ops.getTyyppi())) {
            throw new BusinessRuleViolationException("pohjaa-ei-voi-julkaista");
        }

        if (KoulutustyyppiToteutus.LOPS2019.equals(ops.getToteutus())) {
            Lops2019ValidointiDto validointi = validointiService.getValidointi(opsId);
            if (!validointi.isValid()) {
                throw new BusinessRuleViolationException("julkaisu-ei-mahdollinen-keskeneraiselle");
            }
        }

        OpetussuunnitelmanJulkaisu julkaisu = new OpetussuunnitelmanJulkaisu();
        julkaisu.setOpetussuunnitelma(ops);
        julkaisu.setTiedote(mapper.map(julkaisuDto.getJulkaisutiedote(), LokalisoituTeksti.class));
        OpetussuunnitelmaExportDto opsData = getExportedOpetussuunnitelma(opsId);

        try {
            ObjectNode opsDataJson = (ObjectNode) jsonMapper.toJson(opsData);
            List<OpetussuunnitelmanJulkaisu> vanhatJulkaisut = julkaisuRepository.findAllByOpetussuunnitelma(ops);
            if (!vanhatJulkaisut.isEmpty()) {
                int lastHash = vanhatJulkaisut.get(vanhatJulkaisut.size() - 1).getData().getHash();
                if (lastHash == opsDataJson.hashCode()) {
                    throw new BusinessRuleViolationException("opetussuunnitelma-ei-muuttunut-viime-julkaisun-jalkeen");
                }
            }

            Set<DokumenttiDto> dokumentit = ops.getJulkaisukielet().stream().map(kieli -> {
                DokumenttiDto dokumenttiDto = dokumenttiService.getDto(opsId, kieli);
                try {
                    dokumenttiService.setStarted(dokumenttiDto);
                    dokumenttiService.generateWithDto(dokumenttiDto);
                } catch (DokumenttiException e) {
                    logger.error(e.getLocalizedMessage(), e.getCause());
                }
                return dokumenttiDto;
            }).collect(toSet());

            JulkaistuOpetussuunnitelmaData data = new JulkaistuOpetussuunnitelmaData(opsDataJson);
            data = julkaistuOpetussuunnitelmaDataRepository.save(data);
            julkaisu.setDokumentit(dokumentit.stream().map(DokumenttiDto::getId).collect(toSet()));
            julkaisu.setData(data);
            julkaisu.setRevision(vanhatJulkaisut.size() + 1);
            julkaisu = julkaisuRepository.save(julkaisu);

            muokkaustietoService.addOpsMuokkausTieto(opsId, ops, MuokkausTapahtuma.JULKAISU);
            refreshOpetussuunnitelmaNavigation(opsId);

            return taytaKayttajaTiedot(mapper.map(julkaisu, OpetussuunnitelmanJulkaisuDto.class));
        } catch (IOException e) {
            e.printStackTrace();
            throw new BusinessRuleViolationException("julkaisun-tallennus-epaonnistui");
        }
    }

    private void refreshOpetussuunnitelmaNavigation(Long opsId) {
        Stream.of(Kieli.FI, Kieli.SV, Kieli.EN).forEach(kieli -> {
            self.publicNavigationEvict(opsId, kieli.toString());
            self.buildNavigationPublic(opsId, kieli.toString());
        });
    }

    @Override
    public OpetussuunnitelmanJulkaisuDto aktivoiJulkaisu(Long opsId, int revision) {
        Opetussuunnitelma opetussuunnitelma = opetussuunnitelmaRepository.findOne(opsId);
        OpetussuunnitelmanJulkaisu vanhaJulkaisu = julkaisuRepository.findByOpetussuunnitelmaAndRevision(opetussuunnitelma, revision);
        OpetussuunnitelmanJulkaisu viimeisinJulkaisu = julkaisuRepository.findFirstByOpetussuunnitelmaOrderByRevisionDesc(opetussuunnitelma);

        OpetussuunnitelmanJulkaisu julkaisu = new OpetussuunnitelmanJulkaisu();
        julkaisu.setRevision(viimeisinJulkaisu != null ? viimeisinJulkaisu.getRevision() + 1 : 1);
        julkaisu.setTiedote(vanhaJulkaisu.getTiedote());
        julkaisu.setDokumentit(Sets.newHashSet(vanhaJulkaisu.getDokumentit()));
        julkaisu.setOpetussuunnitelma(opetussuunnitelma);
        julkaisu.setData(vanhaJulkaisu.getData());
        julkaisu = julkaisuRepository.save(julkaisu);

        muokkaustietoService.addOpsMuokkausTieto(opsId, opetussuunnitelma, MuokkausTapahtuma.JULKAISU);
        refreshOpetussuunnitelmaNavigation(opsId);

        return taytaKayttajaTiedot(mapper.map(julkaisu, OpetussuunnitelmanJulkaisuDto.class));
    }

    private List<OpetussuunnitelmanJulkaisuDto> taytaKayttajaTiedot(List<OpetussuunnitelmanJulkaisuDto> julkaisut) {
        Map<String, KayttajanTietoDto> kayttajatiedot = kayttajanTietoService
                .haeKayttajatiedot(julkaisut.stream().map(OpetussuunnitelmanJulkaisuDto::getLuoja).collect(Collectors.toList()))
                .stream().collect(Collectors.toMap(kayttajanTieto -> kayttajanTieto.getOidHenkilo(), kayttajanTieto -> kayttajanTieto));
        julkaisut.forEach(julkaisu -> julkaisu.setKayttajanTieto(kayttajatiedot.get(julkaisu.getLuoja())));
        return julkaisut;
    }

    private OpetussuunnitelmanJulkaisuDto taytaKayttajaTiedot(OpetussuunnitelmanJulkaisuDto julkaisu) {
        return taytaKayttajaTiedot(Arrays.asList(julkaisu)).get(0);
    }

    @Override
    @Transactional(readOnly = true)
    public OpetussuunnitelmaKevytDto getOpetussuunnitelma(Long id) {
        Opetussuunnitelma ops = opetussuunnitelmaRepository.findOne(id);
        assertExists(ops, "Pyydettyä opetussuunnitelmaa ei ole olemassa");
        OpetussuunnitelmaKevytDto dto = mapper.map(ops, OpetussuunnitelmaKevytDto.class);
        fetchKuntaNimet(dto);
        fetchOrganisaatioNimet(dto);
        return dto;
    }

    private void fetchLapsiOpetussuunnitelmat(Long id, Set<Opetussuunnitelma> opsit) {
        opsit.addAll(opetussuunnitelmaRepository.findAllByPohjaId(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<OpetussuunnitelmaInfoDto> getLapsiOpetussuunnitelmat(Long id) {
        Opetussuunnitelma ops = opetussuunnitelmaRepository.findOne(id);
        assertExists(ops, "Pyydettyä opetussuunnitelmaa ei ole olemassa");
        Set<Opetussuunnitelma> result = new HashSet<>();
        fetchLapsiOpetussuunnitelmat(id, result);
        return mapper.mapAsList(result, OpetussuunnitelmaInfoDto.class);
    }

    @Override
    @Transactional(readOnly = true)
    public OpetussuunnitelmaDto getOpetussuunnitelmaKaikki(Long id) {
        Opetussuunnitelma ops = opetussuunnitelmaRepository.findOne(id);
        assertExists(ops, "Pyydettyä opetussuunnitelmaa ei ole olemassa");
        OpetussuunnitelmaDto dto = mapper.map(ops, OpetussuunnitelmaDto.class);
        fetchKuntaNimet(dto);
        fetchOrganisaatioNimet(dto);
        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<OpetussuunnitelmaInfoDto> getOpetussuunnitelmaOpsPohjat() {
        Map<String, OrganisaatioLaajaDto> kayttajanOrganisaatiot = kayttajanTietoService.haeOrganisaatioOikeudet()
                .stream()
                .filter(Objects::nonNull)
                .map(oid -> organisaatioService.getOrganisaatio(oid, OrganisaatioLaajaDto.class))
                .filter(Objects::nonNull)
                .collect(toMap(OrganisaatioLaajaDto::getOid, Function.identity()));
        Set<String> kaikki = kayttajanOrganisaatiot.values().stream()
                .map(OrganisaatioLaajaDto::getParentPath)
                .flatMap(Collection::stream)
                .collect(toSet());
        kaikki.remove(SecurityUtil.OPH_OID);
        kaikki.addAll(kayttajanOrganisaatiot.keySet());
        return opetussuunnitelmaRepository.findOpsPohja(kaikki).stream()
            .map(p -> mapper.map(p, OpetussuunnitelmaInfoDto.class))
            .collect(toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Set<PerusteLaajaalainenosaaminenDto> getLaajaalaisetosaamiset(Long id) {
        Opetussuunnitelma ops = opetussuunnitelmaRepository.findOne(id);
        assertExists(ops, "Pyydettyä opetussuunnitelmaa ei ole olemassa");
        return eperusteetService.getPeruste(ops.getPerusteenDiaarinumero()).getPerusopetus()
                .getLaajaalaisetosaamiset();
    }

    @Override
    public void updateOppiainejarjestys(Long opsId, List<JarjestysDto> oppiainejarjestys) {
        Opetussuunnitelma ops = opetussuunnitelmaRepository.findOne(opsId);
        assertExists(ops, "Pyydettyä opetussuunnitelmaa ei ole olemassa");

        Map<Long, OpsOppiaine> oppiaineet = new HashMap<>();
        Map<Long, Oppiaine> oppimaarat = new HashMap<>();
        ops.getOppiaineet().forEach(opsOppiaine -> {
            oppiaineet.put(opsOppiaine.getOppiaine().getId(), opsOppiaine);
            oppimaarat.put(opsOppiaine.getOppiaine().getId(), opsOppiaine.getOppiaine());
            if (opsOppiaine.getOppiaine().getOppimaarat() != null) {
                opsOppiaine.getOppiaine().getOppimaarat().forEach(oppimaara -> oppimaarat.put(oppimaara.getId(), oppimaara));
            }
        });

        for (JarjestysDto node : oppiainejarjestys) {
            OpsOppiaine oppiaine = oppiaineet.get(node.getOppiaineId());
            if (oppiaine != null) {
                oppiaine.setJnro(node.getJnro());
            }
            Oppiaine oppimaara = oppimaarat.get(node.getOppiaineId());
            assertExists(oppimaara, "Pyydettyä oppiainetta ei ole");
            oppimaara.getVuosiluokkakokonaisuudet().forEach(
                    oppiaineenvuosiluokkakokonaisuus -> oppiaineenvuosiluokkakokonaisuus.setJnro(node.getJnro()));
        }
    }

    private void jarjestaOppiaineet(
            Opetussuunnitelma ops,
            HashMap<Long, String> oaKoodiMap,
            List<OppiaineOpintojaksoDto> oppiaineet
    ) {
        Lops2019Sisalto lops2019Sisalto = ops.getLops2019();
        Set<Lops2019OppiaineJarjestys> oppiaineJarjestykset = lops2019Sisalto.getOppiaineJarjestykset();
        oppiaineJarjestykset.clear();

        for (int i = 0; i < oppiaineet.size(); i++) {
            OppiaineOpintojaksoDto lapsi = oppiaineet.get(i);
            Long id = lapsi.getId();
            if (id != null) {
                String oppiaineKoodi = oaKoodiMap.get(id);
                if (oppiaineKoodi != null) {
                    Lops2019OppiaineJarjestys oppiaineJarjestys = new Lops2019OppiaineJarjestys();
                    oppiaineJarjestys.setKoodi(oppiaineKoodi);
                    oppiaineJarjestys.setJarjestys(i);
                    oppiaineJarjestykset.add(oppiaineJarjestys);
                }
            }
        }
    }

    private void jarjestaOpintojaksot(
            List<OppiaineOpintojaksoDto> lapset,
            HashMap<Long, String> oaKoodiMap,
            HashMap<Long, Lops2019Opintojakso> ojMap,
            String parentOaKoodi
    ) {
        for (int i = 0; i < lapset.size(); i++) {
            OppiaineOpintojaksoDto lapsi = lapset.get(i);
            Long id = lapsi.getId();
            if (id != null) {
                String oppiaineKoodi = oaKoodiMap.get(id);

                if (parentOaKoodi != null
                        && ojMap.get(id) != null) {
                    Lops2019Opintojakso oj = ojMap.get(id);
                    for (Lops2019OpintojaksonOppiaine ojOa : oj.getOppiaineet()) {
                        if (ojOa.getKoodi() != null && ojOa.getKoodi().equals(parentOaKoodi)) {
                            ojOa.setJarjestys(i);
                        }
                    }
                    opintojaksoRepository.save(oj);
                }

                // Käydään rekursiivisesti läpi
                if (!ObjectUtils.isEmpty(lapsi.getLapset())) {
                    jarjestaOpintojaksot(lapsi.getLapset(), oaKoodiMap, ojMap, oppiaineKoodi);
                }
            }
        }
    }

    @Override
    public void updateOppiaineJaOpintojaksojarjestys(
            Long opsId,
            List<OppiaineOpintojaksoDto> oppiaineopintojaksojarjestys
    ) {
        Opetussuunnitelma ops = opetussuunnitelmaRepository.findOne(opsId);
        assertExists(ops, "Pyydettyä opetussuunnitelmaa ei ole olemassa");

        HashMap<Long, String> oaKoodiMap = new HashMap<>();
        lops2019Service.getPerusteOppiaineetAndOppimaarat(opsId).forEach(oa -> {
            if (oa.getKoodi() != null && oa.getKoodi().getUri() != null) {
                oaKoodiMap.put(oa.getId(), oa.getKoodi().getUri());
            }
        });
        lops2019OppiaineService.getAll(opsId, Lops2019PaikallinenOppiaineDto.class).forEach(poa -> {
            if (poa.getKoodi() != null) {
                oaKoodiMap.put(poa.getId(), poa.getKoodi());
            }
        });

        HashMap<Long, Lops2019Opintojakso> ojMap = new HashMap<>();
        mapper.mapAsList(lops2019OpintojaksoService.getAll(opsId, Lops2019OpintojaksoDto.class),
                Lops2019Opintojakso.class).forEach(oj -> ojMap.put(oj.getId(), oj));

        jarjestaOppiaineet(ops, oaKoodiMap, oppiaineopintojaksojarjestys.stream()
                .flatMap(oa -> Stream.concat(Stream.of(oa), (oa.getLapset() != null ? oa.getLapset() : Collections.<OppiaineOpintojaksoDto>emptyList()).stream()))
                .collect(Collectors.toList()));
        jarjestaOpintojaksot(oppiaineopintojaksojarjestys, oaKoodiMap, ojMap, null);

        muokkaustietoService.addOpsMuokkausTieto(opsId, ops, MuokkausTapahtuma.PAIVITYS);
    }

    private NavigationNodeDto siirraLiitteetLoppuun(NavigationNodeDto navigationNodeDto) {
        Stack<NavigationNodeDto> stack = new Stack<>();
        stack.push(navigationNodeDto);

        List<NavigationNodeDto> liitteet = new ArrayList<>();

        while (!stack.empty()) {
            NavigationNodeDto head = stack.pop();

            // Kerätään liitteet talteen
            liitteet.addAll(head.getChildren().stream()
                    .filter(child -> Objects.equals(child.getType(), NavigationType.liite))
                    .collect(Collectors.toList()));

            // Poistetaan liitteet
            head.setChildren(head.getChildren().stream()
                    .filter(child -> !Objects.equals(child.getType(), NavigationType.liite))
                    .collect(Collectors.toList()));

            // Käydään lävitse myös lapset
            stack.addAll(head.getChildren());
        }

        // Lisätään liitteet loppuun
        navigationNodeDto.getChildren().addAll(liitteet);

        return navigationNodeDto;
    }

    @Override
    public void fetchKuntaNimet(OpetussuunnitelmaBaseDto opetussuunnitelmaDto) {
        for (KoodistoDto koodistoDto : opetussuunnitelmaDto.getKunnat()) {
            Map<String, String> tekstit = new HashMap<>();
            KoodistoKoodiDto kunta = koodistoService.get("kunta", koodistoDto.getKoodiUri());
            if (kunta != null) {
                for (KoodistoMetadataDto metadata : kunta.getMetadata()) {
                    tekstit.put(metadata.getKieli(), metadata.getNimi());
                }
            }
            koodistoDto.setNimi(new LokalisoituTekstiDto(tekstit));
        }
    }

    @Override
    public void fetchOrganisaatioNimet(OpetussuunnitelmaBaseDto opetussuunnitelmaDto) {
        for (OrganisaatioDto organisaatioDto : opetussuunnitelmaDto.getOrganisaatiot()) {
            Map<String, String> tekstit = new HashMap<>();
            List<String> tyypit = new ArrayList<>();
            JsonNode organisaatio = organisaatioService.getOrganisaatio(organisaatioDto.getOid());
            if (organisaatio != null) {
                JsonNode nimiNode = organisaatio.get("nimi");
                if (nimiNode != null) {
                    Iterator<Map.Entry<String, JsonNode>> it = nimiNode.fields();
                    while (it.hasNext()) {
                        Map.Entry<String, JsonNode> field = it.next();
                        tekstit.put(field.getKey(), field.getValue().asText());
                    }
                }

                JsonNode tyypitNode = ofNullable(organisaatio.get("tyypit"))
                        .orElse(organisaatio.get("organisaatiotyypit"));
                if (tyypitNode != null) {
                    tyypit = StreamSupport.stream(tyypitNode.spliterator(), false)
                            .map(JsonNode::asText)
                            .collect(Collectors.toList());
                }
            }
            organisaatioDto.setNimi(new LokalisoituTekstiDto(tekstit));
            organisaatioDto.setTyypit(tyypit);
        }
    }

    public void kopioiPohjanSisallotOpetussuunnitelmaan(Opetussuunnitelma pohja, Opetussuunnitelma ops) {
        if (pohja == null) {
            throw new BusinessRuleViolationException("pohjaa-ei-loytynyt");
        }

        if (ops == null) {
            throw new BusinessRuleViolationException("opetussuunnitelmaa-ei-loytynyt");
        }

        if (!KoulutustyyppiToteutus.LOPS2019.equals(pohja.getToteutus()) || !pohja.getToteutus().equals(ops.getToteutus())) {
            throw new BusinessRuleViolationException("toteutustyyppi-ei-tuettu");
        }

        kasitteleTekstit(pohja.getTekstit(), ops.getTekstit(), true);
    }

    @Override
    @Transactional
    public OpetussuunnitelmaDto addOpetussuunnitelma(OpetussuunnitelmaLuontiDto opetussuunnitelmaDto) {

        if (opetussuunnitelmaDto.getId() != null) {
            throw new BusinessRuleViolationException("Uudessa opetussuunnitelmassa on id");
        }

        opetussuunnitelmaDto.setTyyppi(Tyyppi.OPS);
        Opetussuunnitelma ops = mapper.map(opetussuunnitelmaDto, Opetussuunnitelma.class);

        Set<String> userOids = SecurityUtil.getOrganizations(EnumSet.of(RolePermission.CRUD,
                RolePermission.ADMIN));
        if (CollectionUtil.intersect(userOids, ops.getOrganisaatiot()).isEmpty()) {
            throw new BusinessRuleViolationException("Käyttäjällä ei ole luontioikeutta " +
                    "opetussuunnitelman organisaatioissa");
        }

        Opetussuunnitelma pohja = ops.getPohja();

        if (pohja == null) {
            Set<Opetussuunnitelma> pohjat = opetussuunnitelmaRepository.findOneByTyyppiAndTilaAndKoulutustyyppi(
                    Tyyppi.POHJA,
                    Tila.VALMIS,
                    opetussuunnitelmaDto.getKoulutustyyppi());
            if (pohjat.isEmpty()) {
                throw new BusinessRuleViolationException("koulutustyypin-pohjaa-ei-ole");
            }
            else if (pohjat.size() == 1) {
                pohja = pohjat.iterator().next();
            }
        }

        if (pohja != null) {
            ops.setKoulutustyyppi(pohja.getKoulutustyyppi());
            ops.setToteutus(pohja.getToteutus());
            ops.setPohja(pohja);
            ops.setTila(Tila.LUONNOS);
            ops.setTekstit(new TekstiKappaleViite(Omistussuhde.OMA));
            ops.getTekstit().setLapset(new ArrayList<>());

            checkValidPohja(ops);

            if (pohja.getPerusteenDiaarinumero() == null) {
                throw new BusinessRuleViolationException("Pohjalta puuttuu perusteen diaarinumero");
            }

            ops.setPerusteenDiaarinumero(pohja.getPerusteenDiaarinumero());
            ops.setCachedPeruste(ops.getCachedPeruste());
            if (ops.getCachedPeruste() == null) {
                PerusteDto peruste = eperusteetService.getPeruste(ops.getPerusteenDiaarinumero());
                PerusteCache perusteCache = perusteCacheRepository.findNewestEntryForPeruste(peruste.getId());
                if (perusteCache == null) {
                    throw new BusinessRuleViolationException("Opetussuunnitelman pohjasta ei löytynyt perustetta");
                }
                ops.setCachedPeruste(perusteCache);
            }

            if (KoulutustyyppiToteutus.LOPS2019.equals(ops.getToteutus())) {
                Lops2019Sisalto sisalto = new Lops2019Sisalto();
                sisalto.setOpetussuunnitelma(ops);
                ops.setLops2019(sisalto);
                ops = opetussuunnitelmaRepository.save(ops);
                kopioiPohjanSisallotOpetussuunnitelmaan(pohja, ops);
            }
            else {
                luoOpsPohjasta(pohja, ops);
                ops = opetussuunnitelmaRepository.save(ops);
                if (isPohjastaTehtyPohja(pohja)
                        && !KoulutustyyppiToteutus.LOPS2019.equals(pohja.getToteutus())
                        && pohja.getKoulutustyyppi().isLukio()) {
                    lisaaTeemaopinnotJosPohjassa(ops, pohja);
                }
            }
        } else {
            throw new BusinessRuleViolationException("Valmista opetussuunnitelman pohjaa ei löytynyt");
        }

        return mapper.map(ops, OpetussuunnitelmaDto.class);
    }

    private void checkValidPohja(Opetussuunnitelma ops) {
        if(ops.getPohja().getPohja() == null) {
            if (!Tila.VALMIS.equals(ops.getPohja().getTila()) & !Tila.JULKAISTU.equals(ops.getPohja().getTila())) {
                throw new BusinessRuleViolationException("pohjan-pitaa-olla-julkaistu");
            }
        }
    }

    private void lisaaTeemaopinnotJosPohjassa(Opetussuunnitelma ops, Opetussuunnitelma pohja) {
        final Long opsId = ops.getId();
        pohja.getOppiaineet().stream()
                .filter(opsOppiaine1 -> opsOppiaine1.getOppiaine().getKoodiUri().compareTo("oppiaineetyleissivistava2_to") == 0)
                .findFirst()
                .ifPresent(opsOppiaine -> {
                    LukioAbstraktiOppiaineTuontiDto dto = new LukioAbstraktiOppiaineTuontiDto();
                    dto.setNimi(mapper.map(opsOppiaine.getOppiaine().getNimi(), LokalisoituTekstiDto.class));
                    dto.setTunniste(opsOppiaine.getOppiaine().getTunniste());
                    lukioOpetussuunnitelmaService.addAbstraktiOppiaine(opsId, dto);
                });
    }

    private void luoOpsPohjasta(Opetussuunnitelma pohja, Opetussuunnitelma ops) {
        boolean teeKopio = pohja.getTyyppi() == Tyyppi.POHJA;
        kasitteleTekstit(pohja.getTekstit(), ops.getTekstit(), teeKopio);

        boolean onPohjastaTehtyPohja = isPohjastaTehtyPohja(pohja);

        Copier<Oppiaine> oppiaineCopier = teeKopio ? Oppiaine.basicCopier() : Copier.nothing();
        Map<Long, Oppiaine> newOppiaineByOld = new HashMap<>();
        Copier<Oppiaine> kurssiCopier = null;

        if (pohja.getKoulutustyyppi().isLukio()) {
            luoLukiokoulutusPohjasta(pohja, ops);
            kurssiCopier = getLukiokurssitOppiaineCopier(pohja, ops, teeKopio);
            oppiaineCopier = oppiaineCopier
                    .and(kurssiCopier)
                    .and((fromOa, toOa) -> {
                        toOa.setAbstrakti(fromOa.getAbstrakti());
                        newOppiaineByOld.put(fromOa.getId(), toOa);
                    });
        } else if (teeKopio) {
            oppiaineCopier = oppiaineCopier.and(Oppiaine.perusopetusCopier());
        }

        final Copier<Oppiaine> oppiainePerusCopier = oppiaineCopier;
        if (teeKopio && (!onPohjastaTehtyPohja || pohja.getKoulutustyyppi().isLukio())) {
            ConstructedCopier<Oppiaine> omConst = oppiainePerusCopier.construct(oa -> new Oppiaine(oa.getTunniste()));
            if (onPohjastaTehtyPohja && pohja.getKoulutustyyppi().isLukio()) {
                oppiaineCopier = oppiaineCopier.and(Oppiaine.oppimaaraCopier(om -> !om.isAbstraktiBool(), omConst));
            } else {
                oppiaineCopier = oppiaineCopier.and(Oppiaine.oppimaaraCopier(omConst));
            }
        } else if (kurssiCopier != null) {
            final Copier<Oppiaine> finalKurssiCopier = kurssiCopier;
            oppiaineCopier = oppiaineCopier.and((from, to) -> {
                if (from.isKoosteinen() && from.getOppiaine() == null) {
                    from.getOppimaarat().forEach(om ->
                            finalKurssiCopier.copy(om, om)
                    );
                }
            });
        }
        ConstructedCopier<OpsOppiaine> opsOppiaineCopier = OpsOppiaine.copier(
                oppiaineCopier.construct(existing -> teeKopio ? new Oppiaine(existing.getTunniste()) : existing), teeKopio);
        Stream<OpsOppiaine> oppiaineetToCopy = pohja.getKoulutustyyppi().isLukio()
                && pohja.getTyyppi() == Tyyppi.POHJA // ei kopioida pohjasta abstakteja ylätason oppiaineita, mutta OPS:sta kyllä
                    ? pohja.getOppiaineet().stream()
                        .filter(opsOa -> !opsOa.getOppiaine().isAbstraktiBool())
                    : pohja.getOppiaineet().stream();
        ops.setOppiaineet(oppiaineetToCopy.map(opsOppiaineCopier::copy).collect(toSet()));
        ops.getOppiaineJarjestykset().addAll(pohja.getOppiaineJarjestykset().stream()
                .map(old -> !teeKopio
                    ? new LukioOppiaineJarjestys(ops, old.getOppiaine(), old.getJarjestys())
                    : (newOppiaineByOld.get(old.getId().getOppiaineId()) != null
                        ? new LukioOppiaineJarjestys(ops, newOppiaineByOld.get(old.getId().getOppiaineId()), old.getJarjestys())
                        : null))
                .filter(Objects::nonNull)
                .collect(toSet()));

        Set<OpsVuosiluokkakokonaisuus> ovlkoot = pohja.getVuosiluokkakokonaisuudet().stream()
                .filter(ovlk -> ops.getVuosiluokkakokonaisuudet().stream()
                        .anyMatch(vk -> vk.getVuosiluokkakokonaisuus().getTunniste()
                                .equals(ovlk.getVuosiluokkakokonaisuus().getTunniste())))
                .map(ovlk -> teeKopio
                        ? new OpsVuosiluokkakokonaisuus(Vuosiluokkakokonaisuus.copyOf(ovlk.getVuosiluokkakokonaisuus()), true)
                        : new OpsVuosiluokkakokonaisuus(ovlk.getVuosiluokkakokonaisuus(), false))
                .collect(toSet());
        ops.setVuosiluokkakokonaisuudet(ovlkoot);
    }

    private boolean isPohjastaTehtyPohja(Opetussuunnitelma pohja) {
        Opetussuunnitelma ylinpohja = pohja;
        while (ylinpohja.getPohja() != null) {
            ylinpohja = ylinpohja.getPohja();
        }
        return ylinpohja.getId().equals(pohja.getId());
    }

    public static Copier<Oppiaine> getLukiokurssitOppiaineCopier(Opetussuunnitelma pohja,
                                                                 Opetussuunnitelma ops,
                                                                 boolean teeKopio) {

        Map<UUID, Lukiokurssi> existingKurssit = teeKopio ? new HashMap<>() : pohja.getLukiokurssit().stream()
                .map(OppiaineLukiokurssi::getKurssi)
                .filter(k -> k.getTunniste() != null)
                .collect(toMap(Kurssi::getTunniste, k -> k, (a, b) -> a)); // Tunniste ei ole unique opsin sisällä

        Map<Long, List<OppiaineLukiokurssi>> lukiokurssitByPohjaOppiaineId = pohja.getLukiokurssit().stream()
                .collect(groupingBy(oaKurssi -> oaKurssi.getOppiaine().getId()));

        return (from, to) ->
                ops.getLukiokurssit().addAll(ofNullable(lukiokurssitByPohjaOppiaineId.get(from.getId()))
                        .map(list -> list.stream().map(oaKurssi -> {
                                    Lukiokurssi kurssi = oaKurssi.getKurssi().getTunniste() == null
                                            ? null : existingKurssit.get(oaKurssi.getKurssi().getTunniste());

                                    if (kurssi == null) {
                                        kurssi = teeKopio ? oaKurssi.getKurssi().copy() : oaKurssi.getKurssi();
                                        if (oaKurssi.getKurssi().getTunniste() != null) {
                                            existingKurssit.put(oaKurssi.getKurssi().getTunniste(), kurssi);
                                        }
                                    }

                                    return new OppiaineLukiokurssi(ops, to, kurssi, oaKurssi.getJarjestys(), teeKopio);
                                }).collect(toList())
                        ).orElse(emptyList()));
    }

    private void luoLukiokoulutusPohjasta(Opetussuunnitelma from, Opetussuunnitelma to) {
        if (from.getAihekokonaisuudet() != null) {
            to.setAihekokonaisuudet(from.getAihekokonaisuudet().copy(to, from.getAihekokonaisuudet()));
        }
        if (from.getOpetuksenYleisetTavoitteet() != null) {
            to.setOpetuksenYleisetTavoitteet(from.getOpetuksenYleisetTavoitteet().copy(to,
                    from.getOpetuksenYleisetTavoitteet()));
        }
    }

    private void kasitteleTekstit(TekstiKappaleViite vanha, TekstiKappaleViite parent, boolean teeKopio) {
        List<TekstiKappaleViite> vanhaLapset = vanha.getLapset();
        if (vanhaLapset != null) {
            vanhaLapset.stream()
                    .filter(vanhaTkv -> vanhaTkv.getTekstiKappale() != null)
                    .forEach(vanhaTkv -> {
                        TekstiKappaleViite tkv = viiteRepository.save(new TekstiKappaleViite());
                        tkv.setOmistussuhde(teeKopio ? Omistussuhde.OMA : Omistussuhde.LAINATTU);
                        tkv.setLapset(new ArrayList<>());
                        tkv.updateOriginal(vanhaTkv);
                        tkv.setVanhempi(parent);
                        tkv.setPakollinen(vanhaTkv.isPakollinen());
                        tkv.setNaytaPerusteenTeksti(vanhaTkv.isNaytaPerusteenTeksti());
                        tkv.setPerusteTekstikappaleId(vanhaTkv.getPerusteTekstikappaleId());
                        tkv.setLiite(vanhaTkv.isLiite());
                        // EP-2405 Tämä tekee vielä comebackin
//                        tkv.setTekstiKappale(teeKopio ? tekstiKappaleRepository.save(vanhaTkv.getTekstiKappale().copy()) : vanhaTkv.getTekstiKappale());
                        TekstiKappale copy = vanhaTkv.getTekstiKappale().copy();
                        copy.setTeksti(null);
                        copy = tekstiKappaleRepository.save(copy);
                        tkv.setTekstiKappale(copy);
                        parent.getLapset().add(tkv);
                        kasitteleTekstit(vanhaTkv, tkv, teeKopio);
                    });
        }
    }

    private Opetussuunnitelma addPohjaLisaJaEsiopetus(Opetussuunnitelma ops, PerusteDto peruste, OpetussuunnitelmaLuontiDto pohjaDto) {
        ops.setKoulutustyyppi(peruste.getKoulutustyyppi());

        if (pohjaDto != null && pohjaDto.isRakennePohjasta()) {
            lisaaTekstipuuPerusteesta(peruste.getTekstiKappaleViiteSisalto(), ops);
        }

        return ops;
    }

    private Opetussuunnitelma addPohjaPerusopetus(
            Opetussuunnitelma ops,
            PerusteDto peruste,
            OpetussuunnitelmaLuontiDto pohjaDto
    ) {
        Long opsId = ops.getId();
        PerusopetuksenPerusteenSisaltoDto sisalto = peruste.getPerusopetus();

        if (pohjaDto != null && pohjaDto.isRakennePohjasta()) {
            lisaaTekstipuuPerusteesta(sisalto.getSisalto(), ops);
        }

        if (sisalto.getVuosiluokkakokonaisuudet() != null) {
            sisalto.getVuosiluokkakokonaisuudet()
                    .forEach(vk -> vuosiluokkakokonaisuusviiteRepository.save(
                            new Vuosiluokkakokonaisuusviite(vk.getTunniste(), vk.getVuosiluokat())));

            if (sisalto.getOppiaineet() != null) {
                sisalto.getOppiaineet().stream()
                        .map(OpsDtoMapper::fromEperusteet)
                        .forEach(oa -> oppiaineService.add(opsId, oa));
            }

            sisalto.getVuosiluokkakokonaisuudet().stream()
                    .map(OpsDtoMapper::fromEperusteet)
                    .forEach(vk -> vuosiluokkakokonaisuudet.add(opsId, vk));
        }

        // Alustetaan järjestys ePerusteista saatuun järjestykseen
        Integer idx = 0;
        for (OpsOppiaine oa : ops.getOppiaineet()) {
            for (Oppiaineenvuosiluokkakokonaisuus oavlk : oa.getOppiaine().getVuosiluokkakokonaisuudet()) {
                oavlk.setJnro(idx);
            }
            ++idx;
        }

        return ops;
    }

    private Opetussuunnitelma addPohjaLukiokoulutus(Opetussuunnitelma ops, PerusteDto peruste) {
        ops.setKoulutustyyppi(peruste.getKoulutustyyppi());

        LukiokoulutuksenPerusteenSisaltoDto lukioSisalto = peruste.getLukiokoulutus();
        if (lukioSisalto == null) {
            throw new IllegalStateException("Lukiokoutuksen sisältöä ei löytynyt.");
        }
        if (lukioSisalto.getRakenne() != null) {
            importLukioRakenne(lukioSisalto.getRakenne(), ops);
        }
        if (lukioSisalto.getAihekokonaisuudet() != null) {
            importAihekokonaisuudet(lukioSisalto.getAihekokonaisuudet(), ops);
        }
        if (lukioSisalto.getOpetuksenYleisetTavoitteet() != null) {
            importYleisetTavoitteet(lukioSisalto.getOpetuksenYleisetTavoitteet(), ops);
        }
        return ops;
    }

    private void importLukioRakenne(LukioOpetussuunnitelmaRakenneDto from, Opetussuunnitelma to) {
        importOppiaineet(to, from.getOppiaineet(), oa -> {
            to.getOppiaineetReal().add(new OpsOppiaine(oa.getObj(), false));
            to.getOppiaineJarjestykset().add(new LukioOppiaineJarjestys(to, oa.getObj(), oa.getJarjestys()));
        }, null, new HashMap<>());
    }

    private void importOppiaineet(Opetussuunnitelma ops,
                                  Collection<LukioPerusteOppiaineDto> from, Consumer<Jarjestetty<Oppiaine>> to,
                                  Oppiaine parent, Map<UUID, Lukiokurssi> kurssit) {
        for (LukioPerusteOppiaineDto oppiaine : from) {
            Oppiaine oa = new Oppiaine(oppiaine.getTunniste());
            oa.setTyyppi(OppiaineTyyppi.LUKIO);
            oa.setNimi(LokalisoituTeksti.of(oppiaine.getNimi().getTekstit()));
            oa.setOppiaine(parent);
            oa.setAbstrakti(oppiaine.getAbstrakti());
            oa.setKoosteinen(oppiaine.isKoosteinen());
            oa.setKoodiArvo(oppiaine.getKoodiArvo());
            oa.setKoodiUri(oppiaine.getKoodiUri());
            for (Map.Entry<LukiokurssiTyyppi, Optional<LokalisoituTekstiDto>> kv : oppiaine.getKurssiTyyppiKuvaukset().entrySet()) {
                kv.getKey().oppiaineKuvausSetter().set(oa, kv.getValue().map(LokalisoituTekstiDto::getTekstit)
                        .map(LokalisoituTeksti::of).orElse(null));
            }
            to.accept(new Jarjestetty<>(oa, oppiaine.getJarjestys()));
            importOppiaineet(ops, oppiaine.getOppimaarat(), child -> {
                oa.getOppimaaratReal().add(child.getObj());
                ops.getOppiaineJarjestykset().add(new LukioOppiaineJarjestys(ops, child.getObj(), child.getJarjestys()));
            }, oa, kurssit);
            importKurssit(ops, oppiaine.getKurssit(), oa, kurssit);
        }
    }

    private void importKurssit(Opetussuunnitelma ops, Set<LukiokurssiPerusteDto> from, Oppiaine to,
                               Map<UUID, Lukiokurssi> luodut) {
        for (LukiokurssiPerusteDto kurssiDto : from) {
            ops.getLukiokurssit().add(new OppiaineLukiokurssi(ops, to, kurssiByTunniste(kurssiDto, luodut),
                    kurssiDto.getJarjestys(), true));
        }
    }

    private Lukiokurssi kurssiByTunniste(LukiokurssiPerusteDto kurssiDto, Map<UUID, Lukiokurssi> luodut) {
        Lukiokurssi kurssi = luodut.get(kurssiDto.getTunniste());
        if (kurssi != null) {
            return kurssi;
        }
        kurssi = new Lukiokurssi(kurssiDto.getTunniste());
        kurssi.setNimi(LokalisoituTeksti.of(kurssiDto.getNimi().getTekstit()));
        kurssi.setTyyppi(LukiokurssiTyyppi.ofPerusteTyyppi(kurssiDto.getTyyppi()));
        kurssi.setKoodiArvo(kurssiDto.getKoodiArvo());
        kurssi.setKoodiUri(kurssiDto.getKoodiUri());
        kurssi.setLaajuus(BigDecimal.ONE);
        kurssi.setLokalisoituKoodi(kurssiDto.getLokalisoituKoodi() == null ? null
                : LokalisoituTeksti.of(kurssiDto.getLokalisoituKoodi().getTekstit()));
        luodut.put(kurssi.getTunniste(), kurssi);
        return kurssi;
    }

    private void importAihekokonaisuudet(AihekokonaisuudetDto from, Opetussuunnitelma to) {
        if (to.getAihekokonaisuudet() == null) {
            to.setAihekokonaisuudet(new Aihekokonaisuudet(to, from.getUuidTunniste()));
        }
        Long maxJnro = 0L;
        Map<UUID, Aihekokonaisuus> byTunniste = to.getAihekokonaisuudet().getAihekokonaisuudet().stream()
                .collect(toMap(Aihekokonaisuus::getTunniste, ak -> ak));
        for (AihekokonaisuusDto aihekokonaisuusDto : from.getAihekokonaisuudet()) {
            if (byTunniste.containsKey(aihekokonaisuusDto.getTunniste())) {
                continue;
            }
            Aihekokonaisuus aihekokonaisuus = new Aihekokonaisuus(to.getAihekokonaisuudet(), aihekokonaisuusDto.getTunniste());
            aihekokonaisuus.setOtsikko(LokalisoituTeksti.of(aihekokonaisuusDto.getOtsikko().getTekstit()));
            maxJnro = Math.max(maxJnro + 1, ofNullable(aihekokonaisuus.getJnro()).orElse(0L));
            aihekokonaisuus.setJnro(maxJnro);
            to.getAihekokonaisuudet().getAihekokonaisuudet().add(aihekokonaisuus);
        }
    }

    private void importYleisetTavoitteet(OpetuksenYleisetTavoitteetDto from, Opetussuunnitelma to) {
        if (to.getOpetuksenYleisetTavoitteet() == null) {
            to.setOpetuksenYleisetTavoitteet(new OpetuksenYleisetTavoitteet(to, from.getUuidTunniste()));
        }
    }

    @Override
    public void syncPohja(Long pohjaId) {
        Opetussuunnitelma pohja = opetussuunnitelmaRepository.findOne(pohjaId);
        if (pohja.getPohja() != null) {
            throw new BusinessRuleViolationException("OPS ei ollut pohja");
        }

        pohja.setOppiaineet(null);
        pohja.setVuosiluokkakokonaisuudet(null);
        pohja.getLukiokurssit().clear();
        pohja.getOppiaineJarjestykset().clear();
        pohja.setViimeisinSyncPvm(new Date());

        PerusteDto peruste = eperusteetService.getPerusteUpdateCache(pohja.getPerusteenDiaarinumero());
        pohja.setCachedPeruste(perusteCacheRepository.findNewestEntryForPeruste(peruste.getId()));
        cacheManager.getCache("perusteet").evict(peruste.getId());
        lisaaPerusteenSisalto(pohja, peruste, null);
    }

    private Opetussuunnitelma lisaaPerusteenSisalto(
            Opetussuunnitelma ops,
            PerusteDto peruste,
            OpetussuunnitelmaLuontiDto pohjaDto
    ) {
        if (peruste.getKoulutustyyppi() == null || KoulutusTyyppi.PERUSOPETUS == peruste.getKoulutustyyppi()) {
            return addPohjaPerusopetus(ops, peruste, pohjaDto);
        } else if (KoulutusTyyppi.LISAOPETUS == peruste.getKoulutustyyppi()
                || KoulutusTyyppi.ESIOPETUS == peruste.getKoulutustyyppi()
                || KoulutusTyyppi.AIKUISTENPERUSOPETUS == peruste.getKoulutustyyppi()
                || KoulutusTyyppi.TPO == peruste.getKoulutustyyppi()
                || KoulutusTyyppi.PERUSOPETUSVALMISTAVA == peruste.getKoulutustyyppi()
                || KoulutusTyyppi.VARHAISKASVATUS == peruste.getKoulutustyyppi()) {
            return addPohjaLisaJaEsiopetus(ops, peruste, pohjaDto);
        } else if (KoulutustyyppiToteutus.LOPS2019.equals(peruste.getToteutus())) {
            return addPohjaLops2019(ops, peruste);
        } else if (KoulutustyyppiToteutus.PERUSOPETUS.equals(peruste.getToteutus())) {
            return addPohjaPerusopetus(ops, peruste, pohjaDto);
        } else if (peruste.getKoulutustyyppi().isLukio()) {
            return addPohjaLukiokoulutus(ops, peruste);
        } else {
            throw new BusinessRuleViolationException("Ei toimintatapaa perusteen koulutustyypille");
        }
    }

    private Opetussuunnitelma addPohjaLops2019(Opetussuunnitelma ops, PerusteDto peruste) {
        lisaaTekstipuuPerusteesta(peruste.getLops2019().getSisalto(), ops);
        ops = opetussuunnitelmaRepository.save(ops);
        ops.setKoulutustyyppi(peruste.getKoulutustyyppi());
        ops.setToteutus(KoulutustyyppiToteutus.LOPS2019);
        ops.setCachedPeruste(perusteCacheRepository.findNewestEntryForPeruste(peruste.getId()));
        Lops2019Sisalto sisalto = new Lops2019Sisalto();
        sisalto.setOpetussuunnitelma(ops);
        ops.setLops2019(sisalto);
        return ops;
    }

    @Override
    public OpetussuunnitelmaDto addPohja(OpetussuunnitelmaLuontiDto pohjaDto) {
        if (pohjaDto.getId() != null) {
            throw new BusinessRuleViolationException("Uudessa pohjassa on id");
        }

        Opetussuunnitelma pohja = mapper.map(pohjaDto, Opetussuunnitelma.class);
        // Jokainen pohja sisältää OPH:n organisaationaan
        pohja.getOrganisaatiot().add(SecurityUtil.OPH_OID);

        Set<String> userOids = SecurityUtil.getOrganizations(EnumSet.of(RolePermission.CRUD));
        if (CollectionUtil.intersect(userOids, pohja.getOrganisaatiot()).isEmpty()) {
            throw new BusinessRuleViolationException("Käyttäjällä ei ole luontioikeutta " +
                    "opetussuunnitelman pohjan organisaatioissa");
        }

        final String diaarinumero = pohja.getPerusteenDiaarinumero();
        if (StringUtils.isBlank(diaarinumero)) {
            throw new BusinessRuleViolationException("Perusteen diaarinumeroa ei ole määritelty");
        }
        else {
            List<PerusteInfoDto> perusteet = eperusteetService.findPerusteet();
            if (perusteet.stream().noneMatch(p -> diaarinumero.equals(p.getDiaarinumero()))) {
                throw new BusinessRuleViolationException("Diaarinumerolla " + diaarinumero +
                        " ei löydy voimassaolevaa perustetta");
            }
        }

        if (pohja.getPohja() != null) {
            throw new BusinessRuleViolationException("Opetussuunnitelman pohjalla ei voi olla pohjaa");
        }


        PerusteDto peruste = eperusteetService.getPeruste(pohja.getPerusteenDiaarinumero());
        if (peruste == null) {
            throw new BusinessRuleViolationException("perustetta-ei-loytynyt");
        }

        pohja.setTila(Tila.LUONNOS);
        pohja.setToteutus(peruste.getToteutus());
        pohja.setKoulutustyyppi(peruste.getKoulutustyyppi());
        lisaaTekstipuunJuuri(pohja);
        pohja = opetussuunnitelmaRepository.save(pohja);

        if (!KoulutustyyppiToteutus.LOPS2019.equals(peruste.getToteutus()) && !pohjaDto.isRakennePohjasta()) {
            lisaaTekstipuunLapset(pohja);
        }

        pohja.setCachedPeruste(perusteCacheRepository.findNewestEntryForPeruste(peruste.getId()));
        pohja.setKoulutustyyppi(peruste.getKoulutustyyppi() != null ? peruste.getKoulutustyyppi()
                : KoulutusTyyppi.PERUSOPETUS);
        pohja = lisaaPerusteenSisalto(pohja, peruste, pohjaDto);
        return mapper.map(pohja, OpetussuunnitelmaDto.class);
    }

    private void lisaaTekstipuuPerusteesta(
            fi.vm.sade.eperusteet.ylops.service.external.impl.perustedto.TekstiKappaleViiteDto sisalto,
            Opetussuunnitelma pohja
    ) {
        TekstiKappaleViite tekstiKappaleViite = CollectionUtil.mapRecursive(sisalto,
                fi.vm.sade.eperusteet.ylops.service.external.impl.perustedto.TekstiKappaleViiteDto::getLapset,
                TekstiKappaleViite::getLapset,
                viiteDto -> {
                    TekstiKappale kpl = new TekstiKappale();
                    TekstiKappaleViite result = new TekstiKappaleViite();
                    if (viiteDto.getTesktiKappale() != null) {
                        TekstiKappale tk = mapper.map(viiteDto.getTesktiKappale(), TekstiKappale.class);
                        result.setPerusteTekstikappaleId(tk.getId());
                        kpl.setNimi(tk.getNimi());
                    }
                    kpl.setId(null);
                    kpl.setTila(Tila.LUONNOS);
                    kpl.setValmis(false);
                    result.setTekstiKappale(tekstiKappaleRepository.save(kpl));
                    result.setPakollinen(true);
                    result.setOmistussuhde(Omistussuhde.OMA);
                    result.setLapset(new ArrayList<>());
                    return result;
                });
        tekstiKappaleViite.kiinnitaHierarkia(null);
        TekstiKappaleViite viite = viiteRepository.saveAndFlush(tekstiKappaleViite);
        pohja.setTekstit(viite);
    }

    private TekstiKappaleViite tekstikappaleViiteRootSisallosta(fi.vm.sade.eperusteet.ylops.service.external.impl.perustedto.TekstiKappaleViiteDto sisalto) {
        return CollectionUtil.mapRecursive(sisalto,
                fi.vm.sade.eperusteet.ylops.service.external.impl.perustedto.TekstiKappaleViiteDto::getLapset,
                TekstiKappaleViite::getLapset,
                viiteDto -> {
                    TekstiKappale kpl = new TekstiKappale();
                    TekstiKappaleViite result = new TekstiKappaleViite();
                    if (viiteDto.getTesktiKappale() != null) {
                        result.setPerusteTekstikappaleId(viiteDto.getTesktiKappale().getId());

                        TekstiKappale tk = mapper.map(viiteDto.getTesktiKappale(), TekstiKappale.class);
                        kpl.setNimi(tk.getNimi());
                    }
                    kpl.setId(null);
                    kpl.setTila(Tila.LUONNOS);
                    kpl.setValmis(false);
                    result.setTekstiKappale(tekstiKappaleRepository.save(kpl));
                    result.setPakollinen(true);
                    result.setOmistussuhde(Omistussuhde.OMA);
                    result.setLapset(new ArrayList<>());
                    return result;
                });
    }

    private void lisaaTekstipuuPerusteesta(PerusteTekstiKappaleViiteDto sisalto, Opetussuunnitelma pohja) {
        TekstiKappaleViite tekstiKappaleViite = CollectionUtil.mapRecursive(sisalto,
                PerusteTekstiKappaleViiteDto::getLapset,
                TekstiKappaleViite::getLapset,
                viiteDto -> {
                    TekstiKappale kpl = new TekstiKappale();
                    TekstiKappaleViite result = new TekstiKappaleViite();
                    if (viiteDto.getPerusteenOsa() != null) {
                        TekstiKappale tk = mapper.map(viiteDto.getPerusteenOsa(), TekstiKappale.class);
                        result.setPerusteTekstikappaleId(tk.getId());
                        kpl.setNimi(tk.getNimi());
                        if (viiteDto.getPerusteenOsa() != null && viiteDto.getPerusteenOsa().getLiite() != null) {
                            result.setLiite(viiteDto.getPerusteenOsa().getLiite()
                                    ? viiteDto.getPerusteenOsa().getLiite() : false);
                        }
                    }
                    kpl.setId(null);
                    kpl.setTila(Tila.LUONNOS);
                    kpl.setValmis(false);
                    result.setTekstiKappale(tekstiKappaleRepository.save(kpl));
                    result.setPakollinen(true);
                    result.setOmistussuhde(Omistussuhde.OMA);
                    result.setLapset(new ArrayList<>());
                    return result;
                });
        tekstiKappaleViite.kiinnitaHierarkia(null);
        TekstiKappaleViite viite = viiteRepository.saveAndFlush(tekstiKappaleViite);
        pohja.setTekstit(viite);
    }

    private void lisaaTekstipuunJuuri(Opetussuunnitelma ops) {
        TekstiKappaleViite juuri = new TekstiKappaleViite(Omistussuhde.OMA);
        juuri = viiteRepository.saveAndFlush(juuri);
        ops.setTekstit(juuri);
    }

    private void lisaaTekstipuunLapset(Opetussuunnitelma ops) {
        LokalisoituTekstiDto nimi, teksti;
        nimi = new LokalisoituTekstiDto(null, Collections.singletonMap(Kieli.FI, "Opetuksen järjestäminen"));
        teksti = new LokalisoituTekstiDto(null, null);
        TekstiKappaleDto ohjeistusTeksti = new TekstiKappaleDto(nimi, teksti, Tila.LUONNOS);
        TekstiKappaleViiteDto.Matala ohjeistus = new TekstiKappaleViiteDto.Matala(ohjeistusTeksti);
        addTekstiKappale(ops.getId(), ohjeistus);

        nimi = new LokalisoituTekstiDto(null, Collections.singletonMap(Kieli.FI,
                "Opetuksen toteuttamisen lähtökohdat"));
        teksti = new LokalisoituTekstiDto(null, null);
        TekstiKappaleDto opetuksenJarjestaminenTeksti
                = new TekstiKappaleDto(nimi, teksti, Tila.LUONNOS);
        TekstiKappaleViiteDto.Matala opetuksenJarjestaminen
                = new TekstiKappaleViiteDto.Matala(opetuksenJarjestaminenTeksti);
        addTekstiKappale(ops.getId(), opetuksenJarjestaminen);
    }

    private void flattenTekstikappaleviitteet(Map<UUID, TekstiKappaleViite> viitteet, TekstiKappaleViite tov) {
        if (tov.getLapset() == null) {
            return;
        }
        for (TekstiKappaleViite lapsi : tov.getLapset()) {
            // Tätä tarkistusta ei välttämättä tarvitse
            if (viitteet.get(lapsi.getTekstiKappale().getTunniste()) != null) {
                continue;
            }
            viitteet.put(lapsi.getTekstiKappale().getTunniste(), lapsi);
            flattenTekstikappaleviitteet(viitteet, lapsi);
        }
    }

    @Override
    public void updateLapsiOpetussuunnitelmat(Long opsId) {
        Opetussuunnitelma ops = opetussuunnitelmaRepository.findOne(opsId);
        assertExists(ops, "Päivitettävää tietoa ei ole olemassa");
        Set<Opetussuunnitelma> aliopsit = opetussuunnitelmaRepository.findAllByPohjaId(opsId);

        for (Opetussuunnitelma aliops : aliopsit) {
            Map<UUID, TekstiKappaleViite> aliopsTekstit = new HashMap<>();
            flattenTekstikappaleviitteet(aliopsTekstit, aliops.getTekstit());
            aliops.getTekstit().getLapset().clear();
            aliopsTekstit.values().forEach((teksti) -> {
                teksti.setVanhempi(aliops.getTekstit());
                teksti.getLapset().clear();
            });
        }
    }

    @Override
    public OpetussuunnitelmaDto updateOpetussuunnitelma(OpetussuunnitelmaDto opetussuunnitelmaDto) {
        Opetussuunnitelma ops = opetussuunnitelmaRepository.findOne(opetussuunnitelmaDto.getId());
        assertExists(ops, "Päivitettävää tietoa ei ole olemassa");

        poistaKielletytMuutokset(ops, opetussuunnitelmaDto);

        validoiMuutokset(ops, opetussuunnitelmaDto);

        mapper.map(opetussuunnitelmaDto, ops);
        ops = opetussuunnitelmaRepository.save(ops);

        muokkaustietoService.addOpsMuokkausTieto(ops.getId(), ops, MuokkausTapahtuma.PAIVITYS);

        return mapper.map(ops, OpetussuunnitelmaDto.class);
    }

    @Override
    public OpetussuunnitelmaDto importPerusteTekstit(Long id) {
        return importPerusteTekstit(id, false);
    }

    @Override
    public OpetussuunnitelmaDto importPerusteTekstit(Long id, boolean skip) {
        Opetussuunnitelma ops = opetussuunnitelmaRepository.findOne(id);
        assertExists(ops, "opetussuunnitelmaa ei lyödy");

        if (!skip) {
            PerusteDto peruste = getPeruste(id);
            if (!KoulutusTyyppi.PERUSOPETUS.equals(peruste.getKoulutustyyppi())
                    && !KoulutusTyyppi.TPO.equals(peruste.getKoulutustyyppi())
                    && !KoulutusTyyppi.VARHAISKASVATUS.equals(peruste.getKoulutustyyppi())
                    && !KoulutusTyyppi.ESIOPETUS.equals(peruste.getKoulutustyyppi())) {
                throw new BusinessRuleViolationException("koulutustyyppi-ei-tuettu");
            }

            fi.vm.sade.eperusteet.ylops.service.external.impl.perustedto.TekstiKappaleViiteDto viiteDto = null;
            if (KoulutusTyyppi.PERUSOPETUS.equals(peruste.getKoulutustyyppi())) {
                viiteDto = peruste.getPerusopetus().getSisalto();
            } else if (KoulutusTyyppi.TPO.equals(peruste.getKoulutustyyppi())) {
                viiteDto = peruste.getTpo().getSisalto();
            } else if (KoulutusTyyppi.VARHAISKASVATUS.equals(peruste.getKoulutustyyppi()) || KoulutusTyyppi.ESIOPETUS.equals(peruste.getKoulutustyyppi())) {
                viiteDto = peruste.getEsiopetus().getSisalto();
            }

            List<Long> vanhatIdt = new ArrayList<>();
            ops.getTekstit().getLapset().forEach(tekstikappaleViite -> {
                TekstiKappaleViiteDto.Matala tekstikappale = tarkistaJaKorjaaTekstikappaleViiteOmistussuhde(id, tekstikappaleViite.getId());
                Map<Kieli, String> nimet = tekstikappale.getTekstiKappale().getNimi().getTekstit();
                nimet.replaceAll((kieli, teksti) -> teksti + " (vanha)");
                tekstikappale.getTekstiKappale().setNimi(new LokalisoituTekstiDto(null, nimet));
                tekstikappale.setPakollinen(false);
                paivitaTekstikappaleViiteLapsetPakollisuusRec(id, tekstikappale.getLapset(), false);
                tekstiKappaleViiteService.updateTekstiKappaleViite(id, tekstikappale.getId(), tekstikappale);
                vanhatIdt.add(tekstikappaleViite.getId());
            });

            tekstikappaleViiteRootSisallosta(viiteDto).getLapset().forEach(perusteTekstikappale -> {
                addTekstikappaleViiteRec(id, ops.getTekstit().getId(), perusteTekstikappale);
            });

            TekstiKappaleViiteDto.Puu tekstikappale = getTekstit(id, TekstiKappaleViiteDto.Puu.class);
            tekstikappale.getLapset().sort(Comparator.comparing(tkLapsi -> vanhatIdt.contains(tkLapsi.getId())));
            tekstiKappaleViiteService.reorderSubTree(id, tekstikappale.getId(), tekstikappale);
        }
        ops.setPerusteDataTuontiPvm(new Date());

        return updateOpetussuunnitelma(mapper.map(ops, OpetussuunnitelmaDto.class));
    }

    private void addTekstikappaleViiteRec(Long opsId, Long parentId, TekstiKappaleViite viite) {
        TekstiKappaleViiteDto.Matala lisatty = tekstiKappaleViiteService.addTekstiKappaleViite(opsId, parentId, mapper.map(viite, TekstiKappaleViiteDto.Matala.class));
        if (CollectionUtils.isNotEmpty(viite.getLapset())) {
            viite.getLapset().forEach(lapsiviite -> {
                addTekstikappaleViiteRec(opsId, lisatty.getId(), lapsiviite);
            });
        }
    }

    private void paivitaTekstikappaleViiteLapsetPakollisuusRec(Long opsId, List<Reference> lapset, boolean pakollisuus) {
        if (CollectionUtils.isNotEmpty(lapset)) {
            lapset.forEach(lapsi -> {
                TekstiKappaleViiteDto.Matala tekstikappale = tarkistaJaKorjaaTekstikappaleViiteOmistussuhde(opsId, Long.valueOf(lapsi.getId()));
                tekstikappale.setPakollinen(pakollisuus);
                tekstiKappaleViiteService.updateTekstiKappaleViite(opsId, tekstikappale.getId(), tekstikappale);
                paivitaTekstikappaleViiteLapsetPakollisuusRec(opsId, tekstikappale.getLapset(), pakollisuus);
            });
        }
    }

    private TekstiKappaleViiteDto.Matala tarkistaJaKorjaaTekstikappaleViiteOmistussuhde(Long opsId, Long viiteId) {
        TekstiKappaleViiteDto.Matala tekstikappale = tekstiKappaleViiteService.getTekstiKappaleViite(opsId, viiteId);
        if (tekstikappale.getOmistussuhde() != Omistussuhde.OMA) {
            return tekstiKappaleViiteService.kloonaaTekstiKappale(opsId, viiteId, TekstiKappaleViiteDto.Matala.class);
        }

        return tekstikappale;
    }

    private void poistaKielletytMuutokset(Opetussuunnitelma ops, OpetussuunnitelmaDto opetussuunnitelmaDto) {
        // Ei sallita pohjan muuttamista
        opetussuunnitelmaDto.setPohja(mapper.map(ops.getPohja(), OpetussuunnitelmaNimiDto.class));

        // EP-1611
        if (ops.getKoulutustyyppi().isYksinkertainen() && ops.getTyyppi().equals(Tyyppi.POHJA)) {

            // Tarkistetaan perusteen perusteen olemassaolo
            final String diaarinumero = opetussuunnitelmaDto.getPerusteenDiaarinumero();
            if (StringUtils.isBlank(diaarinumero)) {
                throw new BusinessRuleViolationException("Uuden perusteen diaarinumeroa ei ole määritelty");
            } else if (eperusteetService.findPerusteet().stream()
                    .noneMatch(p -> diaarinumero.equals(p.getDiaarinumero()))) {
                throw new BusinessRuleViolationException("Diaarinumerolla " + diaarinumero +
                        " ei löydy voimassaolevaa perustetta");
            }

            // Haetaan uusi peruste
            PerusteDto peruste = eperusteetService.getPeruste(diaarinumero);

            // Tarkistetaan perusteen koulutustyyppi
            if (!ops.getKoulutustyyppi().equals(peruste.getKoulutustyyppi())) {
                throw new BusinessRuleViolationException("Uuden perusteen koulutustyyppi ei saa muuttua");
            }

            // Asetetaan uusi diaarinumero ja cached peruste
            ops.setPerusteenDiaarinumero(opetussuunnitelmaDto.getPerusteenDiaarinumero());

            PerusteCache cachedPeruste = perusteCacheRepository.findNewestEntryForPeruste(peruste.getId());
            if (cachedPeruste != null && cachedPeruste.getId() != null) {
                opetussuunnitelmaDto.setPerusteenId(peruste.getId());
                ops.setCachedPeruste(cachedPeruste);
            }

        } else {
            // Ei sallita perusteen diaarinumeron muuttamista
            opetussuunnitelmaDto.setPerusteenDiaarinumero(ops.getPerusteenDiaarinumero());

            // Ei sallita peruste cachen muuttamista
            PerusteCache cachedPeruste = ops.getCachedPeruste();
            if (cachedPeruste != null && cachedPeruste.getPerusteId() != null) {
                opetussuunnitelmaDto.setPerusteenId(cachedPeruste.getPerusteId());
            } else {
                opetussuunnitelmaDto.setPerusteenId(null);
            }
        }

        // Tilan muuttamiseen on oma erillinen endpointtinsa
        opetussuunnitelmaDto.setTila(ops.getTila());
    }

    private void validoiMuutokset(Opetussuunnitelma ops, OpetussuunnitelmaDto opetussuunnitelmaDto) {
        if (CollectionUtils.isNotEmpty(ops.getKunnat()) && CollectionUtils.isEmpty(opetussuunnitelmaDto.getKunnat())) {
            throw new BusinessRuleViolationException("Kuntia ei voi poistaa");
        }

        if (CollectionUtils.isNotEmpty(ops.getOrganisaatiot()) && CollectionUtils.isEmpty(opetussuunnitelmaDto.getOrganisaatiot())) {
            throw new BusinessRuleViolationException("Organisaatioita ei voi poistaa");
        }
        
        // Käyttäjällä ei oikeutta tulevassa organisaatiossa
        Set<String> userOids = SecurityUtil.getOrganizations(EnumSet.of(RolePermission.CRUD,
                RolePermission.ADMIN));
        Set<String> organisaatiot = opetussuunnitelmaDto.getOrganisaatiot().stream()
                .map(OrganisaatioDto::getOid)
                .collect(toSet());
        if (CollectionUtil.intersect(userOids, organisaatiot).isEmpty()) {
            throw new BusinessRuleViolationException("Käyttäjällä ei ole oikeuksia organisaatiossa");
        }


        if (opetussuunnitelmaDto.getTyyppi() != ops.getTyyppi()) {
            throw new BusinessRuleViolationException("Opetussuunnitelman tyyppiä ei voi vaihtaa");
        }

        // Ei sallita kieli ja vuoluokkakokonaisuuksien muutoksia kuin luonnostilassa
        if (opetussuunnitelmaDto.getTila() != Tila.LUONNOS) {
            if (!opetussuunnitelmaDto.getVuosiluokkakokonaisuudet().stream()
                    .map(vlk -> vlk.getVuosiluokkakokonaisuus().getId())
                    .collect(Collectors.toSet())
                    .equals(ops.getVuosiluokkakokonaisuudet().stream()
                            .map(vlk -> vlk.getVuosiluokkakokonaisuus().getId())
                            .collect(Collectors.toSet()))) {
                throw new BusinessRuleViolationException("Opetussuunnitelman vuosiluokkakokonaisuuksia ei voi vaihtaa kuin luonnoksessa");
            }

            if (!new HashSet<>(opetussuunnitelmaDto.getJulkaisukielet())
                    .equals(new HashSet<>(ops.getJulkaisukielet()))) {
                throw new BusinessRuleViolationException("Opetussuunnitelman julkaisukieliä ei voi vaihtaa kuin luonnoksessa");
            }
        }

        {
            Long pohjaId = opetussuunnitelmaDto.getPohja() != null ? opetussuunnitelmaDto.getPohja().getId() : null;
            Long oldPohjaId = ops.getPohja() != null ? ops.getPohja().getId() : null;
            if (!Objects.equals(pohjaId, oldPohjaId)) {
                throw new BusinessRuleViolationException("Opetussuunnitelman pohjaa ei voi vaihtaa");
            }
        }

        if (!Objects.equals(opetussuunnitelmaDto.getPerusteenDiaarinumero(), ops.getPerusteenDiaarinumero())) {
            throw new BusinessRuleViolationException("Perusteen diaarinumeroa ei voi vaihtaa");
        }

        if (!Objects.equals(opetussuunnitelmaDto.getPerusteenId(), ops.getCachedPeruste() != null ? ops.getCachedPeruste().getPerusteId() : null)) {
            throw new BusinessRuleViolationException("Opetussuunnitelman perustetta ei voi vaihtaa");
        }

        if (opetussuunnitelmaDto.getOrganisaatiot().isEmpty()) {
            throw new BusinessRuleViolationException("Organisaatiolista ei voi olla tyhjä");
        }
    }

    private Validointi validoiOpetussuunnitelma(Opetussuunnitelma ops) {
        Validointi validointi = new Validointi();

        Set<Kieli> julkaisukielet = ops.getJulkaisukielet();

        validateOpetussuunnitelmaTiedot(ops, validointi);
        validoiPaikallisetOpintojaksot(ops, validointi);
        validateTextHierarchy(ops, julkaisukielet, validointi);

        ops.getVuosiluokkakokonaisuudet().stream()
                .filter(OpsVuosiluokkakokonaisuus::isOma)
                .map(OpsVuosiluokkakokonaisuus::getVuosiluokkakokonaisuus)
                .forEach(vlk -> Vuosiluokkakokonaisuus.validoi(validointi, vlk, julkaisukielet));

        //TODO Should we use same version of Peruste for with the Opetuusuunnitelma was based on if available?
        PerusteDto peruste = eperusteetService.getPeruste(ops.getPerusteenDiaarinumero());
        if (peruste.getPerusopetus() != null) {
            ops.getOppiaineet().stream()
                    .filter(OpsOppiaine::isOma)
                    .map(OpsOppiaine::getOppiaine)
                    .forEach(oa -> peruste.getPerusopetus().getOppiaine(oa.getTunniste()).ifPresent(poppiaine -> {
                        Oppiaine.validoi(validointi, oa, julkaisukielet);
                        Set<UUID> PerusteenTavoitteet = new HashSet<>();

                        poppiaine.getVuosiluokkakokonaisuudet()
                                .forEach(vlk -> vlk.getTavoitteet()
                                        .forEach(tavoite -> PerusteenTavoitteet.add(tavoite.getTunniste())));

                        Set<UUID> OpsinTavoitteet = oa.getVuosiluokkakokonaisuudet().stream()
                                .flatMap(vlk -> vlk.getVuosiluokat().stream())
                                .map(Oppiaineenvuosiluokka::getTavoitteet)
                                .flatMap(Collection::stream)
                                .map(Opetuksentavoite::getTunniste)
                                .collect(Collectors.toSet());

                    }));
        }

        return validointi;
    }

    private void validoiPaikallisetOpintojaksot(Opetussuunnitelma ops, Validointi validointi) {
        if(KoulutustyyppiToteutus.LOPS2019.equals(ops.getToteutus()) && !lops2019OpintojaksoService.tarkistaOpintojaksot(ops.getId())){
            validointi.virhe("ops-paikallinen-opintojakso-rakennevirhe");
        }
    }

    private void validateOpetussuunnitelmaTiedot(Opetussuunnitelma ops, Validointi validointi) {
        if (ops.getPerusteenDiaarinumero().isEmpty()) {
            validointi.virhe("opsilla-ei-perusteen-diaarinumeroa");
        }
    }

    private void validateTextHierarchy(Opetussuunnitelma ops, Set<Kieli> julkaisukielet, Validointi validointi) {
        if (ops.getTekstit() != null && ops.getTekstit().getLapset() != null) {
            for (TekstiKappaleViite teksti : ops.getTekstit().getLapset()) {
                TekstiKappaleViite.validoi(validointi, teksti, julkaisukielet);
            }
        }
    }

    private void validoiOhjeistus(TekstiKappaleViite tkv, Set<Kieli> kielet) {
        Validointi validointi = new Validointi();
        for (TekstiKappaleViite lapsi : tkv.getLapset()) {
            Ohje ohje = ohjeRepository.findFirstByKohde(lapsi.getTekstiKappale().getTunniste());

            if (ohje != null && (ohje.getTeksti() == null || !ohje.getTeksti().hasKielet(kielet))) {
                validointi.virhe("ops-pohja-ohjeistus-puuttuu", tkv.getTekstiKappale().getNimi(), tkv.getTekstiKappale().getNimi());
            } else {
                validointi.virhe("ops-pohja-ohjeistus-puuttuu");
            }
            validoiOhjeistus(lapsi, kielet);
        }
        validointi.tuomitse();
    }

    private Validointi validoiPohja(Opetussuunnitelma ops) {
        return new Validointi();
    }

    @Override
    public OpetussuunnitelmaDto updateTila(Long id, Tila tila) {
        Opetussuunnitelma ops = opetussuunnitelmaRepository.findOne(id);
        assertExists(ops, "Opetussuunnitelmaa ei ole olemassa");

        if (ops.getTyyppi() == Tyyppi.POHJA && tila == Tila.JULKAISTU) {
            tila = Tila.VALMIS;
        }

        if (ops.getTyyppi() == Tyyppi.OPS && ops.getTila() == Tila.JULKAISTU && tila == Tila.VALMIS) {
            ops.setTila(tila);
            ops = opetussuunnitelmaRepository.save(ops);
        }

        if (tila != ops.getTila() && ops.getTila().mahdollisetSiirtymat(ops.getTyyppi()
                == Tyyppi.POHJA).contains(tila)) {
            if (ops.getTyyppi() == Tyyppi.OPS && (tila == Tila.JULKAISTU)) {
                Validointi validointi = validoiOpetussuunnitelma(ops);
                validointi.tuomitse();
                for (Kieli kieli : ops.getJulkaisukielet()) {
                    try {
                        dokumenttiService.autogenerate(ops.getId(), kieli);
                    } catch (DokumenttiException e) {
                        logger.error(e.getLocalizedMessage(), e.getCause());
                    }
                }
            } else if (ops.getTyyppi() == Tyyppi.POHJA && tila == Tila.VALMIS) {
                Validointi validointi = validoiPohja(ops);
                validointi.tuomitse();
            }
            
            if (tila == Tila.VALMIS && ops.getTila() == Tila.LUONNOS && ops.getTyyppi() != Tyyppi.POHJA &&
                    ops.getKoulutustyyppi().isLukio()) {
                Validointi validointi = validoiLukioPohja(ops);
                validointi.tuomitse();
            }
            ops.setTila(tila);
            ops = opetussuunnitelmaRepository.save(ops);

            muokkaustietoService.addOpsMuokkausTieto(id, ops, MuokkausTapahtuma.PAIVITYS, "tapahtuma-opetussuunnitelma-tila-" + tila);
        }

        return mapper.map(ops, OpetussuunnitelmaDto.class);
    }

    @Override
    public List<Validointi> validoiOpetussuunnitelma(Long id) {
        Opetussuunnitelma ops = opetussuunnitelmaRepository.findOne(id);
        assertExists(ops, "Opetussuunnitelmaa ei ole olemassa");

        List<Validointi> result = new ArrayList<>();

        switch (ops.getTyyppi()) {
            case OPS:
                result.add(validoiOpetussuunnitelma(ops));
                break;
            default:
                result.add(validoiPohja(ops));
                break;
        }

        switch (ops.getKoulutustyyppi()) {
            case LUKIOKOULUTUS:
            case LUKIOVALMISTAVAKOULUTUS:
                result.add(validoiLukioPohja(ops));
                break;
            default:
                break;
        }
        return result;
    }

    private Validointi validoiLukioPohja(Opetussuunnitelma ops) {
        Validointi validointi = new Validointi();

        if (ops.getOppiaineet().isEmpty()) {
            logger.error("lukio-ei-oppiaineita");
            validointi.virhe("lukio-ei-oppiaineita", ops.getNimi());
        }

        if (ops.getAihekokonaisuudet() == null || ops.getAihekokonaisuudet().getAihekokonaisuudet().isEmpty()) {
            logger.error("lukio-ei-aihekokonaisuuksia");
            validointi.virhe("lukio-ei-aihekokonaisuuksia", ops.getNimi());
        }

        ops.getOppiaineet().forEach(opsOppiaine -> {
            if (!opsOppiaine.getOppiaine().isKoosteinen() && !oppiaineHasKurssi(opsOppiaine.getOppiaine(), ops.getLukiokurssit())) {
                logger.error("lukio-oppiaineessa-ei-kursseja");
                validointi.virhe("lukio-oppiaineessa-ei-kursseja", opsOppiaine.getOppiaine().getNimi());
            }
            if (opsOppiaine.getOppiaine().isKoosteinen() && opsOppiaine.getOppiaine().getOppimaarat().isEmpty()) {
                logger.error("lukio-oppiaineessa-ei-oppimaaria");
                validointi.varoitus("lukio-oppiaineessa-ei-oppimaaria", opsOppiaine.getOppiaine().getNimi());
            }
        });

        ops.getLukiokurssit().forEach(oppiaineLukiokurssi -> {
            if (oppiaineLukiokurssi.getKurssi().getTyyppi().isPaikallinen()) {
                oppiaineLukiokurssi.getKurssi().validoiTavoitteetJaKeskeinenSisalto(validointi, ops.getJulkaisukielet());
            }
        });

        logger.error("lukio-opsin-validointi-epaonnistui", validointi.getVirheet().size());
        return validointi;
    }

    private boolean oppiaineHasKurssi(Oppiaine oppiaine, Set<OppiaineLukiokurssi> lukiokurssit) {
        for (OppiaineLukiokurssi oppiaineLukiokurssi : lukiokurssit) {
            if (oppiaineLukiokurssi.getOppiaine().getTunniste().compareTo(oppiaine.getTunniste()) == 0) {
                return true;
            }
        }
        return false;
    }

    @Override
    public OpetussuunnitelmaDto restore(Long id) {
        Opetussuunnitelma ops = opetussuunnitelmaRepository.findOne(id);
        assertExists(ops, "Opetussuunnitelmaa ei ole olemassa");

        ops.setTila(Tila.LUONNOS);
        ops = opetussuunnitelmaRepository.save(ops);
        return mapper.map(ops, OpetussuunnitelmaDto.class);
    }

    @Override
    @Transactional(readOnly = true)
    public <T> T getTekstit(Long opsId, Class<T> t) {
        Opetussuunnitelma ops = opetussuunnitelmaRepository.findOne(opsId);
        assertExists(ops, "Opetussuunnitelmaa ei ole olemassa");
        return mapper.map(ops.getTekstit(), t);
    }

    @Override
    public TekstiKappaleViiteDto.Matala addTekstiKappale(Long opsId, TekstiKappaleViiteDto.Matala viite) {
        Opetussuunnitelma ops = opetussuunnitelmaRepository.findOne(opsId);
        assertExists(ops, "Opetussuunnitelmaa ei ole olemassa");
        // Lisätään viite juurinoden alle
        return tekstiKappaleViiteService.addTekstiKappaleViite(opsId, ops.getTekstit().getId(), viite);
    }

    @Override
    public TekstiKappaleViiteDto.Matala addTekstiKappaleLapsi(Long opsId, Long parentId,
                                                              TekstiKappaleViiteDto.Matala viite) {
        // Lisätään viite parent-noden alle
        return tekstiKappaleViiteService.addTekstiKappaleViite(opsId, parentId, viite);
    }

    @Override
    public void vaihdaPohja(Long id, Long pohjaId) {
        dispatcher.get(KoulutustyyppiToteutus.LOPS2019, OpsPohjanVaihto.class).vaihdaPohja(id, pohjaId);
    }

    @Override
    public Set<OpetussuunnitelmaInfoDto> vaihdettavatPohjat(Long id) {
        return dispatcher.get(KoulutustyyppiToteutus.LOPS2019, OpsPohjanVaihto.class).haeVaihtoehdot(id);
    }

    @Override
    public OpetussuunnitelmaExportDto getExportedOpetussuunnitelma(Long id) {
        final OpetussuunnitelmaExportDto exported = dispatcher.get(id, OpsExport.class).export(id);
        return exported;
    }

    @Override
    @Transactional(noRollbackFor = {NotExistsException.class})
    public TekstiKappaleDto getPerusteTekstikappale(Long opsId, Long tekstikappaleId) {
        Opetussuunnitelma ops = opetussuunnitelmaRepository.findOne(opsId);
        if (ops.getCachedPeruste() != null) {
            PerusteDto perusteDto = eperusteetService.getPerusteById(ops.getCachedPeruste().getPerusteId());
            fi.vm.sade.eperusteet.ylops.service.external.impl.perustedto.TekstiKappaleViiteDto sisalto = perusteDto.getTekstiKappaleViiteSisalto();

            if (sisalto != null) {
                fi.vm.sade.eperusteet.ylops.service.external.impl.perustedto.TekstiKappaleViiteDto perusteenTekstikappaleViite = CollectionUtil.treeToStream(
                        sisalto,
                        fi.vm.sade.eperusteet.ylops.service.external.impl.perustedto.TekstiKappaleViiteDto::getLapset)
                        .filter(viiteDto -> viiteDto.getTesktiKappale() != null
                                && viiteDto.getTesktiKappale().getTeksti() != null
                                && Objects.equals(tekstikappaleId, viiteDto.getTesktiKappale().getId()))
                        .findFirst()
                        .orElseThrow(() -> new NotExistsException("tekstikappaletta-ei-ole"));
                return new TekstiKappaleDto(
                        new LokalisoituTekstiDto(perusteenTekstikappaleViite.getTesktiKappale().getNimi().asMap()),
                        new LokalisoituTekstiDto(perusteenTekstikappaleViite.getTesktiKappale().getTeksti().asMap()),
                        null);
            }
            throw new NotExistsException("tekstikappaletta-ei-ole");
        }

        return null;
    }
}
