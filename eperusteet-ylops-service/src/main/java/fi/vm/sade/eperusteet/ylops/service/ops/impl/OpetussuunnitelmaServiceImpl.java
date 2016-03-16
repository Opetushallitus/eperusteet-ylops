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
import fi.vm.sade.eperusteet.ylops.domain.KoulutusTyyppi;
import fi.vm.sade.eperusteet.ylops.domain.Tila;
import fi.vm.sade.eperusteet.ylops.domain.Tyyppi;
import fi.vm.sade.eperusteet.ylops.domain.Vuosiluokkakokonaisuusviite;
import fi.vm.sade.eperusteet.ylops.domain.lukio.*;
import fi.vm.sade.eperusteet.ylops.domain.ohje.Ohje;
import fi.vm.sade.eperusteet.ylops.domain.oppiaine.Oppiaine;
import fi.vm.sade.eperusteet.ylops.domain.oppiaine.OppiaineTyyppi;
import fi.vm.sade.eperusteet.ylops.domain.oppiaine.Oppiaineenvuosiluokkakokonaisuus;
import fi.vm.sade.eperusteet.ylops.domain.ops.Opetussuunnitelma;
import fi.vm.sade.eperusteet.ylops.domain.ops.OpsOppiaine;
import fi.vm.sade.eperusteet.ylops.domain.ops.OpsVuosiluokkakokonaisuus;
import fi.vm.sade.eperusteet.ylops.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.ylops.domain.teksti.LokalisoituTeksti;
import fi.vm.sade.eperusteet.ylops.domain.teksti.Omistussuhde;
import fi.vm.sade.eperusteet.ylops.domain.teksti.TekstiKappaleViite;
import fi.vm.sade.eperusteet.ylops.domain.vuosiluokkakokonaisuus.Vuosiluokkakokonaisuus;
import fi.vm.sade.eperusteet.ylops.dto.JarjestysDto;
import fi.vm.sade.eperusteet.ylops.dto.koodisto.KoodistoDto;
import fi.vm.sade.eperusteet.ylops.dto.koodisto.KoodistoKoodiDto;
import fi.vm.sade.eperusteet.ylops.dto.koodisto.KoodistoMetadataDto;
import fi.vm.sade.eperusteet.ylops.dto.koodisto.OrganisaatioDto;
import fi.vm.sade.eperusteet.ylops.dto.lukio.LukioAbstraktiOppiaineTuontiDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.*;
import fi.vm.sade.eperusteet.ylops.dto.peruste.PerusopetuksenPerusteenSisaltoDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.PerusteDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.PerusteLaajaalainenosaaminenDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.lukio.*;
import fi.vm.sade.eperusteet.ylops.dto.teksti.LokalisoituTekstiDto;
import fi.vm.sade.eperusteet.ylops.dto.teksti.TekstiKappaleDto;
import fi.vm.sade.eperusteet.ylops.dto.teksti.TekstiKappaleViiteDto;
import fi.vm.sade.eperusteet.ylops.repository.cache.PerusteCacheRepository;
import fi.vm.sade.eperusteet.ylops.repository.ohje.OhjeRepository;
import fi.vm.sade.eperusteet.ylops.repository.ops.OpetussuunnitelmaRepository;
import fi.vm.sade.eperusteet.ylops.repository.ops.VuosiluokkakokonaisuusviiteRepository;
import fi.vm.sade.eperusteet.ylops.repository.teksti.TekstiKappaleRepository;
import fi.vm.sade.eperusteet.ylops.repository.teksti.TekstikappaleviiteRepository;
import fi.vm.sade.eperusteet.ylops.service.exception.BusinessRuleViolationException;
import fi.vm.sade.eperusteet.ylops.service.external.EperusteetService;
import fi.vm.sade.eperusteet.ylops.service.external.KoodistoService;
import fi.vm.sade.eperusteet.ylops.service.external.OrganisaatioService;
import fi.vm.sade.eperusteet.ylops.service.mapping.DtoMapper;
import fi.vm.sade.eperusteet.ylops.service.ops.OpetussuunnitelmaService;
import fi.vm.sade.eperusteet.ylops.service.ops.OppiaineService;
import fi.vm.sade.eperusteet.ylops.service.ops.TekstiKappaleViiteService;
import fi.vm.sade.eperusteet.ylops.service.ops.VuosiluokkakokonaisuusService;
import fi.vm.sade.eperusteet.ylops.service.ops.lukio.LukioOpetussuunnitelmaService;
import fi.vm.sade.eperusteet.ylops.service.security.PermissionEvaluator.RolePermission;
import fi.vm.sade.eperusteet.ylops.service.teksti.KommenttiService;
import fi.vm.sade.eperusteet.ylops.service.util.CollectionUtil;
import fi.vm.sade.eperusteet.ylops.service.util.Jarjestetty;
import fi.vm.sade.eperusteet.ylops.service.util.LambdaUtil.ConstructedCopier;
import fi.vm.sade.eperusteet.ylops.service.util.LambdaUtil.Copier;
import fi.vm.sade.eperusteet.ylops.service.util.SecurityUtil;
import fi.vm.sade.eperusteet.ylops.service.util.Validointi;
import org.apache.commons.lang.LocaleUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.method.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static fi.vm.sade.eperusteet.ylops.service.util.Nulls.assertExists;
import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.*;
import org.springframework.cache.annotation.Cacheable;

/**
 *
 * @author mikkom
 */
@Service
@Transactional
public class OpetussuunnitelmaServiceImpl implements OpetussuunnitelmaService {

    static private final Logger logger = LoggerFactory.getLogger(OpetussuunnitelmaServiceImpl.class);

    @Autowired
    private DtoMapper mapper;

    @Autowired
    private OpetussuunnitelmaRepository repository;

    @Autowired
    private TekstikappaleviiteRepository viiteRepository;

    @Autowired
    private TekstiKappaleRepository tekstiKappaleRepository;

    @Autowired
    private TekstiKappaleViiteService tekstiKappaleViiteService;

    @Autowired
    private OppiaineService oppiaineService;

    @Autowired
    private KoodistoService koodistoService;

    @Autowired
    private OrganisaatioService organisaatioService;

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

    @Override
    @Transactional(readOnly = true)
    public List<OpetussuunnitelmaJulkinenDto> getAllJulkiset(Tyyppi tyyppi) {
        final List<Opetussuunnitelma> opetussuunnitelmat = repository.findAllByTyyppiAndTilaIsJulkaistu(tyyppi);

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
        Opetussuunnitelma ops = repository.findOne(id);
        assertExists(ops, "Pyydettyä opetussuunnitelmaa ei ole olemassa");
        OpetussuunnitelmaJulkinenDto dto = mapper.map(ops, OpetussuunnitelmaJulkinenDto.class);
        //fetchKuntaNimet(dto);
        //fetchOrganisaatioNimet(dto);
        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<OpetussuunnitelmaInfoDto> getAll(Tyyppi tyyppi) {
        Set<String> organisaatiot = SecurityUtil.getOrganizations(EnumSet.allOf(RolePermission.class));
        final List<Opetussuunnitelma> opetussuunnitelmat;
        if (tyyppi == Tyyppi.POHJA) {
            opetussuunnitelmat = repository.findPohja(organisaatiot);
        } else {
            opetussuunnitelmat = repository.findAllByTyyppi(tyyppi, organisaatiot);
        }
        final List<OpetussuunnitelmaInfoDto> dtot = mapper.mapAsList(opetussuunnitelmat,
                OpetussuunnitelmaInfoDto.class);
        dtot.forEach(dto -> {
            fetchKuntaNimet(dto);
            fetchOrganisaatioNimet(dto);
        });
        return dtot;
    }

    @Override
    @Transactional(readOnly = true)
    public List<OpetussuunnitelmaStatistiikkaDto> getStatistiikka() {
        List<Opetussuunnitelma> opsit = repository.findAllByTyyppi(Tyyppi.OPS);
        return mapper.mapAsList(opsit,OpetussuunnitelmaStatistiikkaDto.class);
    }

    @Override
    @Transactional(readOnly = true)
    public PerusteDto getPeruste(Long opsId) {
        Opetussuunnitelma ops = repository.findOne(opsId);
        assertExists(ops, "Pyydettyä opetussuunnitelmaa ei ole olemassa");
        return eperusteetService.getPeruste(ops.getPerusteenDiaarinumero());
    }

    @Override
    @Transactional(readOnly = true)
    public OpetussuunnitelmaKevytDto getOpetussuunnitelma(Long id) {
        Opetussuunnitelma ops = repository.findOne(id);
        assertExists(ops, "Pyydettyä opetussuunnitelmaa ei ole olemassa");
        OpetussuunnitelmaKevytDto dto = mapper.map(ops, OpetussuunnitelmaKevytDto.class);
        fetchKuntaNimet(dto);
        fetchOrganisaatioNimet(dto);
        return dto;
    }

    private void fetchLapsiOpetussuunnitelmat(Long id, Set<Opetussuunnitelma> opsit) {
        opsit.addAll(repository.findAllByPohjaId(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<OpetussuunnitelmaInfoDto> getLapsiOpetussuunnitelmat(Long id) {
        Opetussuunnitelma ops = repository.findOne(id);
        assertExists(ops, "Pyydettyä opetussuunnitelmaa ei ole olemassa");
        Set<Opetussuunnitelma> result = new HashSet<>();
        fetchLapsiOpetussuunnitelmat(id, result);
        return mapper.mapAsList(result, OpetussuunnitelmaInfoDto.class);
    }

    @Override
    @Transactional(readOnly = true)
    public OpetussuunnitelmaLaajaDto getOpetussuunnitelmaEnempi(Long id) {
        Opetussuunnitelma ops = repository.findOne(id);
        assertExists(ops, "Pyydettyä opetussuunnitelmaa ei ole olemassa");
        OpetussuunnitelmaLaajaDto dto = mapper.map(ops, OpetussuunnitelmaLaajaDto.class);
        fetchKuntaNimet(dto);
        fetchOrganisaatioNimet(dto);
        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public OpetussuunnitelmaDto getOpetussuunnitelmaKaikki(Long id) {
        Opetussuunnitelma ops = repository.findOne(id);
        assertExists(ops, "Pyydettyä opetussuunnitelmaa ei ole olemassa");
        OpetussuunnitelmaDto dto = mapper.map(ops, OpetussuunnitelmaDto.class);
        fetchKuntaNimet(dto);
        fetchOrganisaatioNimet(dto);
        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public Set<PerusteLaajaalainenosaaminenDto> getLaajaalaisetosaamiset(Long id) {
        Opetussuunnitelma ops = repository.findOne(id);
        assertExists(ops, "Pyydettyä opetussuunnitelmaa ei ole olemassa");
        return eperusteetService.getPeruste(ops.getPerusteenDiaarinumero()).getPerusopetus()
                .getLaajaalaisetosaamiset();
    }

    @Override
    public void updateOppiainejarjestys(Long opsId, List<JarjestysDto> oppiainejarjestys) {
        Opetussuunnitelma ops = repository.findOne(opsId);
        assertExists(ops, "Pyydettyä opetussuunnitelmaa ei ole olemassa");

        Map<Long, Oppiaine> oppiaineet = new HashMap<Long, Oppiaine>();
        ops.getOppiaineet().forEach(opsOppiaine -> {
            oppiaineet.put(opsOppiaine.getOppiaine().getId(), opsOppiaine.getOppiaine());
            if (opsOppiaine.getOppiaine().getOppimaarat() != null) {
                opsOppiaine.getOppiaine().getOppimaarat().forEach(oppimaara -> oppiaineet.put(oppimaara.getId(), oppimaara));
            }
        });

        for (JarjestysDto node : oppiainejarjestys) {
            Oppiaine oppiaine = oppiaineet.get(node.getOppiaineId());
            assertExists(oppiaine, "Pyydettyä oppiainetta ei ole");
            oppiaine.getVuosiluokkakokonaisuudet().forEach(oppiaineenvuosiluokkakokonaisuus -> {
                oppiaineenvuosiluokkakokonaisuus.setJnro(node.getJnro());
            });
        }
    }

    private void fetchKuntaNimet(OpetussuunnitelmaBaseDto opetussuunnitelmaDto) {
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

    private void fetchOrganisaatioNimet(OpetussuunnitelmaBaseDto opetussuunnitelmaDto) {
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

    @Override
    public OpetussuunnitelmaDto addOpetussuunnitelma(OpetussuunnitelmaLuontiDto opetussuunnitelmaDto) {
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
            pohja = repository.findOneByTyyppiAndTilaAndKoulutustyyppi(Tyyppi.POHJA,
                    Tila.VALMIS, opetussuunnitelmaDto.getKoulutustyyppi());
        }

        if (pohja != null) {
            ops.setTekstit(new TekstiKappaleViite(Omistussuhde.OMA));
            ops.getTekstit().setLapset(new ArrayList<>());
            luoOpsPohjasta(pohja, ops);
            ops.setTila(Tila.LUONNOS);
            ops = repository.save(ops);

            if(isPohjastaTehtyPohja(pohja) && pohja.getKoulutustyyppi().isLukio() ){
                lisaaTeemaopinnotJosPohjassa(ops, pohja);
            }
        } else {
            throw new BusinessRuleViolationException("Valmista opetussuunnitelman pohjaa ei löytynyt");
        }

        return mapper.map(ops, OpetussuunnitelmaDto.class);
    }

    private void lisaaTeemaopinnotJosPohjassa(Opetussuunnitelma ops, Opetussuunnitelma pohja) {
        final Long opsId = ops.getId();
        pohja.getOppiaineet().stream()
                .filter(opsOppiaine1 -> opsOppiaine1.getOppiaine().getKoodiUri().compareTo("oppiaineetyleissivistava2_to")==0)
                .findFirst()
                .ifPresent(opsOppiaine -> {
                    LukioAbstraktiOppiaineTuontiDto dto = new LukioAbstraktiOppiaineTuontiDto();
                    dto.setNimi(mapper.map(opsOppiaine.getOppiaine().getNimi(), LokalisoituTekstiDto.class));
                    dto.setTunniste(opsOppiaine.getOppiaine().getTunniste());
                    lukioOpetussuunnitelmaService.addAbstraktiOppiaine(opsId, dto);
                });
    }

    private void luoOpsPohjasta(Opetussuunnitelma pohja, Opetussuunnitelma ops) {
        ops.setPohja(pohja);
        ops.setPerusteenDiaarinumero(pohja.getPerusteenDiaarinumero());
        ops.setCachedPeruste(ops.getCachedPeruste());
        if (ops.getCachedPeruste() == null) {
            PerusteDto peruste = eperusteetService.getPeruste(ops.getPerusteenDiaarinumero());
            ops.setCachedPeruste(perusteCacheRepository.findNewestEntryForPeruste(peruste.getId()));
        }
        boolean teeKopio = pohja.getTyyppi() == Tyyppi.POHJA;
        kasitteleTekstit(pohja.getTekstit(), ops.getTekstit(), teeKopio);

        boolean onPohjastaTehtyPohja = isPohjastaTehtyPohja(pohja);

        Copier<Oppiaine> oppiaineCopier = teeKopio ? Oppiaine.basicCopier() : Copier.nothing();
        Map<Long,Oppiaine> newOppiaineByOld = new HashMap<>();
        Copier<Oppiaine> kurssiCopier = null;
        if (pohja.getKoulutustyyppi().isLukio()) {
            luoLukiokoulutusPohjasta(pohja, ops);
            kurssiCopier = getLukiokurssitOppiaineCopier(pohja, ops, teeKopio);
            oppiaineCopier = oppiaineCopier.and(kurssiCopier)
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
                    from.getOppimaarat().stream().forEach(om ->
                        finalKurssiCopier.copy(om, om)
                    );
                }
            });
        }
        ConstructedCopier<OpsOppiaine> opsOppiaineCopier = OpsOppiaine.copier(
                oppiaineCopier.construct(existing -> teeKopio ? new Oppiaine(existing.getTunniste()) : existing), teeKopio);
        Stream<OpsOppiaine> oppiaineetToCopy = pohja.getKoulutustyyppi().isLukio()
                    && pohja.getTyyppi() == Tyyppi.POHJA // ei kopioida pohjasta abstakteja ylätason oppiaineita, mutta OPS:sta kyllä
                ? pohja.getOppiaineet().stream().filter(opsOa -> !opsOa.getOppiaine().isAbstraktiBool())
                : pohja.getOppiaineet().stream();
        ops.setOppiaineet(oppiaineetToCopy.map(opsOppiaineCopier::copy).collect(toSet()));
        ops.getOppiaineJarjestykset().addAll(pohja.getOppiaineJarjestykset().stream().map(old
                -> !teeKopio ? new LukioOppiaineJarjestys(ops, old.getOppiaine(), old.getJarjestys())
                    : (newOppiaineByOld.get(old.getId().getOppiaineId()) != null ?
                        new LukioOppiaineJarjestys(ops, newOppiaineByOld.get(old.getId().getOppiaineId()), old.getJarjestys())
                        : null)).filter(o -> o != null).collect(toSet()));
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

    @SuppressWarnings({"ServiceMethodEntity", "TransactionalAnnotations"})
    public static Copier<Oppiaine> getLukiokurssitOppiaineCopier(Opetussuunnitelma pohja, Opetussuunnitelma ops, boolean teeKopio) {
        Map<UUID, Lukiokurssi> existingKurssit = teeKopio ? new HashMap<>()
                : pohja.getLukiokurssit().stream().map(OppiaineLukiokurssi::getKurssi)
                .filter(k-> k.getTunniste() != null)
                .collect(toMap(Kurssi::getTunniste, k -> k, (a, b) -> a));
        Map<Long, List<OppiaineLukiokurssi>> lukiokurssitByPohjaOppiaineId
                = pohja.getLukiokurssit().stream().collect(groupingBy(oak -> oak.getOppiaine().getId()));
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
                                return new OppiaineLukiokurssi(
                                        ops, to, kurssi, oaKurssi.getJarjestys(), teeKopio
                                );
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
                    tkv.setVanhempi(parent);
                    tkv.setPakollinen(vanhaTkv.isPakollinen());
                    tkv.setTekstiKappale(teeKopio
                            ? tekstiKappaleRepository.save(vanhaTkv.getTekstiKappale().copy())
                            : vanhaTkv.getTekstiKappale());
                    parent.getLapset().add(tkv);
                    kasitteleTekstit(vanhaTkv, tkv, teeKopio);
                });
        }
    }

    private Opetussuunnitelma addPohjaLisaJaEsiopetus(Opetussuunnitelma ops, PerusteDto peruste) {
        ops.setKoulutustyyppi(peruste.getKoulutustyyppi());
        return ops;
    }

    private Opetussuunnitelma addPohjaPerusopetus(Opetussuunnitelma ops, PerusteDto peruste) {
        Long opsId = ops.getId();

        PerusopetuksenPerusteenSisaltoDto sisalto = peruste.getPerusopetus();

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
            maxJnro = Math.max(maxJnro+1, ofNullable(aihekokonaisuus.getJnro()).orElse(0L));
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
        Opetussuunnitelma pohja = repository.findOne(pohjaId);
        if (pohja.getPohja() != null) {
            throw new BusinessRuleViolationException("OPS ei ollut pohja");
        }

        pohja.setOppiaineet(null);
        pohja.setVuosiluokkakokonaisuudet(null);
        pohja.getLukiokurssit().clear();;
        pohja.getOppiaineJarjestykset().clear();

        PerusteDto peruste = eperusteetService.getPerusteUpdateCache(pohja.getPerusteenDiaarinumero());
        pohja.setCachedPeruste(perusteCacheRepository.findNewestEntryForPeruste(peruste.getId()));
        lisaaPerusteenSisalto(pohja, peruste);
    }

    private Opetussuunnitelma lisaaPerusteenSisalto(Opetussuunnitelma ops, PerusteDto peruste) {
        if (peruste.getKoulutustyyppi() == null || KoulutusTyyppi.PERUSOPETUS == peruste.getKoulutustyyppi()) {
            return addPohjaPerusopetus(ops, peruste);
        } else if (KoulutusTyyppi.LISAOPETUS == peruste.getKoulutustyyppi()
                || KoulutusTyyppi.ESIOPETUS == peruste.getKoulutustyyppi()
                || KoulutusTyyppi.VARHAISKASVATUS == peruste.getKoulutustyyppi()) {
            return addPohjaLisaJaEsiopetus(ops, peruste);
        } else if (peruste.getKoulutustyyppi().isLukio()) {
            return addPohjaLukiokoulutus(ops, peruste);
        } else {
            throw new BusinessRuleViolationException("Ei toimintatapaa perusteen koulutustyypille");
        }
    }

    @Override
    public OpetussuunnitelmaDto addPohja(OpetussuunnitelmaLuontiDto opetussuunnitelmaDto) {
        Opetussuunnitelma ops = mapper.map(opetussuunnitelmaDto, Opetussuunnitelma.class);
        // Jokainen pohja sisältää OPH:n organisaationaan
        ops.getOrganisaatiot().add(SecurityUtil.OPH_OID);

        Set<String> userOids = SecurityUtil.getOrganizations(EnumSet.of(RolePermission.CRUD));
        if (CollectionUtil.intersect(userOids, ops.getOrganisaatiot()).isEmpty()) {
            throw new BusinessRuleViolationException("Käyttäjällä ei ole luontioikeutta " +
                    "opetussuunnitelman pohjan organisaatioissa");
        }

        final String diaarinumero = ops.getPerusteenDiaarinumero();
        if (StringUtils.isBlank(diaarinumero)) {
            throw new BusinessRuleViolationException("Perusteen diaarinumeroa ei ole määritelty");
        } else if (eperusteetService.findPerusteet().stream()
                .noneMatch(p -> diaarinumero.equals(p.getDiaarinumero()))) {
            throw new BusinessRuleViolationException("Diaarinumerolla " + diaarinumero +
                 " ei löydy voimassaolevaa perustetta");
        }

        if (ops.getPohja() != null) {
            throw new BusinessRuleViolationException("Opetussuunnitelman pohjalla ei voi olla pohjaa");
        }

        ops.setTila(Tila.LUONNOS);
        lisaaTekstipuunJuuri(ops);

        ops = repository.save(ops);
        lisaaTekstipuunLapset(ops);

        PerusteDto peruste = eperusteetService.getPeruste(ops.getPerusteenDiaarinumero());
        ops.setCachedPeruste(perusteCacheRepository.findNewestEntryForPeruste(peruste.getId()));
        ops.setKoulutustyyppi(peruste.getKoulutustyyppi() != null ? peruste.getKoulutustyyppi()
                : KoulutusTyyppi.PERUSOPETUS);
        return mapper.map(lisaaPerusteenSisalto(ops, peruste), OpetussuunnitelmaDto.class);
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
    @Transactional(readOnly = false)
    public void updateLapsiOpetussuunnitelmat(Long opsId) {
        Opetussuunnitelma ops = repository.findOne(opsId);
        assertExists(ops, "Päivitettävää tietoa ei ole olemassa");
        Set<Opetussuunnitelma> aliopsit = repository.findAllByPohjaId(opsId);

        for (Opetussuunnitelma aliops : aliopsit) {
            Map<UUID, TekstiKappaleViite> aliopsTekstit = new HashMap<>();
            flattenTekstikappaleviitteet(aliopsTekstit, aliops.getTekstit());
            aliops.getTekstit().getLapset().clear();
            aliopsTekstit.values().stream()
                    .forEach((teksti) -> {
                        teksti.setVanhempi(aliops.getTekstit());
                        teksti.getLapset().clear();
                    });
//            aliops.getTekstit().getLapset().addAll(aliopsTekstit.values());
//            for (TekstiKappaleViite lapsi : ops.getTekstit().getLapset()) {
//                TekstiKappaleViite uusiSolmu = viiteRepository.save(lapsi.kopioiHierarkia(aliopsTekstit));
//                uusiSolmu.setVanhempi(aliops.getTekstit());
//                aliops.getTekstit().getLapset().add(uusiSolmu);
//            }
//            aliops.getTekstit().getLapset().addAll(aliopsTekstit.values());
        }
    }

    @Override
    public OpetussuunnitelmaDto updateOpetussuunnitelma(OpetussuunnitelmaDto opetussuunnitelmaDto) {
        Opetussuunnitelma ops = repository.findOne(opetussuunnitelmaDto.getId());
        assertExists(ops, "Päivitettävää tietoa ei ole olemassa");

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

            if (!opetussuunnitelmaDto.getJulkaisukielet().stream()
                    .collect(Collectors.toSet())
                    .equals(ops.getJulkaisukielet().stream()
                            .collect(Collectors.toSet()))) {
                throw new BusinessRuleViolationException("Opetussuunnitelman julkaisukieliä ei voi vaihtaa kuin luonnoksessa");
            }
        }

        if (ops.getPohja() != null && !Objects.equals(opetussuunnitelmaDto.getPohja().getId(), ops.getPohja().getId())) {
            throw new BusinessRuleViolationException("Opetussuunnitelman pohjaa ei voi vaihtaa");
        }

        if (!Objects.equals(opetussuunnitelmaDto.getPerusteenDiaarinumero(), ops.getPerusteenDiaarinumero())) {
            throw new BusinessRuleViolationException("Perusteen diaarinumeroa ei voi vaihtaa");
        }

        if (opetussuunnitelmaDto.getOrganisaatiot().isEmpty()) {
            throw new BusinessRuleViolationException("Organisaatiolista ei voi olla tyhjä");
        }

        // Tilan muuttamiseen on oma erillinen endpointtinsa
        opetussuunnitelmaDto.setTila(ops.getTila());

        mapper.map(opetussuunnitelmaDto, ops);
        ops = repository.save(ops);

        return mapper.map(ops, OpetussuunnitelmaDto.class);
    }

    private void validoiOpetussuunnitelma(Opetussuunnitelma ops) {
        Set<Kieli> julkaisukielet = ops.getJulkaisukielet();
        Validointi validointi = new Validointi();

        if (ops.getPerusteenDiaarinumero().isEmpty()) {
            validointi.lisaaVirhe(Validointi.luoVirhe("opsilla-ei-perusteen-diaarinumeroa"));
        }

        if (ops.getTekstit() != null && ops.getTekstit().getLapset() != null) {
            for (TekstiKappaleViite teksti : ops.getTekstit().getLapset()) {
                TekstiKappaleViite.validoi(validointi, teksti, julkaisukielet);
            }
        }

        ops.getVuosiluokkakokonaisuudet().stream()
            .filter(OpsVuosiluokkakokonaisuus::isOma)
            .map(OpsVuosiluokkakokonaisuus::getVuosiluokkakokonaisuus)
            .forEach(vlk -> Vuosiluokkakokonaisuus.validoi(validointi, vlk, julkaisukielet));

        //TODO:should we use same version of Peruste for with the Opetuusuunnitelma was based on if available?
        PerusteDto peruste = eperusteetService.getPeruste(ops.getPerusteenDiaarinumero());

        ops.getOppiaineet().stream()
            .filter(OpsOppiaine::isOma)
            .map(OpsOppiaine::getOppiaine)
            .forEach(oa -> {
                peruste.getPerusopetus().getOppiaine(oa.getTunniste()).ifPresent(poppiaine -> {
                    Oppiaine.validoi(validointi, oa, julkaisukielet);
                    Set<UUID> PerusteenTavoitteet = new HashSet<>();

                    poppiaine.getVuosiluokkakokonaisuudet().stream()
                    .forEach(vlk -> vlk.getTavoitteet().stream()
                        .forEach(tavoite -> PerusteenTavoitteet.add(tavoite.getTunniste())));

                    Set<UUID> OpsinTavoitteet = oa.getVuosiluokkakokonaisuudet().stream()
                        .flatMap(vlk -> vlk.getVuosiluokat().stream())
                            .map(ovlk -> ovlk.getTavoitteet())
                            .flatMap(tavoitteet -> tavoitteet.stream())
                                .map(tavoite -> tavoite.getTunniste())
                                .collect(Collectors.toSet());

                    if (!OpsinTavoitteet.equals(PerusteenTavoitteet)) {
    //                    validointi.lisaaVirhe(Validointi.luoVirhe("opsin-oppiainetta-ei-ole-vuosiluokkaistettu", poppiaine.getNimi()));
                    }
        		});
            });

        validointi.tuomitse();
    }

    private void validoiOhjeistus(TekstiKappaleViite tkv, Set<Kieli> kielet) {
        Validointi validointi = new Validointi();
        for (TekstiKappaleViite lapsi : tkv.getLapset()) {
            Ohje ohje = ohjeRepository.findFirstByKohde(lapsi.getTekstiKappale().getTunniste());

            if (ohje != null && (ohje.getTeksti() == null || !ohje.getTeksti().hasKielet(kielet))) {
                validointi.lisaaVirhe(Validointi.luoVirhe("ops-pohja-ohjeistus-puuttuu", tkv.getTekstiKappale().getNimi(), tkv.getTekstiKappale().getNimi()));
            }
            else {
                validointi.lisaaVirhe(Validointi.luoVirhe("ops-pohja-ohjeistus-puuttuu"));
            }
            validoiOhjeistus(lapsi, kielet);
        }
        validointi.tuomitse();
    }

    private void validoiPohja(Opetussuunnitelma ops) {
//        TekstiKappaleViite sisalto = ops.getTekstit();
//        for (TekstiKappaleViite lapsi : sisalto.getLapset()) {
//            validoiOhjeistus(lapsi, ops.getJulkaisukielet());
//        }
    }

    @Override
    public OpetussuunnitelmaDto updateTila(@P("id") Long id, Tila tila) {
        Opetussuunnitelma ops = repository.findOne(id);
        assertExists(ops, "Opetussuunnitelmaa ei ole olemassa");

        if (ops.getTyyppi() == Tyyppi.POHJA && tila == Tila.JULKAISTU) {
            tila = Tila.VALMIS;
        }

        if (ops.getTyyppi() == Tyyppi.OPS && ops.getTila() == Tila.JULKAISTU && tila == Tila.VALMIS) {
            ops.setTila(tila);
            ops = repository.save(ops);
        }

        if (tila != ops.getTila() && ops.getTila().mahdollisetSiirtymat(ops.getTyyppi()
                == Tyyppi.POHJA).contains(tila)) {
            if (ops.getTyyppi() == Tyyppi.OPS && (tila == Tila.JULKAISTU)) {
                validoiOpetussuunnitelma(ops);
            } else if (ops.getTyyppi() == Tyyppi.POHJA && tila == Tila.VALMIS) {
                validoiPohja(ops);
            }

            if (tila == Tila.VALMIS && ops.getTyyppi() == Tyyppi.POHJA) {
                // Arkistoidaan vanhat valmiit pohjat
                List<Opetussuunnitelma> pohjat = repository.findAllByTyyppiAndTilaAndKoulutustyyppi(
                        Tyyppi.POHJA, Tila.VALMIS, ops.getKoulutustyyppi());
                for (Opetussuunnitelma pohja : pohjat) {
                    pohja.setTila(Tila.POISTETTU);
                }
            }

            if (tila == Tila.VALMIS && ops.getTila() == Tila.LUONNOS && ops.getTyyppi() != Tyyppi.POHJA &&
                    ops.getKoulutustyyppi().isLukio()) {
                validoiLukioPohja(ops);
            }
            ops.setTila(tila);
            ops = repository.save(ops);
        }
        return mapper.map(ops, OpetussuunnitelmaDto.class);
    }

    private void validoiLukioPohja(Opetussuunnitelma ops) {
        Validointi validointi = new Validointi();

        if( ops.getOppiaineet().isEmpty() ){
            validointi.lisaaVirhe(validointi.luoVirhe("lukio-ei-oppiaineita", ops.getNimi()));
        }

        if (ops.getAihekokonaisuudet() == null || ops.getAihekokonaisuudet().getAihekokonaisuudet().isEmpty()) {
            validointi.lisaaVirhe(validointi.luoVirhe("lukio-ei-aihekokonaisuuksia", ops.getNimi()));
        }

        ops.getOppiaineet().forEach(opsOppiaine -> {
            if (!opsOppiaine.getOppiaine().isKoosteinen() && !oppiaineHasKurssi( opsOppiaine.getOppiaine(), ops.getLukiokurssit())) {
                validointi.lisaaVirhe(validointi.luoVirhe("lukio-oppiaineessa-ei-kursseja", opsOppiaine.getOppiaine().getNimi()));
            }
            if( opsOppiaine.getOppiaine().isKoosteinen() && opsOppiaine.getOppiaine().getOppimaarat().isEmpty()){
                validointi.lisaaVirhe(validointi.luoVirhe("lukio-oppiaineessa-ei-oppimaaria", opsOppiaine.getOppiaine().getNimi()));
            }
        });

        ops.getLukiokurssit().forEach(oppiaineLukiokurssi -> {
            if (oppiaineLukiokurssi.getKurssi().getTyyppi().isPaikallinen()) {
                oppiaineLukiokurssi.getKurssi().validoiTavoitteetJaKeskeinenSisalto(validointi, ops.getJulkaisukielet());
            }
        });

        validointi.tuomitse();
    }

    private boolean oppiaineHasKurssi(Oppiaine oppiaine, Set<OppiaineLukiokurssi> lukiokurssit) {
        for (OppiaineLukiokurssi oppiaineLukiokurssi : lukiokurssit) {
            if( oppiaineLukiokurssi.getOppiaine().getTunniste().compareTo( oppiaine.getTunniste() ) == 0){
                return true;
            }
        }
        return false;
    }

    @Override
    public OpetussuunnitelmaDto restore(@P("id") Long id) {
        Opetussuunnitelma ops = repository.findOne(id);
        assertExists(ops, "Opetussuunnitelmaa ei ole olemassa");

        ops.setTila(Tila.LUONNOS);
        ops = repository.save(ops);
        return mapper.map(ops, OpetussuunnitelmaDto.class);
    }

    @Override
    public void removeOpetussuunnitelma(Long id) {
        Opetussuunnitelma ops = repository.findOne(id);
        if (ops != null) {
            kommenttiService.getAllByOpetussuunnitelma(id)
                .forEach(k -> kommenttiService.deleteReally(k.getId()));
        }
        repository.delete(ops);
    }

    @Override
    @Transactional(readOnly = true)
    public <T> T getTekstit(Long opsId, Class<T> t) {
        Opetussuunnitelma ops = repository.findOne(opsId);
        assertExists(ops, "Opetussuunnitelmaa ei ole olemassa");
        return mapper.map(ops.getTekstit(), t);
    }

    @Override
    public TekstiKappaleViiteDto.Matala addTekstiKappale(Long opsId, TekstiKappaleViiteDto.Matala viite) {
        Opetussuunnitelma ops = repository.findOne(opsId);
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

}
