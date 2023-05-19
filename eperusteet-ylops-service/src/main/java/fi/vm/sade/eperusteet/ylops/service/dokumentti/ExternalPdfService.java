package fi.vm.sade.eperusteet.ylops.service.dokumentti;

import com.fasterxml.jackson.core.JsonProcessingException;
import fi.vm.sade.eperusteet.ylops.dto.dokumentti.DokumenttiDto;

public interface ExternalPdfService {
    void generatePdf(DokumenttiDto dto) throws JsonProcessingException;
}