package fi.vm.sade.eperusteet.ylops.service.util.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Sets;
import fi.vm.sade.eperusteet.ylops.domain.KoulutustyyppiToteutus;
import fi.vm.sade.eperusteet.ylops.domain.MuokkausTapahtuma;
import fi.vm.sade.eperusteet.ylops.domain.Tyyppi;
import fi.vm.sade.eperusteet.ylops.domain.ops.JulkaistuOpetussuunnitelmaData;
import fi.vm.sade.eperusteet.ylops.domain.ops.Opetussuunnitelma;
import fi.vm.sade.eperusteet.ylops.domain.ops.OpetussuunnitelmanJulkaisu;
import fi.vm.sade.eperusteet.ylops.domain.teksti.Kieli;
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
import fi.vm.sade.eperusteet.ylops.repository.ops.JulkaisuRepository;
import fi.vm.sade.eperusteet.ylops.repository.ops.OpetussuunnitelmaRepository;
import fi.vm.sade.eperusteet.ylops.service.dokumentti.DokumenttiService;
import fi.vm.sade.eperusteet.ylops.service.exception.BusinessRuleViolationException;
import fi.vm.sade.eperusteet.ylops.service.exception.DokumenttiException;
import fi.vm.sade.eperusteet.ylops.service.external.KayttajanTietoService;
import fi.vm.sade.eperusteet.ylops.service.mapping.DtoMapper;
import fi.vm.sade.eperusteet.ylops.service.ops.OpetussuunnitelmaService;
import fi.vm.sade.eperusteet.ylops.service.ops.OpetussuunnitelmanMuokkaustietoService;
import fi.vm.sade.eperusteet.ylops.service.ops.ValidointiService;
import fi.vm.sade.eperusteet.ylops.service.util.JsonMapper;
import fi.vm.sade.eperusteet.ylops.service.util.JulkaisuService;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public JsonNode queryOpetussuunnitelmaJulkaisu(Long opsId, String query) {
        Opetussuunnitelma ops = opetussuunnitelmaRepository.findOne(opsId);
        assertExists(ops, "Pyydettyä opetussuunnitelmaa ei ole olemassa");
        OpetussuunnitelmanJulkaisu julkaisu = julkaisuRepository.findFirstByOpetussuunnitelmaOrderByRevisionDesc(ops);
        assertExists(julkaisu, "Pyydettyä opetussuunnitelmaa ei ole olemassa");
        JsonNode data = julkaisuRepositoryCustom.querySisalto(julkaisu.getId(), query);
        return data;
    }

    @Override
    @CacheEvict(value = "ops-julkaisu", key = "#opsId")
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
        OpetussuunnitelmaExportDto opsData = opetussuunnitelmaService.getExportedOpetussuunnitelma(opsId);

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
                    log.error(e.getLocalizedMessage(), e.getCause());
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
            opetussuunnitelmaService.publicNavigationEvict(opsId, kieli.toString());
            opetussuunnitelmaService.buildNavigationJulkinen(opsId, kieli.toString());
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

    private OpetussuunnitelmanJulkaisuDto taytaKayttajaTiedot(OpetussuunnitelmanJulkaisuDto julkaisu) {
        return taytaKayttajaTiedot(Arrays.asList(julkaisu)).get(0);
    }

    private List<OpetussuunnitelmanJulkaisuDto> taytaKayttajaTiedot(List<OpetussuunnitelmanJulkaisuDto> julkaisut) {
        Map<String, KayttajanTietoDto> kayttajatiedot = kayttajanTietoService
                .haeKayttajatiedot(julkaisut.stream().map(OpetussuunnitelmanJulkaisuDto::getLuoja).collect(Collectors.toList()))
                .stream().collect(Collectors.toMap(kayttajanTieto -> kayttajanTieto.getOidHenkilo(), kayttajanTieto -> kayttajanTieto));
        julkaisut.forEach(julkaisu -> julkaisu.setKayttajanTieto(kayttajatiedot.get(julkaisu.getLuoja())));
        return julkaisut;
    }
}
