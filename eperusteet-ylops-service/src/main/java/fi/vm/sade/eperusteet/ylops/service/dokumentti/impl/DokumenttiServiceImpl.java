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

package fi.vm.sade.eperusteet.ylops.service.dokumentti.impl;

import fi.vm.sade.eperusteet.ylops.domain.Tila;
import fi.vm.sade.eperusteet.ylops.domain.dokumentti.Dokumentti;
import fi.vm.sade.eperusteet.ylops.domain.dokumentti.DokumenttiTila;
import fi.vm.sade.eperusteet.ylops.domain.ops.Opetussuunnitelma;
import fi.vm.sade.eperusteet.ylops.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.ylops.dto.dokumentti.DokumenttiDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpetussuunnitelmaInfoDto;
import fi.vm.sade.eperusteet.ylops.repository.dokumentti.DokumenttiRepository;
import fi.vm.sade.eperusteet.ylops.repository.ops.OpetussuunnitelmaRepository;
import fi.vm.sade.eperusteet.ylops.service.dokumentti.DokumenttiBuilderService;
import fi.vm.sade.eperusteet.ylops.service.dokumentti.DokumenttiService;
import fi.vm.sade.eperusteet.ylops.service.dokumentti.DokumenttiStateService;
import fi.vm.sade.eperusteet.ylops.service.dokumentti.impl.util.DokumenttiUtils;
import fi.vm.sade.eperusteet.ylops.service.exception.DokumenttiException;
import fi.vm.sade.eperusteet.ylops.service.exception.NotExistsException;
import fi.vm.sade.eperusteet.ylops.service.mapping.DtoMapper;
import fi.vm.sade.eperusteet.ylops.service.util.SecurityUtil;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import javax.imageio.ImageIO;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author iSaul
 */
@Service
@Transactional
public class DokumenttiServiceImpl implements DokumenttiService {
    private static final Logger LOG = LoggerFactory.getLogger(DokumenttiServiceImpl.class);

    @Autowired
    private DokumenttiRepository dokumenttiRepository;

    @Autowired
    private DtoMapper mapper;

    @Autowired
    private OpetussuunnitelmaRepository opetussuunnitelmaRepository;

    @Autowired
    private DokumenttiBuilderService builder;

    @Autowired
    private DokumenttiStateService dokumenttiStateService;

    @Lazy
    @Autowired
    private DokumenttiService self;

    @Override
    @Transactional
    public DokumenttiDto getDto(Long opsId, Kieli kieli) {
        Dokumentti dokumentti = getLatestDokumentti(opsId, kieli);

        if (dokumentti != null) {

            // Jos aloitusajasta on kulunut liian kauan, on luonti epäonnistunut
            if (dokumentti.getTila() != DokumenttiTila.VALMIS && dokumentti.getTila() != DokumenttiTila.EI_OLE) {
                if (DokumenttiUtils.isTimePass(dokumentti)) {
                    dokumentti.setTila(DokumenttiTila.EPAONNISTUI);
                    dokumentti = dokumenttiRepository.save(dokumentti);
                }
            }

            return mapper.map(dokumentti, DokumenttiDto.class);
        } else {
            return self.createDtoFor(opsId, kieli);
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public DokumenttiDto createDtoFor(Long id, Kieli kieli) {
        Dokumentti dokumentti = new Dokumentti();
        dokumentti.setTila(DokumenttiTila.EI_OLE);
        dokumentti.setKieli(kieli);

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
    public void autogenerate(Long id, Kieli kieli) throws DokumenttiException {
        Dokumentti dokumentti;
        List<Dokumentti> dokumentit = dokumenttiRepository.findByOpsIdAndKieli(id, kieli);
        if (!dokumentit.isEmpty()) {
            dokumentit.sort(Comparator.comparingLong(Dokumentti::getId));
            dokumentti = dokumentit.get(0);
        } else {
            dokumentti = new Dokumentti();
        }

        dokumentti.setTila(DokumenttiTila.LUODAAN);
        dokumentti.setAloitusaika(new Date());
        dokumentti.setLuoja(SecurityUtil.getAuthenticatedPrincipal().getName());
        dokumentti.setKieli(kieli);
        dokumentti.setOpsId(id);

        Opetussuunnitelma ops = opetussuunnitelmaRepository.findOne(id);
        if (ops != null) {
            try {
                dokumentti.setData(builder.generatePdf(ops, dokumentti, kieli));
                dokumentti.setTila(DokumenttiTila.VALMIS);
                dokumentti.setValmistumisaika(new Date());
                dokumentti.setVirhekoodi("");
                dokumenttiRepository.save(dokumentti);
            } catch (Exception ex) {
                dokumentti.setTila(DokumenttiTila.EPAONNISTUI);
                dokumentti.setVirhekoodi(ExceptionUtils.getStackTrace(ex));
                dokumenttiRepository.save(dokumentti);

                throw new DokumenttiException(ex.getMessage(), ex);
            }
        } else {
            dokumentti.setTila(DokumenttiTila.EPAONNISTUI);
            dokumenttiRepository.save(dokumentti);
        }
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
    @Transactional(noRollbackFor = DokumenttiException.class)
    @Async(value = "docTaskExecutor")
    public void generateWithDto(DokumenttiDto dto) throws DokumenttiException {
        dto.setTila(DokumenttiTila.LUODAAN);
        Dokumentti dokumentti = dokumenttiStateService.save(dto);

        try {
            Opetussuunnitelma ops = opetussuunnitelmaRepository.findOne(dokumentti.getOpsId());
            if (ops != null) {
                dokumentti.setData(builder.generatePdf(ops, dokumentti, dokumentti.getKieli()));
            }
            dokumentti.setTila(DokumenttiTila.VALMIS);
            dokumentti.setValmistumisaika(new Date());
            dokumentti.setVirhekoodi(null);

            dokumenttiRepository.save(dokumentti);
        } catch (Exception ex) {
            dto.setTila(DokumenttiTila.EPAONNISTUI);
            dto.setVirhekoodi(ex.getLocalizedMessage());

            dokumenttiStateService.save(dto);

            throw new DokumenttiException(ex.getMessage(), ex);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public DokumenttiDto getDto(Long id) {
        Dokumentti dokumentti = dokumenttiRepository.findOne(id);
        return mapper.map(dokumentti, DokumenttiDto.class);
    }

    @Override
    public DokumenttiDto addImage(Long opsId, DokumenttiDto dto, String tyyppi, Kieli kieli, MultipartFile file) throws IOException {

        if (!file.isEmpty()) {

            BufferedImage bufferedImage = ImageIO.read(file.getInputStream());

            int width = bufferedImage.getWidth();
            int height = bufferedImage.getHeight();

            // Muutetaan kaikkien kuvien väriavaruus RGB:ksi jotta PDF/A validointi menee läpi
            // Asetetaan lisäksi läpinäkyvien kuvien taustaksi valkoinen väri
            BufferedImage tempImage = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(),
                    BufferedImage.TYPE_3BYTE_BGR);
            tempImage.getGraphics().setColor(new Color(255, 255, 255, 0));
            tempImage.getGraphics().fillRect(0, 0, width, height);
            tempImage.getGraphics().drawImage(bufferedImage, 0, 0, null);

            bufferedImage = tempImage;

            // Muutetaan kuva PNG:ksi
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "jpg", baos);
            baos.flush();
            byte[] image = baos.toByteArray();
            baos.close();

            // Haetaan domain dokumentti
            Dokumentti dokumentti = getLatestDokumentti(dto.getOpsId(), kieli);

            if (dokumentti == null) {
                return null;
            }

            switch (tyyppi) {
                case "kansikuva":
                    dokumentti.setKansikuva(image);
                    break;
                case "ylatunniste":
                    dokumentti.setYlatunniste(image);
                    break;
                case "alatunniste":
                    dokumentti.setAlatunniste(image);
                    break;
                default:
                    mapper.map(dokumentti, DokumenttiDto.class);
            }

            return mapper.map(dokumenttiRepository.save(dokumentti), DokumenttiDto.class);
        }

        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] getImage(Long opsId, String tyyppi, Kieli kieli) {

        // Haetaan kuva
        List<Dokumentti> dokumentit = dokumenttiRepository.findByOpsIdAndKieli(opsId, kieli);
        if (dokumentit.isEmpty()) {
            throw new NotExistsException("Dokumentti ei löytynyt");
        }

        Dokumentti dokumentti = dokumentit.get(0);

        byte[] image;

        switch (tyyppi) {
            case "kansikuva":
                image = dokumentti.getKansikuva();
                break;
            case "ylatunniste":
                image = dokumentti.getYlatunniste();
                break;
            case "alatunniste":
                image = dokumentti.getAlatunniste();
                break;
            default:
                throw new IllegalArgumentException(tyyppi + " ei ole kelvollinen tyyppi");
        }

        return image;
    }

    @Override
    public void deleteImage(Long opsId, String tyyppi, Kieli kieli) {

        // Tehdään DokumenttiDto jos ei löydy jo valmiina
        DokumenttiDto dokumenttiDto = getDto(opsId, kieli);

        if (dokumenttiDto == null) {
            return;
        }

        // Haetaan kuva
        List<Dokumentti> dokumentit = dokumenttiRepository.findByOpsIdAndKieli(opsId, kieli);
        Dokumentti dokumentti = dokumentit.isEmpty() ? null : dokumentit.get(0);
        if (dokumentti == null) {
            return;
        }

        switch (tyyppi) {
            case "kansikuva":
                dokumentti.setKansikuva(null);
                break;
            case "ylatunniste":
                dokumentti.setYlatunniste(null);
                break;
            case "alatunniste":
                dokumentti.setAlatunniste(null);
                break;
            default:
                return;
        }

        dokumenttiRepository.save(dokumentti);
    }

    private Dokumentti getLatestDokumentti(Long opsId, Kieli kieli) {
        List<Dokumentti> dokumentit = dokumenttiRepository.findByOpsIdAndKieli(opsId, kieli);
        if (dokumentit.isEmpty()) {
            return null;
        } else {
            return dokumentit.get(0);
        }
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
    @Transactional(readOnly = true)
    public Long getDokumenttiId(Long opsId, Kieli kieli) {
        Sort sort = new Sort(Sort.Direction.DESC, "valmistumisaika");
        List<Dokumentti> documents = dokumenttiRepository
                .findByOpsIdAndKieliAndTila(opsId, kieli, DokumenttiTila.VALMIS, sort);

        if (!documents.isEmpty()) {
            return documents.get(0).getId();
        } else {
            return null;
        }
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
        return mapper.map(dokumentti, DokumenttiDto.class);
    }

    @Override
    @Transactional(readOnly = true)
    public DokumenttiTila getTila(Long opsId, Kieli kieli) {
        DokumenttiDto dokumentti = getDto(opsId, kieli);
        if (dokumentti != null) {
            return dokumentti.getTila();
        }

        return null;
    }
}
