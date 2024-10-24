package fi.vm.sade.eperusteet.ylops.service.util.impl;

import com.fasterxml.jackson.databind.node.ObjectNode;
import fi.vm.sade.eperusteet.ylops.domain.KoulutusTyyppi;
import fi.vm.sade.eperusteet.ylops.domain.MuokkausTapahtuma;
import fi.vm.sade.eperusteet.ylops.domain.Tyyppi;
import fi.vm.sade.eperusteet.ylops.domain.ops.JulkaistuOpetussuunnitelmaData;
import fi.vm.sade.eperusteet.ylops.domain.ops.Opetussuunnitelma;
import fi.vm.sade.eperusteet.ylops.domain.ops.OpetussuunnitelmanJulkaisu;
import fi.vm.sade.eperusteet.ylops.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.ylops.domain.teksti.LokalisoituTeksti;
import fi.vm.sade.eperusteet.ylops.dto.OpetussuunnitelmaExportDto;
import fi.vm.sade.eperusteet.ylops.dto.util.CacheArvot;
import fi.vm.sade.eperusteet.ylops.repository.ops.JulkaisuRepository;
import fi.vm.sade.eperusteet.ylops.repository.ops.OpetussuunnitelmaRepository;
import fi.vm.sade.eperusteet.ylops.service.ops.OpetussuunnitelmaService;
import fi.vm.sade.eperusteet.ylops.service.ops.OpetussuunnitelmanMuokkaustietoService;
import fi.vm.sade.eperusteet.ylops.service.util.JsonMapper;
import fi.vm.sade.eperusteet.ylops.service.util.MaintenanceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class MaintenanceServiceImpl implements MaintenanceService {

    @Autowired
    private OpetussuunnitelmaRepository opetussuunnitelmaRepository;

    @Autowired
    private PlatformTransactionManager ptm;

    @Autowired
    private JulkaisuRepository julkaisuRepository;

    @Autowired
    @Lazy
    private OpetussuunnitelmaService opetussuunnitelmaService;

    @Autowired
    private OpetussuunnitelmanMuokkaustietoService opetussuunnitelmanMuokkaustietoService;

    @Autowired
    private JsonMapper jsonMapper;

    @Autowired
    private CacheManager cacheManager;

    @Override
    public void clearCache(String cache) {
        Objects.requireNonNull(cacheManager.getCache(cache)).clear();
    }

    @Override
    @Async
    @Transactional(propagation = Propagation.NEVER)
    public void teeJulkaisut(boolean julkaiseKaikki, Set<KoulutusTyyppi> koulutustyypit) {
        List<Opetussuunnitelma> opetussuunnitelmat;
        if (koulutustyypit != null) {
            opetussuunnitelmat = opetussuunnitelmaRepository.findJulkaistutByTyyppi(Tyyppi.OPS, koulutustyypit);
        } else {
            opetussuunnitelmat = opetussuunnitelmaRepository.findJulkaistutByTyyppi(Tyyppi.OPS);
        }

        List<Long> ids = opetussuunnitelmat.stream()
                .filter(peruste -> julkaiseKaikki || CollectionUtils.isEmpty(peruste.getJulkaisut()))
                .map(Opetussuunnitelma::getId)
                .collect(Collectors.toList());

        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        for (Long opsId : ids) {
            try {
                teeJulkaisu(username, opsId);
            } catch (RuntimeException ex) {
                log.error(ex.getLocalizedMessage(), ex);
            }
        }

        log.info("julkaisut tehty");
    }

    private void teeJulkaisu(String username, Long opsId) {
        TransactionTemplate template = new TransactionTemplate(ptm);
        template.execute(status -> {
            try {
                Opetussuunnitelma opetussuunnitelma = opetussuunnitelmaRepository.findOne(opsId);
                log.info("Luodaan julkaisu opetussuunnitelmalle: " + opetussuunnitelma.getId());
                OpetussuunnitelmanJulkaisu viimeisinJulkaisu = julkaisuRepository.findFirstByOpetussuunnitelmaOrderByRevisionDesc(opetussuunnitelma);

                OpetussuunnitelmaExportDto opsData = opetussuunnitelmaService.getExportedOpetussuunnitelma(opsId);
                OpetussuunnitelmanJulkaisu julkaisu = new OpetussuunnitelmanJulkaisu();
                julkaisu.setRevision(viimeisinJulkaisu != null ? viimeisinJulkaisu.getRevision() + 1 : 1);
                julkaisu.setLuoja("maintenance");
                julkaisu.setTiedote(LokalisoituTeksti.of(Kieli.FI, "Ylläpidon suorittama julkaisu"));
                julkaisu.setLuotu(new Date());
                julkaisu.setOpetussuunnitelma(opetussuunnitelma);

                ObjectNode dataJson = (ObjectNode) jsonMapper.toJson(opsData);
                julkaisu.setData(new JulkaistuOpetussuunnitelmaData(dataJson));
                julkaisuRepository.save(julkaisu);

                opetussuunnitelmanMuokkaustietoService.addOpsMuokkausTieto(opsId, opetussuunnitelma, MuokkausTapahtuma.JULKAISU);
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        });
    }

    @Override
    public void clearOpetussuunnitelmaCaches(Long opetussuunnitelmaId) {
        opetussuunnitelmaRepository.findOne(opetussuunnitelmaId).getJulkaisukielet().forEach(kieli -> {
            cacheManager.getCache(CacheArvot.OPETUSSUUNNITELMA_NAVIGAATIO_JULKINEN).evictIfPresent(opetussuunnitelmaId + kieli.toString());
        });
        cacheManager.getCache(CacheArvot.OPETUSSUUNNITELMA_JULKAISU).evictIfPresent(opetussuunnitelmaId);
    }

    @Override
    public void cacheOpetussuunnitelmaNavigaatiot() {
        opetussuunnitelmaService.getKaikkiJulkaistutOpetussuunnitelmat().forEach(ops -> {
            try {
                ops.getJulkaisukielet().forEach(kieli -> opetussuunnitelmaService.buildNavigationPublic(ops.getId(), kieli.toString(), null));
            } catch (Exception e) {
                log.error("Error caching navigation for opetussuunnitelma {}", ops.getId(), e);
            }
        });
    }

    @Override
    public void cacheJulkaistutOpetussuunnitelmat() {
        opetussuunnitelmaService.getKaikkiJulkaistutOpetussuunnitelmat().forEach(ops -> {
            try {
                opetussuunnitelmaService.getOpetussuunnitelmaJulkaistuSisalto(ops.getId(), null);
            } catch(Exception e) {
                log.error("Error caching julkaistu sisalto for opetussuunnitelma {}", ops.getId(), e);
            }
        });
    }


}
