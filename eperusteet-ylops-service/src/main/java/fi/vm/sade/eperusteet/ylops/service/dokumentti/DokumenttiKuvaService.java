package fi.vm.sade.eperusteet.ylops.service.dokumentti;

import fi.vm.sade.eperusteet.ylops.domain.dokumentti.DokumenttiKuva;
import fi.vm.sade.eperusteet.ylops.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.ylops.dto.dokumentti.DokumenttiKuvaDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface DokumenttiKuvaService {
    DokumenttiKuvaDto getDto(Long opsId, Kieli kieli);

    DokumenttiKuvaDto addImage(Long opsId, String tyyppi, Kieli kieli, MultipartFile file) throws IOException;

    DokumenttiKuva createDtoFor(Long id, Kieli kieli);

    byte[] getImage(Long opsId, String tyyppi, Kieli kieli);

    void deleteImage(Long opsId, String tyyppi, Kieli kieli);
}
