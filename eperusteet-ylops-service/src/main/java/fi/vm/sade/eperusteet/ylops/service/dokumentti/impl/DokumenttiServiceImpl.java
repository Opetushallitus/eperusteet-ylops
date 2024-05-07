package fi.vm.sade.eperusteet.ylops.service.dokumentti.impl;

import fi.vm.sade.eperusteet.ylops.domain.Tila;
import fi.vm.sade.eperusteet.ylops.domain.dokumentti.Dokumentti;
import fi.vm.sade.eperusteet.ylops.domain.dokumentti.DokumenttiTila;
import fi.vm.sade.eperusteet.ylops.domain.ops.Opetussuunnitelma;
import fi.vm.sade.eperusteet.ylops.domain.ops.OpetussuunnitelmanJulkaisu;
import fi.vm.sade.eperusteet.ylops.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.ylops.dto.dokumentti.DokumenttiDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpetussuunnitelmaInfoDto;
import fi.vm.sade.eperusteet.ylops.repository.dokumentti.DokumenttiRepository;
import fi.vm.sade.eperusteet.ylops.repository.ops.JulkaisuRepository;
import fi.vm.sade.eperusteet.ylops.repository.ops.OpetussuunnitelmaRepository;
import fi.vm.sade.eperusteet.ylops.service.dokumentti.DokumenttiService;
import fi.vm.sade.eperusteet.ylops.service.dokumentti.DokumenttiStateService;
import fi.vm.sade.eperusteet.ylops.service.dokumentti.ExternalPdfService;
import fi.vm.sade.eperusteet.ylops.service.exception.DokumenttiException;
import fi.vm.sade.eperusteet.ylops.service.external.EperusteetService;
import fi.vm.sade.eperusteet.ylops.service.mapping.DtoMapper;
import fi.vm.sade.eperusteet.ylops.service.util.SecurityUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Slf4j
@Service
@Transactional
public class DokumenttiServiceImpl implements DokumenttiService {
    @Autowired
    private DokumenttiRepository dokumenttiRepository;

    @Autowired
    private DtoMapper mapper;

    @Autowired
    private OpetussuunnitelmaRepository opetussuunnitelmaRepository;

    @Autowired
    private DokumenttiStateService dokumenttiStateService;

    @Autowired
    private JulkaisuRepository julkaisuRepository;

    @Autowired
    private EperusteetService eperusteetService;

    @Autowired
    private ExternalPdfService externalPdfService;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public DokumenttiDto createDtoFor(Long id, Kieli kieli) {
        Dokumentti dokumentti = new Dokumentti();
        dokumentti.setTila(DokumenttiTila.EI_OLE);
        dokumentti.setKieli(kieli);
        dokumentti.setAloitusaika(new Date());
        dokumentti.setLuoja(SecurityUtil.getAuthenticatedPrincipal().getName());

        Opetussuunnitelma ops = opetussuunnitelmaRepository.findOne(id);
        if (ops != null) {
            dokumentti.setOpsId(id);
            Dokumentti saved = dokumenttiRepository.save(dokumentti);
            return mapper.map(saved, DokumenttiDto.class);
        }

        return null;
    }

    @Override
    @Transactional(noRollbackFor = DokumenttiException.class)
    @Async(value = "docTaskExecutor")
    public void generateWithDto(DokumenttiDto dto) throws DokumenttiException {
        dto.setTila(DokumenttiTila.LUODAAN);

        try {
            externalPdfService.generatePdf(dto);
        } catch (Exception ex) {
            dto.setTila(DokumenttiTila.EPAONNISTUI);
            dto.setVirhekoodi(ex.getLocalizedMessage());
            dokumenttiStateService.save(dto);
            throw new DokumenttiException(ex.getMessage(), ex);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Long getLatestValmisDokumenttiId(Long opsId, Kieli kieli) {
        Sort sort = Sort.by(Sort.Direction.DESC, "valmistumisaika");
        List<Dokumentti> dokumentit = dokumenttiRepository.findByOpsIdAndKieliAndTila(opsId, kieli, DokumenttiTila.VALMIS, sort);

        if (!dokumentit.isEmpty()) {
            return dokumentit.get(0).getId();
        }
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public DokumenttiDto getLatestDokumentti(Long opsId, Kieli kieli) {
        Dokumentti dokumentti = dokumenttiRepository.findFirstByOpsIdAndKieliOrderByAloitusaikaDesc(opsId, kieli);

        if (dokumentti != null) {
            DokumenttiDto dokumenttiDto = mapper.map(dokumentti, DokumenttiDto.class);
            DokumenttiDto julkaisuDokumentti = getJulkaistuDokumentti(opsId, kieli, null);
            if (julkaisuDokumentti != null && dokumenttiDto.getId().equals(julkaisuDokumentti.getId())) {
                dokumenttiDto.setJulkaisuDokumentti(true);
            }
            return dokumenttiDto;

        } else {
            DokumenttiDto dto = new DokumenttiDto();
            dto.setOpsId(opsId);
            dto.setKieli(kieli);
            dto.setTila(DokumenttiTila.EI_OLE);
            return dto;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public DokumenttiDto getJulkaistuDokumentti(Long opsId, Kieli kieli, Integer revision) {
        Opetussuunnitelma ops = opetussuunnitelmaRepository.findOne(opsId);

        if (ops == null) {
            return null;
        }

        OpetussuunnitelmanJulkaisu julkaisu;
        if (revision != null) {
            julkaisu = julkaisuRepository.findByOpetussuunnitelmaAndRevision(ops, revision);
        } else {
            julkaisu = julkaisuRepository.findFirstByOpetussuunnitelmaOrderByRevisionDesc(ops);
        }

        if (julkaisu != null && CollectionUtils.isNotEmpty(julkaisu.getDokumentit())) {
            Dokumentti dokumentti = dokumenttiRepository.findByIdInAndKieli(julkaisu.getDokumentit(), kieli);
            if (dokumentti != null) {
                DokumenttiDto dokumenttiDto = mapper.map(dokumentti, DokumenttiDto.class);
                dokumenttiDto.setJulkaisuDokumentti(true);
                return dokumenttiDto;
            }
        }

        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasPermission(Long id) {
        Dokumentti dokumentti = dokumenttiRepository.findOne(id);
        if (dokumentti == null) {
            return false;
        }

        OpetussuunnitelmaInfoDto ops = mapper.map(opetussuunnitelmaRepository.findOne(dokumentti.getOpsId()), OpetussuunnitelmaInfoDto.class);
        String name = SecurityUtil.getAuthenticatedPrincipal().getName();

        return ops.getTila().equals(Tila.JULKAISTU) || ops.isEsikatseltavissa() || !name.equals("anonymousUser");
    }

    @Override
    @Transactional(readOnly = true)
    public DokumenttiDto query(Long id) {
        Dokumentti dokumentti = dokumenttiRepository.findOne(id);
        DokumenttiDto dto = mapper.map(dokumentti, DokumenttiDto.class);
        if (dokumentti != null && DokumenttiUtils.isTimePass(dokumentti)) {
            log.error("dokumentin valmistus kesti yli {} minuuttia, opetussuunnitelma {}", DokumenttiUtils.MAX_TIME_IN_MINUTES, dto.getOpsId());
            dto.setTila(DokumenttiTila.EPAONNISTUI);
            dto.setVirhekoodi("tuntematon");
            dto.setValmistumisaika(new Date());
            dokumenttiStateService.save(dto);
        }
        return dto;
    }

    @Override
    @Transactional
    public void setStarted(DokumenttiDto dto) {
        dto.setAloitusaika(new Date());
        dto.setLuoja(SecurityUtil.getAuthenticatedPrincipal().getName());
        dto.setTila(DokumenttiTila.JONOSSA);
        dokumenttiStateService.save(dto);
    }

    @Override
    @Transactional(readOnly = true)
    public DokumenttiDto getDto(Long id) {
        Dokumentti dokumentti = dokumenttiRepository.findOne(id);
        return mapper.map(dokumentti, DokumenttiDto.class);
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] get(Long id) {
        Dokumentti dokumentti = dokumenttiRepository.findOne(id);
        if (dokumentti == null) {
            return null;
        }
        return dokumentti.getData();
    }

    @Override
    public void updateDokumenttiPdfData(byte[] pdfData, Long dokumenttiId) {
        Dokumentti dokumentti = dokumenttiRepository.findById(dokumenttiId).orElseThrow();
        dokumentti.setData(pdfData);
        dokumentti.setVirhekoodi(null);
        dokumentti.setTila(DokumenttiTila.VALMIS);
        dokumentti.setValmistumisaika(new Date());
        dokumenttiRepository.save(dokumentti);
    }

    @Override
    public void updateDokumenttiTila(DokumenttiTila tila, Long dokumenttiId) {
        Dokumentti dokumentti = dokumenttiRepository.findById(dokumenttiId).orElseThrow();
        dokumentti.setTila(tila);
        dokumenttiRepository.save(dokumentti);
    }
}
