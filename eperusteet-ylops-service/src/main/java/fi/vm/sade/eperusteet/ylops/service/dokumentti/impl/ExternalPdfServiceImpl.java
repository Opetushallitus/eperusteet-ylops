package fi.vm.sade.eperusteet.ylops.service.dokumentti.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fi.vm.sade.eperusteet.utils.client.RestClientFactory;
import fi.vm.sade.eperusteet.ylops.dto.OpetussuunnitelmaExportDto;
import fi.vm.sade.eperusteet.ylops.dto.dokumentti.DokumenttiDto;
import fi.vm.sade.eperusteet.ylops.repository.ops.JulkaisuRepository;
import fi.vm.sade.eperusteet.ylops.resource.config.InitJacksonConverter;
import fi.vm.sade.eperusteet.ylops.service.dokumentti.DokumenttiService;
import fi.vm.sade.eperusteet.ylops.service.dokumentti.ExternalPdfService;
import fi.vm.sade.eperusteet.ylops.service.ops.OpetussuunnitelmaService;
import fi.vm.sade.javautils.http.OphHttpClient;
import fi.vm.sade.javautils.http.OphHttpEntity;
import fi.vm.sade.javautils.http.OphHttpRequest;
import org.apache.http.entity.ContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.Date;
import java.util.Optional;

import static javax.servlet.http.HttpServletResponse.SC_ACCEPTED;
import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static javax.servlet.http.HttpServletResponse.SC_FORBIDDEN;
import static javax.servlet.http.HttpServletResponse.SC_FOUND;
import static javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
import static javax.servlet.http.HttpServletResponse.SC_METHOD_NOT_ALLOWED;
import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;
import static javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;

@Service
public class ExternalPdfServiceImpl implements ExternalPdfService {

    @Value("${fi.vm.sade.eperusteet.ylops.pdf-service:''}")
    private String pdfServiceUrl;

    @Autowired
    private OpetussuunnitelmaService opetussuunnitelmaService;

    @Autowired
    private JulkaisuRepository julkaisuRepository;

    @Autowired
    RestClientFactory restClientFactory;

    @Lazy
    @Autowired
    private DokumenttiService dokumenttiService;

    private final ObjectMapper mapper = InitJacksonConverter.createMapper();

    @Override
    public void generatePdf(DokumenttiDto dto) throws JsonProcessingException {

        OpetussuunnitelmaExportDto ops = null;
        DokumenttiDto viimeisinJulkaistuDokumentti = dokumenttiService.getJulkaistuDokumentti(dto.getOpsId(), dto.getKieli(), null);
        if (viimeisinJulkaistuDokumentti != null && viimeisinJulkaistuDokumentti.getId().equals(dto.getId())) {
            ops = opetussuunnitelmaService.getOpetussuunnitelmaJulkaistuSisalto(dto.getOpsId());
        } else {
            ops = opetussuunnitelmaService.getExportedOpetussuunnitelma(dto.getOpsId());
        }

        if (!dto.getJulkaisuDokumentti()) {
            ops.setViimeisinJulkaisuAika(null);
        } else if (ops.getViimeisinJulkaisuAika() == null) {
            ops.setViimeisinJulkaisuAika(new Date());
        }

        String json = mapper.writeValueAsString(ops);
        OphHttpClient client = restClientFactory.get(pdfServiceUrl, true);
        String url = pdfServiceUrl + "/api/pdf/generate/ylops/" + dto.getId() + "/" + dto.getKieli().name();

        String result = (String) client.execute(
                        OphHttpRequest.Builder
                                .post(url)
                                .addHeader("Content-Type", "application/json;charset=UTF-8")
                                .setEntity(new OphHttpEntity.Builder()
                                        .content(json)
                                        .contentType(ContentType.APPLICATION_JSON)
                                        .build())
                                .build())
                .handleErrorStatus(SC_FOUND, SC_UNAUTHORIZED, SC_FORBIDDEN, SC_METHOD_NOT_ALLOWED, SC_BAD_REQUEST, SC_INTERNAL_SERVER_ERROR, SC_NOT_FOUND)
                .with(res -> Optional.of("error"))
                .expectedStatus(SC_ACCEPTED)
                .mapWith(res -> res)
                .orElse(null);

        if (!ObjectUtils.isEmpty(result)) {
            throw new RuntimeException("Virhe pdf-palvelun kutsussa");
        }
    }
}