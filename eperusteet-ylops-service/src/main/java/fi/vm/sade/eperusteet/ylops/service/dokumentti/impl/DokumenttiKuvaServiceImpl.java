package fi.vm.sade.eperusteet.ylops.service.dokumentti.impl;

import fi.vm.sade.eperusteet.ylops.domain.dokumentti.DokumenttiKuva;
import fi.vm.sade.eperusteet.ylops.domain.ops.Opetussuunnitelma;
import fi.vm.sade.eperusteet.ylops.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.ylops.dto.dokumentti.DokumenttiKuvaDto;
import fi.vm.sade.eperusteet.ylops.repository.dokumentti.DokumenttiKuvaRepository;
import fi.vm.sade.eperusteet.ylops.repository.ops.OpetussuunnitelmaRepository;
import fi.vm.sade.eperusteet.ylops.service.dokumentti.DokumenttiKuvaService;
import fi.vm.sade.eperusteet.ylops.service.exception.BusinessRuleViolationException;
import fi.vm.sade.eperusteet.ylops.service.mapping.DtoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
@Transactional
public class DokumenttiKuvaServiceImpl implements DokumenttiKuvaService {

    @Autowired
    private DtoMapper mapper;

    @Autowired
    private OpetussuunnitelmaRepository opetussuunnitelmaRepository;

    @Autowired
    private DokumenttiKuvaRepository dokumenttiKuvaRepository;


    @Override
    @Transactional(readOnly = true)
    public DokumenttiKuvaDto getDto(Long opsId, Kieli kieli) {
        return mapper.map(dokumenttiKuvaRepository.findFirstByOpsIdAndKieli(opsId, kieli), DokumenttiKuvaDto.class);
    }

    @Override
    public DokumenttiKuvaDto addImage(Long opsId, String tyyppi, Kieli kieli, MultipartFile file) throws IOException {

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

            DokumenttiKuva dokumenttiKuva = dokumenttiKuvaRepository.findFirstByOpsIdAndKieli(opsId, kieli);

            if (dokumenttiKuva == null) {
                dokumenttiKuva = createDtoFor(opsId, kieli);
            }

            switch (tyyppi) {
                case "kansikuva":
                    dokumenttiKuva.setKansikuva(image);
                    break;
                case "ylatunniste":
                    dokumenttiKuva.setYlatunniste(image);
                    break;
                case "alatunniste":
                    dokumenttiKuva.setAlatunniste(image);
                    break;
                default:
                    mapper.map(dokumenttiKuva, DokumenttiKuvaDto.class);
            }
            return mapper.map(dokumenttiKuvaRepository.save(dokumenttiKuva), DokumenttiKuvaDto.class);
        }
        return null;
    }

    @Override
    @Transactional
    public DokumenttiKuva createDtoFor(Long id, Kieli kieli) {

        DokumenttiKuva dokumenttiKuva = new DokumenttiKuva();
        dokumenttiKuva.setKieli(kieli);

        Opetussuunnitelma ops = opetussuunnitelmaRepository.findOne(id);
        if (ops != null) {
            dokumenttiKuva.setOpsId(id);
            return dokumenttiKuvaRepository.save(dokumenttiKuva);
        }
        else {
            throw new BusinessRuleViolationException("opetussuunnitelmaa-ei-loytynyt");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] getImage(Long opsId, String tyyppi, Kieli kieli) {

        DokumenttiKuva dokumenttiKuva = dokumenttiKuvaRepository.findFirstByOpsIdAndKieli(opsId, kieli);

        if (dokumenttiKuva == null) {
            return null;
        }

        byte[] image;

        switch (tyyppi) {
            case "kansikuva":
                image = dokumenttiKuva.getKansikuva();
                break;
            case "ylatunniste":
                image = dokumenttiKuva.getYlatunniste();
                break;
            case "alatunniste":
                image = dokumenttiKuva.getAlatunniste();
                break;
            default:
                throw new IllegalArgumentException(tyyppi + " ei ole kelvollinen tyyppi");
        }
        return image;
    }

    @Override
    public void deleteImage(Long opsId, String tyyppi, Kieli kieli) {
        DokumenttiKuva dokumenttiKuva = dokumenttiKuvaRepository.findFirstByOpsIdAndKieli(opsId, kieli);

        if (dokumenttiKuva == null) {
            return;
        }

        switch (tyyppi) {
            case "kansikuva":
                dokumenttiKuva.setKansikuva(null);
                break;
            case "ylatunniste":
                dokumenttiKuva.setYlatunniste(null);
                break;
            case "alatunniste":
                dokumenttiKuva.setAlatunniste(null);
                break;
            default:
                return;
        }
        dokumenttiKuvaRepository.save(dokumenttiKuva);
    }
}
