package fi.vm.sade.eperusteet.ylops.service.ops.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
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
import fi.vm.sade.eperusteet.ylops.domain.oppiaine.Oppiaine;
import fi.vm.sade.eperusteet.ylops.domain.oppiaine.OppiaineTyyppi;
import fi.vm.sade.eperusteet.ylops.domain.oppiaine.Oppiaineenvuosiluokkakokonaisuus;
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
import fi.vm.sade.eperusteet.ylops.dto.koodisto.KoodistoDto;
import fi.vm.sade.eperusteet.ylops.dto.koodisto.KoodistoKoodiDto;
import fi.vm.sade.eperusteet.ylops.dto.koodisto.KoodistoMetadataDto;
import fi.vm.sade.eperusteet.ylops.dto.koodisto.OrganisaatioDto;
import fi.vm.sade.eperusteet.ylops.dto.koodisto.OrganisaatioLaajaDto;
import fi.vm.sade.eperusteet.ylops.dto.koodisto.OrganisaatioTyyppi;
import fi.vm.sade.eperusteet.ylops.dto.lops2019.Lops2019OpintojaksoDto;
import fi.vm.sade.eperusteet.ylops.dto.lops2019.Lops2019PaikallinenOppiaineDto;
import fi.vm.sade.eperusteet.ylops.dto.lukio.LukioAbstraktiOppiaineTuontiDto;
import fi.vm.sade.eperusteet.ylops.dto.navigation.NavigationNodeDto;
import fi.vm.sade.eperusteet.ylops.dto.navigation.NavigationType;
import fi.vm.sade.eperusteet.ylops.dto.ops.MuokkaustietoLisatieto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpetussuunnitelmaBaseDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpetussuunnitelmaDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpetussuunnitelmaInfoDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpetussuunnitelmaJulkaistuQuery;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpetussuunnitelmaJulkinenDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpetussuunnitelmaKevytDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpetussuunnitelmaLuontiDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpetussuunnitelmaNimiDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpetussuunnitelmaQuery;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpetussuunnitelmaStatistiikkaDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpetussuunnitelmaTilastoDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpetussuunnitelmaWithLatestTilaUpdateTime;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpsVuosiluokkakokonaisuusDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpsVuosiluokkakokonaisuusKevytDto;
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
import fi.vm.sade.eperusteet.ylops.dto.teksti.TekstiKappaleViitePerusteTekstillaDto;
import fi.vm.sade.eperusteet.ylops.repository.cache.PerusteCacheRepository;
import fi.vm.sade.eperusteet.ylops.repository.dokumentti.DokumenttiRepository;
import fi.vm.sade.eperusteet.ylops.repository.lops2019.Lops2019OpintojaksoRepository;
import fi.vm.sade.eperusteet.ylops.repository.ops.JulkaisuRepository;
import fi.vm.sade.eperusteet.ylops.repository.ops.OpetussuunnitelmaRepository;
import fi.vm.sade.eperusteet.ylops.repository.ops.OppiaineRepository;
import fi.vm.sade.eperusteet.ylops.repository.ops.VuosiluokkakokonaisuusRepository;
import fi.vm.sade.eperusteet.ylops.repository.ops.VuosiluokkakokonaisuusviiteRepository;
import fi.vm.sade.eperusteet.ylops.repository.teksti.TekstiKappaleRepository;
import fi.vm.sade.eperusteet.ylops.repository.teksti.TekstikappaleviiteRepository;
import fi.vm.sade.eperusteet.ylops.resource.config.InitJacksonConverter;
import fi.vm.sade.eperusteet.ylops.service.exception.BusinessRuleViolationException;
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
import fi.vm.sade.eperusteet.ylops.service.ops.OpetussuunnitelmaAsyncTekstitPohjastaService;
import fi.vm.sade.eperusteet.ylops.service.ops.OpetussuunnitelmaService;
import fi.vm.sade.eperusteet.ylops.service.ops.OpetussuunnitelmanMuokkaustietoService;
import fi.vm.sade.eperusteet.ylops.service.ops.OppiaineService;
import fi.vm.sade.eperusteet.ylops.service.ops.OpsDispatcher;
import fi.vm.sade.eperusteet.ylops.service.ops.OpsExport;
import fi.vm.sade.eperusteet.ylops.service.ops.OpsPohjaSynkronointi;
import fi.vm.sade.eperusteet.ylops.service.ops.OpsPohjanVaihto;
import fi.vm.sade.eperusteet.ylops.service.ops.TekstiKappaleViiteService;
import fi.vm.sade.eperusteet.ylops.service.ops.ValidointiService;
import fi.vm.sade.eperusteet.ylops.service.ops.VuosiluokkakokonaisuusService;
import fi.vm.sade.eperusteet.ylops.service.ops.lukio.LukioOpetussuunnitelmaService;
import fi.vm.sade.eperusteet.ylops.service.security.PermissionEvaluator.RolePermission;
import fi.vm.sade.eperusteet.ylops.service.util.CollectionUtil;
import fi.vm.sade.eperusteet.ylops.service.util.Jarjestetty;
import fi.vm.sade.eperusteet.ylops.service.util.JulkaisuService;
import fi.vm.sade.eperusteet.ylops.service.util.LambdaUtil.ConstructedCopier;
import fi.vm.sade.eperusteet.ylops.service.util.LambdaUtil.Copier;
import fi.vm.sade.eperusteet.ylops.service.util.NavigationUtil;
import fi.vm.sade.eperusteet.ylops.service.util.SecurityUtil;
import fi.vm.sade.eperusteet.ylops.service.util.Validointi;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.method.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

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

import static fi.vm.sade.eperusteet.ylops.service.util.Nulls.assertExists;
import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

@Service
@Transactional
@Slf4j
public class OpetussuunnitelmaServiceImpl implements OpetussuunnitelmaService {
    @Autowired
    private DokumenttiRepository dokumenttiRepository;

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
    private TekstiKappaleViiteService tekstiKappaleViiteService;

    @Autowired
    private OppiaineService oppiaineService;

    @Autowired
    private KoodistoService koodistoService;

    @Autowired
    private OrganisaatioService organisaatioService;

    @Autowired
    private JulkaisuRepository julkaisuRepository;

    @Autowired
    private VuosiluokkakokonaisuusService vuosiluokkakokonaisuudet;

    @Autowired
    private EperusteetService eperusteetService;

    @Autowired
    private VuosiluokkakokonaisuusviiteRepository vuosiluokkakokonaisuusviiteRepository;

    @Autowired
    private PerusteCacheRepository perusteCacheRepository;

    @Autowired
    private LukioOpetussuunnitelmaService lukioOpetussuunnitelmaService;

    @Autowired
    private OpsDispatcher dispatcher;

    @Autowired
    private Lops2019OpintojaksoService lops2019OpintojaksoService;

    @Autowired
    private Lops2019Service lops2019Service;

    @Autowired
    private Lops2019OpintojaksoRepository opintojaksoRepository;

    @Autowired
    private Lops2019OppiaineService lops2019OppiaineService;

    @Autowired
    private OpetussuunnitelmanMuokkaustietoService muokkaustietoService;

    @Autowired
    private KayttajanTietoService kayttajanTietoService;

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private CacheManager cacheManager;

    @Lazy
    @Autowired
    private JulkaisuService julkaisuService;

    @Lazy
    @Autowired
    private OpetussuunnitelmaService self;

    @Autowired
    private VuosiluokkakokonaisuusRepository vuosiluokkakokonaisuusRepository;

    @Autowired
    private ValidointiService validointiService;

    @Autowired
    private OppiaineRepository oppiaineRepository;

    @Autowired
    private OpetussuunnitelmaAsyncTekstitPohjastaService opetussuunnitelmaAsyncTekstitPohjastaService;

    private final ObjectMapper objectMapper = InitJacksonConverter.createMapper();

    private List<Opetussuunnitelma> findJulkaistutByQuery(OpetussuunnitelmaQuery pquery) {
        CriteriaQuery<Opetussuunnitelma> query = getJulkaistutQuery(pquery);
        return em.createQuery(query).getResultList();
    }

    private CriteriaQuery<Opetussuunnitelma> getJulkaistutQuery(OpetussuunnitelmaQuery pquery) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Opetussuunnitelma> query = builder.createQuery(Opetussuunnitelma.class);
        Root<Opetussuunnitelma> ops = query.from(Opetussuunnitelma.class);

        List<Predicate> ehdot = new ArrayList<>();

        // Perusteet joista julkaisu ja ovat arkistoimattomia
        ehdot.add(builder.and(
                builder.notEqual(ops.get(Opetussuunnitelma_.tila), Tila.POISTETTU),
                builder.or(
                    builder.equal(ops.get(Opetussuunnitelma_.tila), Tila.JULKAISTU),
                    builder.greaterThan(opsJulkaistuSubQuery(ops, builder), 0l))));

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
    public Page<OpetussuunnitelmaJulkinenDto> getAllJulkaistutOpetussuunnitelmat(OpetussuunnitelmaJulkaistuQuery query) {
        Pageable pageable = PageRequest.of(query.getSivu(), query.getSivukoko());

        List<String> koulutustyypit = query.getKoulutustyypit();
        if (CollectionUtils.isEmpty(koulutustyypit)) {
            koulutustyypit = Arrays.asList("");
        }

        return julkaisuRepository.findAllJulkisetJulkaisut(
                query.getNimi(),
                query.getKieli(),
                query.getPerusteenDiaarinumero(),
                koulutustyypit,
                pageable)
                .map(this::convertToOpetussuunnitelmaDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Object getJulkaistuSisaltoObjectNode(Long id, List<String> queryList) {
        Opetussuunnitelma opetussuunnitelma = opetussuunnitelmaRepository.findById(id).orElse(null);

        if (opetussuunnitelma == null || opetussuunnitelma.getTila().equals(Tila.POISTETTU)) {
            throw new NotExistsException("");
        }

        String query = queryList.stream().reduce("$", (subquery, element) -> {
            if (NumberUtils.isCreatable(element)) {
                return subquery + String.format("?(@.id==%s)", element);
            }
            return subquery + "." + element;
        });

        try {
            return objectMapper.readValue(julkaisuRepository.findJulkaisutByJsonPath(id, query), Object.class);
        } catch (JsonProcessingException e) {
            log.error(Throwables.getStackTraceAsString(e));
            return null;
        }
    }

    private OpetussuunnitelmaJulkinenDto convertToOpetussuunnitelmaDto(String obj) {
        try {
            return objectMapper.readValue(obj, OpetussuunnitelmaJulkinenDto.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<OpetussuunnitelmaJulkinenDto> getKaikkiJulkaistutOpetussuunnitelmat() {
        return julkaisuRepository.findAllJulkaistutOpetussuunnitelmat().stream()
                .map(this::convertToOpetussuunnitelmaDto).collect(Collectors.toList());
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
        return getSivutettu(tyyppi, tila, koulutustyyppi, nimi, "luotu", "DESC", "fi", sivu, sivukoko);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OpetussuunnitelmaInfoDto> getSivutettu(Tyyppi tyyppi, Tila tila, KoulutusTyyppi koulutustyyppi, String nimi, String jarjestys, String jarjestysSuunta, String kieli, int sivu, int sivukoko) {
        Page<Object[]> opetussuunnitelmat;
        Pageable pageable = PageRequest.of(sivu, sivukoko, Sort.by(Sort.Direction.fromString(jarjestysSuunta), jarjestys));
        if (SecurityUtil.isUserAdmin()) {
            opetussuunnitelmat = opetussuunnitelmaRepository.findSivutettuAdmin(
                    tyyppi,
                    tila.name(),
                    nimi,
                    koulutustyyppi != null ? koulutustyyppi.name() : "",
                    Kieli.of(kieli),
                    pageable);
        } else {
            Set<String> organisaatiot = SecurityUtil.getOrganizations(EnumSet.allOf(RolePermission.class));
            opetussuunnitelmat = opetussuunnitelmaRepository.findSivutettu(
                    tyyppi,
                    tila.name(),
                    nimi,
                    koulutustyyppi != null ? koulutustyyppi.name() : "",
                    organisaatiot,
                    Kieli.of(kieli),
                    pageable);
        }

        return opetussuunnitelmat.map(ops -> mapper.map(ops[0], OpetussuunnitelmaInfoDto.class));
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
    public List<OpetussuunnitelmaTilastoDto> getOpetussuunnitelmaTilastot() {
        Map<Long, Date> opetussuunnitelmaWithLatestTilaUpdateTimesMaps = opetussuunnitelmaRepository.findAllWithLatestTilaUpdateDate(Tyyppi.OPS.name())
                .stream().collect(Collectors.toMap(OpetussuunnitelmaWithLatestTilaUpdateTime::getId, OpetussuunnitelmaWithLatestTilaUpdateTime::getViimeisinTilaMuutosAika));

        return mapper.mapAsList(opetussuunnitelmaRepository.findAllByTyyppi(Tyyppi.OPS), OpetussuunnitelmaTilastoDto.class).stream()
                .peek(dto -> {
                    dto.setViimeisinTilaMuutosAika(opetussuunnitelmaWithLatestTilaUpdateTimesMaps.get(dto.getId()));
                    fetchKuntaNimet(dto);
                    fetchOrganisaatioNimet(dto);
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PerusteDto getPeruste(Long opsId) {
        Opetussuunnitelma ops = opetussuunnitelmaRepository.findOne(opsId);
        assertExists(ops, "Pyydettyä opetussuunnitelmaa ei ole olemassa");
        return eperusteetService.getPeruste(ops.getPerusteenDiaarinumero());
    }

    @Override
    public PerusteInfoDto getPerusteBase(Long id) {
        Opetussuunnitelma ops = opetussuunnitelmaRepository.findOne(id);
        PerusteDto perusteDto = eperusteetService.getPerusteById(ops.getCachedPeruste().getPerusteId());
        return mapper.map(perusteDto, PerusteInfoDto.class);
    }

    @Override
    public NavigationNodeDto buildNavigation(Long opsId, String kieli) {
        NavigationNodeDto navigationNodeDto = dispatcher.get(opsId, NavigationBuilder.class).buildNavigation(opsId, kieli);
        return siirraLiitteetLoppuun(navigationNodeDto);
    }

    @Override
    public NavigationNodeDto buildNavigationPublic(Long opsId, String kieli, Integer revision) {
        NavigationNodeDto navigationNodeDto = dispatcher.get(opsId, NavigationBuilderPublic.class).buildNavigation(opsId, kieli, revision);
        siirraLiitteetLoppuun(navigationNodeDto);
        NavigationUtil.asetaNumerointi(navigationNodeDto);
        return navigationNodeDto;
    }

    @Override
    @Transactional(readOnly = true)
    public OpetussuunnitelmaKevytDto getOpetussuunnitelma(Long id) {
        Opetussuunnitelma ops = opetussuunnitelmaRepository.findOne(id);
        assertExists(ops, "Pyydettyä opetussuunnitelmaa ei ole olemassa");
        OpetussuunnitelmaKevytDto dto = mapper.map(ops, OpetussuunnitelmaKevytDto.class);
        fetchKuntaNimet(dto);
        fetchOrganisaatioNimet(dto);

        if (SecurityUtil.isAuthenticated()) {
            fetchPeriytyvatPohjat(dto, dto.getPohja());
            if (!ObjectUtils.isEmpty(dto.getPeriytyvatPohjat())) {
                dto.setPeriytyvatPohjat(Lists.reverse(dto.getPeriytyvatPohjat()));
            }
            fetchOpsitJoissaPohjana(dto);
        }
        return dto;
    }

    @Override
    public OpetussuunnitelmaNimiDto getOpetussuunnitelmaNimi(Long opsId) {
        return mapper.map(opetussuunnitelmaRepository.findOne(opsId), OpetussuunnitelmaNimiDto.class);
    }

    @Override
    @Transactional(readOnly = true)
    public Set<OpsVuosiluokkakokonaisuusKevytDto> getOpetussuunnitelmanPohjanVuosiluokkakokonaisuudet(Long id) {
        Opetussuunnitelma ops = opetussuunnitelmaRepository.findOne(id);
        assertExists(ops, "Pyydettyä opetussuunnitelmaa ei ole olemassa");
        assertExists(ops.getPohja(), "Pyydettyä opetussuunnitlman pohjaa ei ole olemassa");
        OpetussuunnitelmaKevytDto dto = mapper.map(ops.getPohja(), OpetussuunnitelmaKevytDto.class);
        return dto.getVuosiluokkakokonaisuudet();
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
        Set<String> kaikki = kayttajanOrganisaatioOids();
        return opetussuunnitelmaRepository.findOpsPohja(kaikki).stream()
            .map(p -> mapper.map(p, OpetussuunnitelmaInfoDto.class))
            .peek(this::fetchKuntaNimet)
            .peek(this::fetchOrganisaatioNimet)
            .collect(toList());
    }

    private Set<String> kayttajanOrganisaatioOids() {
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
        return kaikki;
    }

    @Override
    public OpetussuunnitelmaKevytDto getOpetussuunnitelmaOrganisaatiotarkistuksella(Long opsId) {
        Opetussuunnitelma ops = opetussuunnitelmaRepository.findOne(opsId);
        if (ops.getTyyppi().equals(Tyyppi.POHJA) && ops.getTila().equals(Tila.VALMIS)) {
            return self.getOpetussuunnitelma(opsId);
        }

        Set<String> kaikki = kayttajanOrganisaatioOids();
        return mapper.map(opetussuunnitelmaRepository.findByOpsIdAndOrganisaatiot(opsId, kaikki), OpetussuunnitelmaKevytDto.class);
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<PerusteLaajaalainenosaaminenDto> getLaajaalaisetosaamiset(Long id) {
        Opetussuunnitelma ops = opetussuunnitelmaRepository.findOne(id);
        assertExists(ops, "Pyydettyä opetussuunnitelmaa ei ole olemassa");

        PerusteDto peruste = eperusteetService.getPeruste(ops.getPerusteenDiaarinumero());

        if (peruste.getPerusopetus() != null) {
            return peruste.getPerusopetus().getLaajaalaisetosaamiset();
        }

        if (peruste.getAipe() != null) {
            return peruste.getAipe().getLaajaalaisetosaamiset();
        }

        throw new BusinessRuleViolationException("perusteen-laaja-alaisia-osaamisia-ei-loydy");
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

    private void fetchPeriytyvatPohjat(OpetussuunnitelmaKevytDto rootOps, OpetussuunnitelmaBaseDto pohjaDto) {
        if (pohjaDto == null) {
            return;
        }

        Set<String> kayttajaOids = kayttajanOrganisaatioOids();
        Opetussuunnitelma pohja = opetussuunnitelmaRepository.findById(pohjaDto.getId()).orElseThrow();

        OpetussuunnitelmaNimiDto pohjaNimi = new OpetussuunnitelmaNimiDto();
        boolean hasOikeudet = pohja.getOrganisaatiot().stream().anyMatch(kayttajaOids::contains);

        if (hasOikeudet) {
            pohjaNimi.setId(pohja.getId());
        }
        pohjaNimi.setNimi(pohjaDto.getNimi());
        if (rootOps.getPeriytyvatPohjat() == null) {
            rootOps.setPeriytyvatPohjat(new ArrayList<>());
        }
        rootOps.getPeriytyvatPohjat().add(pohjaNimi);

        if (pohja.getPohja() != null) {
            fetchPeriytyvatPohjat(rootOps, mapper.map(pohja.getPohja(), OpetussuunnitelmaKevytDto.class));
        }
    }

    private void fetchOpsitJoissaPohjana(OpetussuunnitelmaKevytDto rootOps) {
        Set<String> kayttajaOids = kayttajanOrganisaatioOids();
        Set<Opetussuunnitelma> opsitJoissaPohjana = opetussuunnitelmaRepository.findAllByPohjaId(rootOps.getId());
        List<OpetussuunnitelmaNimiDto> opsit = new ArrayList<>();

        opsitJoissaPohjana.forEach(ops -> {
            OpetussuunnitelmaNimiDto opsNimiDto = new OpetussuunnitelmaNimiDto();
            boolean hasOikeudet = ops.getOrganisaatiot().stream().anyMatch(kayttajaOids::contains);
            if (hasOikeudet) {
                opsNimiDto.setId(ops.getId());
            }
            opsNimiDto.setNimi(mapper.map(ops.getNimi(),LokalisoituTekstiDto.class));
            opsit.add(opsNimiDto);
        });
        rootOps.setJoissaPohjana(opsit);
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

        asetaKoulutuksenJarjestaja(opetussuunnitelmaDto);
    }

    private void asetaKoulutuksenJarjestaja(OpetussuunnitelmaBaseDto opetussuunnitelmaDto) {
        if (ObjectUtils.isEmpty(opetussuunnitelmaDto.getOrganisaatiot())) {
            return;
        }

        Set<OrganisaatioDto> oppilaitokset = opetussuunnitelmaDto.getOrganisaatiot().stream()
                .filter(org -> org.getTyypit().contains(OrganisaatioTyyppi.OPPILAITOS))
                .collect(Collectors.toUnmodifiableSet());

        Set<OrganisaatioDto> kunnat = opetussuunnitelmaDto.getOrganisaatiot().stream()
                .filter(org -> org.getTyypit().contains(OrganisaatioTyyppi.KUNTA))
                .collect(Collectors.toUnmodifiableSet());

        if (oppilaitokset.size() == 1) {
            opetussuunnitelmaDto.setKoulutuksenjarjestaja(oppilaitokset.iterator().next());
        } else if(kunnat.size() == 1) {
            opetussuunnitelmaDto.setKoulutuksenjarjestaja(kunnat.iterator().next());
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

        kasitteleTekstit(pohja.getTekstit(), ops.getTekstit(), OpetussuunnitelmaLuontiDto.Luontityyppi.VIITTEILLA);
    }

    @Override
    @Transactional
    public OpetussuunnitelmaDto addOpetussuunnitelma(OpetussuunnitelmaLuontiDto opetussuunnitelmaLuontiDto) {

        if (opetussuunnitelmaLuontiDto.getId() != null) {
            throw new BusinessRuleViolationException("Uudessa opetussuunnitelmassa on id");
        }

        opetussuunnitelmaLuontiDto.setTyyppi(Tyyppi.OPS);
        Opetussuunnitelma ops = mapper.map(opetussuunnitelmaLuontiDto, Opetussuunnitelma.class);

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
                    opetussuunnitelmaLuontiDto.getKoulutustyyppi());
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
            ops.setPohja(opetussuunnitelmaLuontiDto.getLuontityyppi().equals(OpetussuunnitelmaLuontiDto.Luontityyppi.KOPIO) && pohja.getPohja() != null ? pohja.getPohja() : pohja);
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
                luoOpsPohjasta(pohja, ops, opetussuunnitelmaLuontiDto.getLuontityyppi());

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
        if(ops.getPohja().getTyyppi().equals(Tyyppi.POHJA) && !Tila.VALMIS.equals(ops.getPohja().getTila()) & !Tila.JULKAISTU.equals(ops.getPohja().getTila())) {
            throw new BusinessRuleViolationException("pohjan-pitaa-olla-julkaistu");
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

    private void luoOpsPohjasta(Opetussuunnitelma pohja, Opetussuunnitelma ops, OpetussuunnitelmaLuontiDto.Luontityyppi luontityyppi) {

        boolean onPohjastaTehtyPohja = isPohjastaTehtyPohja(pohja);
        boolean teeKopio = luontityyppi != OpetussuunnitelmaLuontiDto.Luontityyppi.LEGACY;
        kasitteleTekstit(pohja.getTekstit(), ops.getTekstit(), luontityyppi);

        Copier<Oppiaine> oppiaineCopier = Oppiaine.basicCopier();

        if (luontityyppi == OpetussuunnitelmaLuontiDto.Luontityyppi.VIITTEILLA) {
            oppiaineCopier = oppiaineCopier.and(Oppiaine.viitteellaCopier());
        }

        if (luontityyppi == OpetussuunnitelmaLuontiDto.Luontityyppi.KOPIO) {
            oppiaineCopier = oppiaineCopier.and(Oppiaine.perusopetusCopier());
        } else if (luontityyppi == OpetussuunnitelmaLuontiDto.Luontityyppi.VIITTEILLA) {
            oppiaineCopier = oppiaineCopier.and(Oppiaine.perusopetusCopier(false));
        }

        final Copier<Oppiaine> oppiainePerusCopier = oppiaineCopier;
        if (teeKopio && !onPohjastaTehtyPohja ) {
            ConstructedCopier<Oppiaine> omConst = oppiainePerusCopier.construct(oa -> new Oppiaine(oa.getTunniste()));
            oppiaineCopier = oppiaineCopier.and(Oppiaine.oppimaaraCopier(omConst));
        }
        ConstructedCopier<OpsOppiaine> opsOppiaineCopier = OpsOppiaine.copier(
                oppiaineCopier.construct(existing -> {
                        if (luontityyppi == OpetussuunnitelmaLuontiDto.Luontityyppi.LEGACY) {
                            return existing;
                        }
                        return new Oppiaine(existing.getTunniste());
                    }), teeKopio);
        Stream<OpsOppiaine> oppiaineetToCopy = pohja.getOppiaineet().stream();
        ops.setOppiaineet(oppiaineetToCopy.map(opsOppiaineCopier::copy).collect(toSet()));
        ops.getOppiaineet().forEach(opsOppiaine -> {
            if (opsOppiaine.getOppiaine().getLiittyvaOppiaine() != null) { // korjataan kopioitu valinnaisen oppiaineen liittyvan oppiaine oman opsin oppiaineeseen
                opsOppiaine.getOppiaine().setLiittyvaOppiaine(ops.getOppiaineet().stream()
                        .map(OpsOppiaine::getOppiaine)
                        .filter(oppiaine -> oppiaine.getTunniste().equals(opsOppiaine.getOppiaine().getLiittyvaOppiaine().getTunniste()))
                        .findFirst()
                        .orElse(null));
            }
        });

        Set<OpsVuosiluokkakokonaisuus> ovlkoot = pohja.getVuosiluokkakokonaisuudet().stream()
                .filter(ovlk -> ops.getVuosiluokkakokonaisuudet().stream()
                        .anyMatch(vk -> vk.getVuosiluokkakokonaisuus().getTunniste()
                                .equals(ovlk.getVuosiluokkakokonaisuus().getTunniste())))
                .map(ovlk -> luontityyppi == OpetussuunnitelmaLuontiDto.Luontityyppi.KOPIO
                        ? new OpsVuosiluokkakokonaisuus(Vuosiluokkakokonaisuus.copyOf(ovlk.getVuosiluokkakokonaisuus()), true)
                        : new OpsVuosiluokkakokonaisuus(Vuosiluokkakokonaisuus.copyEmpty(ovlk.getVuosiluokkakokonaisuus()), true))
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

    private void kasitteleTekstit(TekstiKappaleViite vanha, TekstiKappaleViite parent, OpetussuunnitelmaLuontiDto.Luontityyppi luontityyppi) {
        List<TekstiKappaleViite> vanhaLapset = vanha.getLapset();
        if (vanhaLapset != null) {
            vanhaLapset.stream()
                    .filter(vanhaTkv -> vanhaTkv.getTekstiKappale() != null)
                    .forEach(vanhaTkv -> {
                        TekstiKappaleViite tkv = copyTekstikappaleViiteFromOpsToOps(parent, vanhaTkv, luontityyppi);
                        kasitteleTekstit(vanhaTkv, tkv, luontityyppi);
                    });
        }
    }

    private TekstiKappaleViite copyTekstikappaleViiteFromOpsToOps(TekstiKappaleViite parent, TekstiKappaleViite vanhaTkv, OpetussuunnitelmaLuontiDto.Luontityyppi luontityyppi) {
        TekstiKappaleViite tkv = viiteRepository.save(new TekstiKappaleViite());
        tkv.setOmistussuhde(Omistussuhde.OMA);
        tkv.setLapset(new ArrayList<>());
        tkv.setVanhempi(parent);
        tkv.setPakollinen(vanhaTkv.isPakollinen());
        tkv.setNaytaPerusteenTeksti(vanhaTkv.isNaytaPerusteenTeksti());
        tkv.setPerusteTekstikappaleId(vanhaTkv.getPerusteTekstikappaleId());
        tkv.setLiite(vanhaTkv.isLiite());
        TekstiKappale copy = vanhaTkv.getTekstiKappale().copy();
        if (luontityyppi.equals(OpetussuunnitelmaLuontiDto.Luontityyppi.VIITTEILLA)) {
            copy.setTeksti(null);
        }
        copy = tekstiKappaleRepository.save(copy);
        tkv.setTekstiKappale(copy);
        parent.getLapset().add(tkv);
        return tkv;
    }

    private Opetussuunnitelma addPohjaLisaJaEsiopetus(Opetussuunnitelma ops, PerusteDto peruste, OpetussuunnitelmaLuontiDto pohjaDto) {
        ops.setKoulutustyyppi(peruste.getKoulutustyyppi());
        lisaaTekstipuuPerusteesta(peruste.getTekstiKappaleViiteSisalto(), ops);
        return ops;
    }

    private Opetussuunnitelma addPohjaPerusopetus(
            Opetussuunnitelma ops,
            PerusteDto peruste,
            OpetussuunnitelmaLuontiDto pohjaDto
    ) {
        Long opsId = ops.getId();
        PerusopetuksenPerusteenSisaltoDto sisalto = peruste.getPerusopetus();
        lisaaTekstipuuPerusteesta(sisalto.getSisalto(), ops);

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

        opetussuunnitelmaRepository.findByPerusteId(pohja.getCachedPeruste().getPerusteId()).forEach(opetussuunnitelma -> {
            muokkaustietoService.addOpsMuokkausTieto(opetussuunnitelma.getId(), opetussuunnitelma, MuokkausTapahtuma.PAIVITYS, "tapahtuma-opetussuunnitelma-peruste-paivitys");
        });
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
        if (sisalto != null) {
            TekstiKappaleViite tekstiKappaleViite = CollectionUtil.mapRecursive(sisalto,
                    fi.vm.sade.eperusteet.ylops.service.external.impl.perustedto.TekstiKappaleViiteDto::getLapset,
                    TekstiKappaleViite::getLapset,
                    viiteDto -> {
                        TekstiKappale kpl = new TekstiKappale();
                        TekstiKappaleViite result = new TekstiKappaleViite();
                        if (viiteDto.getTekstiKappale() != null) {
                            TekstiKappale tk = mapper.map(viiteDto.getTekstiKappale(), TekstiKappale.class);
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
    }

    private TekstiKappaleViite tekstikappaleViiteRootSisallosta(fi.vm.sade.eperusteet.ylops.service.external.impl.perustedto.TekstiKappaleViiteDto sisalto) {
        return CollectionUtil.mapRecursive(sisalto,
                fi.vm.sade.eperusteet.ylops.service.external.impl.perustedto.TekstiKappaleViiteDto::getLapset,
                TekstiKappaleViite::getLapset,
                viiteDto -> {
                    TekstiKappale kpl = new TekstiKappale();
                    TekstiKappaleViite result = new TekstiKappaleViite();
                    if (viiteDto.getTekstiKappale() != null) {
                        result.setPerusteTekstikappaleId(viiteDto.getTekstiKappale().getId());

                        TekstiKappale tk = mapper.map(viiteDto.getTekstiKappale(), TekstiKappale.class);
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

        if (ops.getKoulutustyyppi().equals(KoulutusTyyppi.PERUSOPETUS)) {
            updateOpsPerusopetus(ops, opetussuunnitelmaDto);
        } else {
            mapper.map(opetussuunnitelmaDto, ops);
        }

        ops = opetussuunnitelmaRepository.save(ops);

        muokkaustietoService.addOpsMuokkausTieto(ops.getId(), ops, MuokkausTapahtuma.PAIVITYS);

        return mapper.map(ops, OpetussuunnitelmaDto.class);
    }

    private void updateOpsPerusopetus(Opetussuunnitelma ops, OpetussuunnitelmaDto opetussuunnitelmaDto) {
        Opetussuunnitelma pohja = ops.getPohja();
        boolean teeKopio = pohja.getTyyppi() == Tyyppi.POHJA;
        Map<String, OpsVuosiluokkakokonaisuusDto> lisattavatVlk = pohja.getVuosiluokkakokonaisuudet().stream()
                .filter(ovlk -> ops.getVuosiluokkakokonaisuudet().stream()
                        .noneMatch(vk -> vk.getVuosiluokkakokonaisuus().getTunniste().getId()
                                .equals(ovlk.getVuosiluokkakokonaisuus().getTunniste().getId())))
                .filter(ovlk -> opetussuunnitelmaDto.getVuosiluokkakokonaisuudet().stream()
                        .anyMatch(vk -> UUID.fromString(vk.getVuosiluokkakokonaisuus().getTunniste().toString())
                                .equals(ovlk.getVuosiluokkakokonaisuus().getTunniste().getId())))
                .map(ovlk -> teeKopio
                        ? new OpsVuosiluokkakokonaisuus(Vuosiluokkakokonaisuus.copyOf(ovlk.getVuosiluokkakokonaisuus()), true)
                        : new OpsVuosiluokkakokonaisuus(Vuosiluokkakokonaisuus.copyEmpty(ovlk.getVuosiluokkakokonaisuus()), true))
                .map(ovlk -> mapper.map(ovlk, OpsVuosiluokkakokonaisuusDto.class))
                .collect(toMap(vlk -> vlk.getVuosiluokkakokonaisuus().getTunniste().toString(), vlk -> vlk));

        opetussuunnitelmaDto.setVuosiluokkakokonaisuudet(opetussuunnitelmaDto.getVuosiluokkakokonaisuudet().stream().map(vlk -> {
            if(lisattavatVlk.containsKey(vlk.getVuosiluokkakokonaisuus().getTunniste().toString())) {
                return lisattavatVlk.get(vlk.getVuosiluokkakokonaisuus().getTunniste().toString());
            }

            return vlk;
        }).collect(Collectors.toSet()));

        mapper.map(opetussuunnitelmaDto, ops);

        ops.getVuosiluokkakokonaisuudet().forEach(ovlk -> {
            if (ovlk.getVuosiluokkakokonaisuus().getId() == null) {
                vuosiluokkakokonaisuusRepository.save(ovlk.getVuosiluokkakokonaisuus());
            }
        });
    }

    @Deprecated
    @Override
    public OpetussuunnitelmaDto importPerusteTekstit(Long id) {
        return importPerusteTekstit(id, false);
    }

    @Deprecated
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

        // Ei sallita vuoluokkakokonaisuuksien muutoksia kuin luonnostilassa
        if (opetussuunnitelmaDto.getTila() != Tila.LUONNOS) {
            Set<Long> vlkIds = opetussuunnitelmaDto.getVuosiluokkakokonaisuudet().stream()
                    .map(vlk -> vlk.getVuosiluokkakokonaisuus().getId())
                    .collect(Collectors.toSet());
            Set<Long> oldVlkIds = ops.getVuosiluokkakokonaisuudet().stream()
                    .map(vlk -> vlk.getVuosiluokkakokonaisuus().getId())
                    .collect(Collectors.toSet());
            if (!vlkIds.containsAll(oldVlkIds)) {
                throw new BusinessRuleViolationException("julkaistun-opetussuunnitelman-vuosiluokkakokonaisuuksia-ei-voi-poistaa");
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

    @Override
    public OpetussuunnitelmaDto updateTila(Long id, Tila tila) {
        Opetussuunnitelma ops = opetussuunnitelmaRepository.findOne(id);
        assertExists(ops, "Opetussuunnitelmaa ei ole olemassa");


        if (ops.getTyyppi() == Tyyppi.POHJA && tila == Tila.JULKAISTU) {
            tila = Tila.VALMIS;
        }

        if (tila != ops.getTila() && ops.getTila().mahdollisetSiirtymat(ops.getTyyppi() == Tyyppi.POHJA).contains(tila)) {
            if (ops.getTyyppi() == Tyyppi.OPS && ops.getTila() == Tila.POISTETTU && tila == Tila.LUONNOS) {
                dokumenttiRepository.deleteAllById(ops.getJulkaisut().stream().map(OpetussuunnitelmanJulkaisu::getDokumentit).flatMap(Collection::stream).collect(toList()));
                julkaisuRepository.deleteAll(ops.getJulkaisut());
            }

            ops.setTila(tila);
        }

        ops = opetussuunnitelmaRepository.save(ops);
        muokkaustietoService.addOpsMuokkausTieto(id, ops, MuokkausTapahtuma.PAIVITYS, "tapahtuma-opetussuunnitelma-tila-" + tila);
        return mapper.map(ops, OpetussuunnitelmaDto.class);
    }

    @Override
    public List<Validointi> validoiOpetussuunnitelma(Long id) {
        return validointiService.validoiOpetussuunnitelma(id);
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
        julkaisuRepository.deleteAll(ops.getJulkaisut());

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
    @Transactional(readOnly = true)
    public TekstiKappaleViitePerusteTekstillaDto getTekstitPerusteenTeksteilla(@P("opsId") final Long opsId) {
        TekstiKappaleViitePerusteTekstillaDto tekstit = getTekstit(opsId, TekstiKappaleViitePerusteTekstillaDto.class);
        PerusteDto perusteDto = getPeruste(opsId);

        if (perusteDto.getTekstiKappaleViiteSisalto() != null) {
            Map<Long, fi.vm.sade.eperusteet.ylops.service.external.impl.perustedto.TekstiKappaleDto> perusteenTekstikappaleet = CollectionUtil.treeToStream(
                            perusteDto.getTekstiKappaleViiteSisalto(),
                            fi.vm.sade.eperusteet.ylops.service.external.impl.perustedto.TekstiKappaleViiteDto::getLapset)
                    .filter(viite -> viite.getTekstiKappale() != null)
                    .collect(toMap(viite -> viite.getTekstiKappale().getId(), perusteenTekstikappale -> perusteenTekstikappale.getTekstiKappale()));

            return CollectionUtil.mapRecursive(tekstit,
                    TekstiKappaleViitePerusteTekstillaDto::getLapset,
                    viiteDto -> {
                        viiteDto.setPerusteenTekstikappale(perusteenTekstikappaleet.get(viiteDto.getPerusteTekstikappaleId()));
                        return viiteDto;
                    });
        }

        return tekstit;
    }

    @Override
    public TekstiKappaleViiteDto.Matala addTekstiKappale(Long opsId, TekstiKappaleViiteDto.Matala viite) {
        Opetussuunnitelma ops = opetussuunnitelmaRepository.findOne(opsId);
        assertExists(ops, "Opetussuunnitelmaa ei ole olemassa");
        // Lisätään viite juurinoden alle
        TekstiKappaleViiteDto.Matala tekstiKappaleViiteDto = tekstiKappaleViiteService.addTekstiKappaleViite(opsId, ops.getTekstit().getId(), viite);
        return addTekstikappaleToChildOpetussuunnitelmat(opsId, tekstiKappaleViiteDto, true);
    }

    @Override
    public TekstiKappaleViiteDto.Matala addTekstiKappaleLapsi(Long opsId, Long parentId,
                                                              TekstiKappaleViiteDto.Matala viite) {
        // Lisätään viite parent-noden alle
        TekstiKappaleViiteDto.Matala tekstiKappaleViiteDto = tekstiKappaleViiteService.addTekstiKappaleViite(opsId, parentId, viite);
        return addTekstikappaleToChildOpetussuunnitelmat(opsId, tekstiKappaleViiteDto, false);
    }

    private TekstiKappaleViiteDto.Matala addTekstikappaleToChildOpetussuunnitelmat(Long opsId, TekstiKappaleViiteDto.Matala tekstiKappaleViiteDto, boolean juuritaso) {
        TekstiKappaleViite tekstiKappaleViite = viiteRepository.findOne(tekstiKappaleViiteDto.getId());
        opetussuunnitelmaRepository.findAllByPohjaId(opsId).forEach(opetussuunnitelma -> {
            Optional<UUID> parentTunniste = Optional.ofNullable(tekstiKappaleViite).map(TekstiKappaleViite::getVanhempi).map(TekstiKappaleViite::getTekstiKappale).map(TekstiKappale::getTunniste);
            Optional<TekstiKappaleViite> parentViite = CollectionUtil.treeToStream(opetussuunnitelma.getTekstit(), TekstiKappaleViite::getLapset)
                    .filter(lapsi -> lapsi.getTekstiKappale() != null)
                    .filter(lapsi -> lapsi.getTekstiKappale().getTunniste().equals(parentTunniste.orElse(null))).findFirst();

            Optional<TekstiKappaleViite> luotuViite = Optional.empty();
            if (juuritaso || parentTunniste.isEmpty() || parentViite.isEmpty()) {
                luotuViite = Optional.of(copyTekstikappaleViiteFromOpsToOps(opetussuunnitelma.getTekstit(), tekstiKappaleViite, OpetussuunnitelmaLuontiDto.Luontityyppi.VIITTEILLA));
            } else if (parentViite.isPresent()) {
                luotuViite = Optional.of(copyTekstikappaleViiteFromOpsToOps(parentViite.get(), tekstiKappaleViite, OpetussuunnitelmaLuontiDto.Luontityyppi.VIITTEILLA));
            }

            luotuViite.ifPresent(kappaleViite -> muokkaustietoService.addOpsMuokkausTieto(opetussuunnitelma, kappaleViite, MuokkausTapahtuma.LUONTI));

        });

        return tekstiKappaleViiteDto;
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
    public void syncTekstitPohjasta(Long opsId) {
        Opetussuunnitelma ops = opetussuunnitelmaRepository.findOne(opsId);
        dispatcher.get(OpsPohjaSynkronointi.class).syncTekstitPohjasta(ops.getId(), ops.getPohja().getId());
        muokkaustietoService.poistaOpsMuokkaustieto(ops, MuokkaustietoLisatieto.POHJA_TEKSTI_SYNKRONOITU_VIRHE);
        opetussuunnitelmaAsyncTekstitPohjastaService.syncTekstitPohjastaKaikki(ops);
    }

    @Override
    public boolean opetussuunnitelmanPohjallaUusiaTeksteja(Long id) {
        return dispatcher.get(id, OpsPohjaSynkronointi.class).opetussuunnitelmanPohjallaUusiaTeksteja(id);
    }

    @Override
    public boolean pohjanPerustePaivittynyt(Long opsId) {
        Opetussuunnitelma ops = opetussuunnitelmaRepository.findOne(opsId);

        if (!ops.getTyyppi().equals(Tyyppi.POHJA)) {
            throw new BusinessRuleViolationException("Tarkistus sallittu vain oph pohjilla");
        }

        Date perusteenJulkaisuaika = eperusteetService.viimeisinPerusteenJulkaisuaika(ops.getCachedPeruste().getPerusteId());
        return perusteenJulkaisuaika.compareTo(ops.getViimeisinSyncPvm() != null ? ops.getViimeisinSyncPvm() : new Date(0l)) > 0;
    }

    @Override
    public OpetussuunnitelmaExportDto getExportedOpetussuunnitelma(Long id) {
        final OpetussuunnitelmaExportDto exported = dispatcher.get(id, OpsExport.class).export(id);
        return exported;
    }

    @Override
    public OpetussuunnitelmaExportDto getOpetussuunnitelmaJulkaistuSisalto(Long opsId) {
        return getOpetussuunnitelmaJulkaistuSisalto(opsId, null);
    }

    @Override
    public OpetussuunnitelmaExportDto getOpetussuunnitelmaJulkaistuSisalto(Long opsId, Integer revision) {
        Opetussuunnitelma ops = opetussuunnitelmaRepository.findOne(opsId);
        if (ops == null || ops.getTila().equals(Tila.POISTETTU)) {
            throw new NotExistsException("");
        }

        if (revision != null && revision == 0) {
            OpetussuunnitelmaExportDto esikatseluDto = getExportedOpetussuunnitelma(opsId);
            esikatseluDto.setViimeisinJulkaisuAika(null);
            return esikatseluDto;
        }

        OpetussuunnitelmanJulkaisu julkaisu;
        if (revision != null) {
            julkaisu = julkaisuRepository.findByOpetussuunnitelmaAndRevision(ops, revision);
        } else {
            julkaisu = julkaisuRepository.findFirstByOpetussuunnitelmaOrderByRevisionDesc(ops);
        }

        if (julkaisu != null) {
            OpetussuunnitelmaExportDto julkaistuDto = self.getOpetussuunnitelmanJulkaisuWithData(opsId, julkaisu);
            julkaistuDto.setViimeisinJulkaisuAika(julkaisu.getLuotu());
            return julkaistuDto;
        }

        return null;
    }

    @Override
    public JsonNode getJulkaistuOpetussuunnitelmaPeruste(Long opsId) {
        OpetussuunnitelmaExportDto opetussuunnitelmaExportDto = getOpetussuunnitelmaJulkaistuSisalto(opsId);

        if (opetussuunnitelmaExportDto == null) {
            return null;
        }

        return eperusteetService.getPerusteenJulkaisuByGlobalversionMuutosaika(
                opetussuunnitelmaExportDto.getPeruste().getId(),
                opetussuunnitelmaExportDto.getPeruste().getGlobalVersion().getAikaleima());

    }

    public OpetussuunnitelmaExportDto getOpetussuunnitelmanJulkaisuWithData(Long opsId, OpetussuunnitelmanJulkaisu julkaisu) {
        ObjectNode data = julkaisu.getData().getOpsData();
        try {
            OpetussuunnitelmaExportDto exportDto = objectMapper.treeToValue(data, dispatcher.get(opsId, OpsExport.class).getExportClass());
            exportDto.setTila(Tila.JULKAISTU);
            return exportDto;
        } catch (JsonProcessingException e) {
            log.error(Throwables.getStackTraceAsString(e));
            throw new BusinessRuleViolationException("opetussuunnitelman-haku-epaonnistui");
        }
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
                        .filter(viiteDto -> viiteDto.getTekstiKappale() != null
                                && viiteDto.getTekstiKappale().getTeksti() != null
                                && Objects.equals(tekstikappaleId, viiteDto.getTekstiKappale().getId()))
                        .findFirst()
                        .orElse(null);
                if (perusteenTekstikappaleViite != null) {
                    return new TekstiKappaleDto(
                            new LokalisoituTekstiDto(perusteenTekstikappaleViite.getTekstiKappale().getNimi().asMap()),
                            new LokalisoituTekstiDto(perusteenTekstikappaleViite.getTekstiKappale().getTeksti().asMap()),
                            null);
                }
            }
        }

        return null;
    }

    @Override
    public void palautaTekstirakenne(Long id) {
        Opetussuunnitelma opetussuunnitelma = opetussuunnitelmaRepository.findOne(id);
        Long edellinenId = opetussuunnitelmaRepository.findEdellinenTekstitId(id);
        TekstiKappaleViite tekstiKappaleViite = viiteRepository.findOne(edellinenId);
        opetussuunnitelma.setTekstit(tekstiKappaleViite);
    }
}
