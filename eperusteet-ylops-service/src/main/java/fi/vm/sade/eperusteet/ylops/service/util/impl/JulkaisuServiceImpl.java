package fi.vm.sade.eperusteet.ylops.service.util.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Throwables;
import com.google.common.collect.Sets;
import fi.vm.sade.eperusteet.ylops.domain.KoulutustyyppiToteutus;
import fi.vm.sade.eperusteet.ylops.domain.MuokkausTapahtuma;
import fi.vm.sade.eperusteet.ylops.domain.Tyyppi;
import fi.vm.sade.eperusteet.ylops.domain.ops.JulkaistuOpetussuunnitelmaData;
import fi.vm.sade.eperusteet.ylops.domain.ops.JulkaistuOpetussuunnitelmaTila;
import fi.vm.sade.eperusteet.ylops.domain.ops.JulkaisuTila;
import fi.vm.sade.eperusteet.ylops.domain.ops.Opetussuunnitelma;
import fi.vm.sade.eperusteet.ylops.domain.ops.OpetussuunnitelmanJulkaisu;
import fi.vm.sade.eperusteet.ylops.domain.teksti.LokalisoituTeksti;
import fi.vm.sade.eperusteet.ylops.dto.OpetussuunnitelmaExportDto;
import fi.vm.sade.eperusteet.ylops.dto.dokumentti.DokumenttiDto;
import fi.vm.sade.eperusteet.ylops.dto.kayttaja.KayttajanTietoDto;
import fi.vm.sade.eperusteet.ylops.dto.lops2019.Validointi.Lops2019ValidointiDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpetussuunnitelmaJulkaisuKevyt;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpetussuunnitelmanJulkaisuDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.UusiJulkaisuDto;
import fi.vm.sade.eperusteet.ylops.repository.JulkaisuRepositoryCustom;
import fi.vm.sade.eperusteet.ylops.repository.ops.JulkaistuOpetussuunnitelmaDataRepository;
import fi.vm.sade.eperusteet.ylops.repository.ops.JulkaistuOpetussuunnitelmaTilaRepository;
import fi.vm.sade.eperusteet.ylops.repository.ops.JulkaisuRepository;
import fi.vm.sade.eperusteet.ylops.repository.ops.OpetussuunnitelmaRepository;
import fi.vm.sade.eperusteet.ylops.resource.config.InitJacksonConverter;
import fi.vm.sade.eperusteet.ylops.service.dokumentti.DokumenttiService;
import fi.vm.sade.eperusteet.ylops.service.exception.BusinessRuleViolationException;
import fi.vm.sade.eperusteet.ylops.service.exception.DokumenttiException;
import fi.vm.sade.eperusteet.ylops.service.external.KayttajanTietoService;
import fi.vm.sade.eperusteet.ylops.service.mapping.DtoMapper;
import fi.vm.sade.eperusteet.ylops.service.ops.OpetussuunnitelmaService;
import fi.vm.sade.eperusteet.ylops.service.ops.OpetussuunnitelmanMuokkaustietoService;
import fi.vm.sade.eperusteet.ylops.service.ops.OpsDispatcher;
import fi.vm.sade.eperusteet.ylops.service.ops.OpsExport;
import fi.vm.sade.eperusteet.ylops.service.ops.ValidointiService;
import fi.vm.sade.eperusteet.ylops.service.util.JsonMapper;
import fi.vm.sade.eperusteet.ylops.service.util.JulkaisuService;
import fi.vm.sade.eperusteet.ylops.service.util.Validointi;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.json.JSONException;
import org.skyscreamer.jsonassert.JSONCompare;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static fi.vm.sade.eperusteet.ylops.service.util.Nulls.assertExists;
import static java.util.stream.Collectors.toSet;

@Service
@Profile("!test")
@Slf4j
@Transactional
public class JulkaisuServiceImpl implements JulkaisuService {

    @Autowired
    private DtoMapper mapper;

    @Autowired
    private OpetussuunnitelmaRepository opetussuunnitelmaRepository;

    @Autowired
    private JulkaisuRepository julkaisuRepository;

    @Autowired
    private KayttajanTietoService kayttajanTietoService;

    @Autowired
    private JulkaisuRepositoryCustom julkaisuRepositoryCustom;

    @Autowired
    private JulkaistuOpetussuunnitelmaDataRepository julkaistuOpetussuunnitelmaDataRepository;

    @Autowired
    private ValidointiService validointiService;

    @Autowired
    private JsonMapper jsonMapper;

    @Autowired
    private OpetussuunnitelmaService opetussuunnitelmaService;

    @Autowired
    private DokumenttiService dokumenttiService;

    @Autowired
    private OpetussuunnitelmanMuokkaustietoService muokkaustietoService;

    @Autowired
    private JulkaistuOpetussuunnitelmaTilaRepository julkaistuOpetussuunnitelmaTilaRepository;

    @Autowired
    private OpsDispatcher dispatcher;

    @Autowired
    @Lazy
    private JulkaisuService self;

    private static final int JULKAISUN_ODOTUSAIKA_SEKUNNEISSA = 5 * 60;

    private final ObjectMapper objectMapper = InitJacksonConverter.createMapper();

    @Override
    public List<OpetussuunnitelmanJulkaisuDto> getJulkaisut(Long opsId) {
        Opetussuunnitelma ops = opetussuunnitelmaRepository.findOne(opsId);
        assertExists(ops, "Pyydettyä opetussuunnitelmaa ei ole olemassa");
        List<OpetussuunnitelmanJulkaisuDto> julkaisut = mapper.mapAsList(julkaisuRepository.findAllByOpetussuunnitelma(ops), OpetussuunnitelmanJulkaisuDto.class);

        JulkaistuOpetussuunnitelmaTila julkaistuOpetussuunnitelmaTila = julkaistuOpetussuunnitelmaTilaRepository.findOne(opsId);
        if (julkaistuOpetussuunnitelmaTila != null
                && (julkaistuOpetussuunnitelmaTila.getJulkaisutila().equals(JulkaisuTila.KESKEN) || julkaistuOpetussuunnitelmaTila.getJulkaisutila().equals(JulkaisuTila.VIRHE))) {
            julkaisut.add(OpetussuunnitelmanJulkaisuDto.builder()
                    .tila(julkaistuOpetussuunnitelmaTila.getJulkaisutila())
                    .luotu(julkaistuOpetussuunnitelmaTila.getMuokattu())
                    .revision(julkaisut.stream().mapToInt(OpetussuunnitelmanJulkaisuDto::getRevision).max().orElse(0) + 1)
                    .build());
        }

        return taytaKayttajaTiedot(julkaisut);
    }

    @Override
    public List<OpetussuunnitelmanJulkaisuDto> getJulkaisutKevyt(Long opsId) {
        Opetussuunnitelma ops = opetussuunnitelmaRepository.findOne(opsId);
        assertExists(ops, "Pyydettyä opetussuunnitelmaa ei ole olemassa");
        List<OpetussuunnitelmaJulkaisuKevyt> julkaisut = julkaisuRepository.findKevytdataByOpetussuunnitelma(ops);
        return mapper.mapAsList(julkaisut, OpetussuunnitelmanJulkaisuDto.class);
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
    public void addJulkaisu(Long opsId, UusiJulkaisuDto julkaisuDto) {
        Opetussuunnitelma ops = opetussuunnitelmaRepository.findOne(opsId);
        assertExists(ops, "Pyydettyä opetussuunnitelmaa ei ole olemassa");

        if (!Tyyppi.OPS.equals(ops.getTyyppi())) {
            throw new BusinessRuleViolationException("pohjaa-ei-voi-julkaista");
        }

        List<OpetussuunnitelmanJulkaisu> vanhatJulkaisut = julkaisuRepository.findAllByOpetussuunnitelma(ops);
        if(vanhatJulkaisut.size() > 0 && !onkoMuutoksia(opsId)) {
            throw new BusinessRuleViolationException("opetussuunnitelma-ei-muuttunut-viime-julkaisun-jalkeen");
        }

        JulkaistuOpetussuunnitelmaTila julkaistuOpetussuunnitelmaTila = getOrCreateTila(opsId);
        julkaistuOpetussuunnitelmaTila.setJulkaisutila(JulkaisuTila.KESKEN);
        saveJulkaistuOpetussuunnitelmaTila(julkaistuOpetussuunnitelmaTila);

        self.addJulkaisuAsync(opsId, julkaisuDto);
    }

    private JulkaistuOpetussuunnitelmaTila getOrCreateTila(Long opsId) {
        JulkaistuOpetussuunnitelmaTila julkaistuOpetussuunnitelmaTila = julkaistuOpetussuunnitelmaTilaRepository.findOne(opsId);
        if (julkaistuOpetussuunnitelmaTila == null) {
            julkaistuOpetussuunnitelmaTila = new JulkaistuOpetussuunnitelmaTila();
            julkaistuOpetussuunnitelmaTila.setOpsId(opsId);
            julkaistuOpetussuunnitelmaTila.setJulkaisutila(JulkaisuTila.JULKAISEMATON);
        }

        return julkaistuOpetussuunnitelmaTila;
    }

    @Override
    @CacheEvict(value = "ops-julkaisu", key = "#opsId")
    @Async("julkaisuTaskExecutor")
    public void addJulkaisuAsync(Long opsId, UusiJulkaisuDto julkaisuDto) {
        log.debug("teeJulkaisu: {}", opsId);

        JulkaistuOpetussuunnitelmaTila julkaistuOpetussuunnitelmaTila = getOrCreateTila(opsId);

        try {
            Opetussuunnitelma ops = opetussuunnitelmaRepository.findOne(opsId);

            if (KoulutustyyppiToteutus.LOPS2019.equals(ops.getToteutus())) {
                Lops2019ValidointiDto validointi = validointiService.getValidointi(opsId);
                if (!validointi.isValid()) {
                    throw new BusinessRuleViolationException("opetussuunnitelma-ei-validi");
                }
            } else {
                List<Validointi> validoinnit = opetussuunnitelmaService.validoiOpetussuunnitelma(opsId);
                if (validoinnit.stream().anyMatch(validointi -> CollectionUtils.isNotEmpty(validointi.getVirheet()))) {
                    throw new BusinessRuleViolationException("opetussuunnitelma-ei-validi");
                };
            }

            OpetussuunnitelmanJulkaisu julkaisu = new OpetussuunnitelmanJulkaisu();
            julkaisu.setOpetussuunnitelma(ops);
            julkaisu.setTiedote(mapper.map(julkaisuDto.getJulkaisutiedote(), LokalisoituTeksti.class));
            OpetussuunnitelmaExportDto opsData = opetussuunnitelmaService.getExportedOpetussuunnitelma(opsId);

            ObjectNode opsDataJson = (ObjectNode) jsonMapper.toJson(opsData);
            List<OpetussuunnitelmanJulkaisu> vanhatJulkaisut = julkaisuRepository.findAllByOpetussuunnitelma(ops);

            Set<DokumenttiDto> dokumentit = ops.getJulkaisukielet().stream().map(kieli -> {
                DokumenttiDto dokumenttiDto = dokumenttiService.createDtoFor(opsId, kieli);
                try {
                    dokumenttiService.setStarted(dokumenttiDto);
                    dokumenttiService.generateWithDto(dokumenttiDto);
                } catch (DokumenttiException e) {
                    log.error(e.getLocalizedMessage(), e.getCause());
                }
                return dokumenttiDto;
            }).collect(toSet());

            JulkaistuOpetussuunnitelmaData data = new JulkaistuOpetussuunnitelmaData(opsDataJson);
            data = julkaistuOpetussuunnitelmaDataRepository.save(data);
            julkaisu.setDokumentit(dokumentit.stream().map(DokumenttiDto::getId).collect(toSet()));
            julkaisu.setData(data);
            julkaisu.setRevision(vanhatJulkaisut.stream().mapToInt(OpetussuunnitelmanJulkaisu::getRevision).max().orElse(0) + 1);
            julkaisu = julkaisuRepository.saveAndFlush(julkaisu);

            muokkaustietoService.addOpsMuokkausTieto(opsId, ops, MuokkausTapahtuma.JULKAISU);

        } catch (Exception e) {
            log.error(Throwables.getStackTraceAsString(e));
            julkaistuOpetussuunnitelmaTila.setJulkaisutila(JulkaisuTila.VIRHE);
            self.saveJulkaistuOpetussuunnitelmaTila(julkaistuOpetussuunnitelmaTila);
            throw new BusinessRuleViolationException("julkaisun-tallennus-epaonnistui");
        }

        julkaistuOpetussuunnitelmaTila.setJulkaisutila(JulkaisuTila.JULKAISTU);
        saveJulkaistuOpetussuunnitelmaTila(julkaistuOpetussuunnitelmaTila);
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
        return taytaKayttajaTiedot(mapper.map(julkaisu, OpetussuunnitelmanJulkaisuDto.class));
    }

    @Override
    public boolean onkoMuutoksia(long opsId) {
        try {
            Opetussuunnitelma opetussuunnitelma = opetussuunnitelmaRepository.findOne(opsId);
            OpetussuunnitelmanJulkaisu viimeisinJulkaisu = julkaisuRepository.findFirstByOpetussuunnitelmaOrderByRevisionDesc(opetussuunnitelma);

            if (viimeisinJulkaisu == null) {
                return false;
            }

            ObjectNode data = viimeisinJulkaisu.getData().getOpsData();
            String julkaistu = generoiOpetussuunnitelmaKaikkiDtAsString(objectMapper.treeToValue(data, dispatcher.get(opsId, OpsExport.class).getExportClass()));
            String nykyinen = generoiOpetussuunnitelmaKaikkiDtAsString(opetussuunnitelmaService.getExportedOpetussuunnitelma(opsId));

            return JSONCompare.compareJSON(julkaistu, nykyinen, JSONCompareMode.LENIENT).failed();
        } catch (IOException|JSONException e) {
            log.error(Throwables.getStackTraceAsString(e));
            throw new BusinessRuleViolationException("onko-muutoksia-julkaisuun-verrattuna-tarkistus-epaonnistui");
        }
    }

    @Override
    public JulkaisuTila viimeisinJulkaisuTila(long opsId) {
        JulkaistuOpetussuunnitelmaTila julkaistuOpetussuunnitelmaTila = julkaistuOpetussuunnitelmaTilaRepository.findOne(opsId);

        if (julkaistuOpetussuunnitelmaTila != null &&
                julkaistuOpetussuunnitelmaTila.getJulkaisutila().equals(JulkaisuTila.KESKEN)
                && (new Date().getTime() - julkaistuOpetussuunnitelmaTila.getMuokattu().getTime()) / 1000 > JULKAISUN_ODOTUSAIKA_SEKUNNEISSA) {
            log.error("Julkaisu kesti yli {} sekuntia, opsilla {}", JULKAISUN_ODOTUSAIKA_SEKUNNEISSA, opsId);
            julkaistuOpetussuunnitelmaTila.setJulkaisutila(JulkaisuTila.VIRHE);
            saveJulkaistuOpetussuunnitelmaTila(julkaistuOpetussuunnitelmaTila);
        }

        return julkaistuOpetussuunnitelmaTila != null ? julkaistuOpetussuunnitelmaTila.getJulkaisutila() : JulkaisuTila.JULKAISEMATON;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveJulkaistuOpetussuunnitelmaTila(JulkaistuOpetussuunnitelmaTila julkaistuOpetussuunnitelmaTila) {
        julkaistuOpetussuunnitelmaTilaRepository.save(julkaistuOpetussuunnitelmaTila);
    }

    private String generoiOpetussuunnitelmaKaikkiDtAsString(OpetussuunnitelmaExportDto opetussuunnitelmaExportDto) throws IOException {
        opetussuunnitelmaExportDto.setViimeisinJulkaisuAika(null);
        opetussuunnitelmaExportDto.setTila(null);
        return objectMapper.writeValueAsString(opetussuunnitelmaExportDto);
    }

    private OpetussuunnitelmanJulkaisuDto taytaKayttajaTiedot(OpetussuunnitelmanJulkaisuDto julkaisu) {
        return taytaKayttajaTiedot(Arrays.asList(julkaisu)).get(0);
    }

    private List<OpetussuunnitelmanJulkaisuDto> taytaKayttajaTiedot(List<OpetussuunnitelmanJulkaisuDto> julkaisut) {
        Map<String, KayttajanTietoDto> kayttajatiedot = kayttajanTietoService
                .haeKayttajatiedot(julkaisut.stream().map(OpetussuunnitelmanJulkaisuDto::getLuoja).filter(Objects::nonNull).collect(Collectors.toList()))
                .stream().collect(Collectors.toMap(kayttajanTieto -> kayttajanTieto.getOidHenkilo(), kayttajanTieto -> kayttajanTieto));
        julkaisut.forEach(julkaisu -> julkaisu.setKayttajanTieto(kayttajatiedot.get(julkaisu.getLuoja())));
        return julkaisut;
    }
}
