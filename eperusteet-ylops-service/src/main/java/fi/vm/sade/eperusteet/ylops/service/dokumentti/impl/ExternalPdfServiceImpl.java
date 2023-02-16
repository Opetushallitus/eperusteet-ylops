package fi.vm.sade.eperusteet.ylops.service.dokumentti.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fi.vm.sade.eperusteet.ylops.dto.OpetussuunnitelmaExportDto;
import fi.vm.sade.eperusteet.ylops.dto.dokumentti.DokumenttiDto;
import fi.vm.sade.eperusteet.ylops.resource.config.InitJacksonConverter;
import fi.vm.sade.eperusteet.ylops.service.dokumentti.ExternalPdfService;
import fi.vm.sade.eperusteet.ylops.service.ops.OpetussuunnitelmaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class ExternalPdfServiceImpl implements ExternalPdfService {

    @Value("${fi.vm.sade.eperusteet.eperusteet.pdf-service:''}")
    private String pdfServiceUrl;

    @Autowired
    private OpetussuunnitelmaService opetussuunnitelmaService;

    @Autowired
    HttpEntity httpEntity;

    private final ObjectMapper mapper = InitJacksonConverter.createMapper();

    @Override
    public void generatePdf(DokumenttiDto dto) throws JsonProcessingException {
        RestTemplate restTemplate = new RestTemplate();
        OpetussuunnitelmaExportDto ops = opetussuunnitelmaService.getOpetussuunnitelmaJulkaistuSisalto(dto.getOpsId());
        String json = mapper.writeValueAsString(ops);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(json, headers);

        String url = pdfServiceUrl + "/api/pdf/generate/ylops/" + dto.getId() + "/" + dto.getKieli().name();
        restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
    }
}
