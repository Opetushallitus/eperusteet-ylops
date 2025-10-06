package fi.vm.sade.eperusteet.ylops.resource.julkinen;

import com.fasterxml.jackson.databind.JsonNode;
import fi.vm.sade.eperusteet.ylops.dto.OpetussuunnitelmaExportDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpetussuunnitelmaJulkaistuQuery;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpetussuunnitelmaJulkinenDto;
import fi.vm.sade.eperusteet.ylops.service.ops.OpetussuunnitelmaService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Description;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@CrossOrigin
@RestController
@RequestMapping(value = "/api/external", produces = "application/json;charset=UTF-8")
@Tag(name = "Julkinen")
@Description("Opetussuunnitelminen julkinen rajapinta")
public class ExternalController {

    @Autowired
    private OpetussuunnitelmaService opetussuunnitelmaService;

    private static final int DEFAULT_PATH_SKIP_VALUE = 5;

    @Operation(summary = "Opetussuunnitelmien haku")
    @RequestMapping(method = RequestMethod.GET, value = "/opetussuunnitelmat")
    @Parameters({
            @Parameter(name = "nimi", schema = @Schema(implementation = String.class), in = ParameterIn.QUERY, description = "Perusteen nimi"),
            @Parameter(name = "kieli", in = ParameterIn.QUERY, array = @ArraySchema(schema = @Schema(type = "string"))),
            @Parameter(name = "perusteenDiaarinumero", schema = @Schema(implementation = String.class), in = ParameterIn.QUERY, description = "Perusteen diaarinumero"),
            @Parameter(name = "koulutustyypit", in = ParameterIn.QUERY, array = @ArraySchema(schema = @Schema(type = "string")), description = "Perusteen koulutustyypit"),
            @Parameter(name = "organisaatio", schema = @Schema(implementation = String.class), in = ParameterIn.QUERY, description = "Organisaation oid"),
            @Parameter(name = "sivu", schema = @Schema(implementation = Integer.class), in = ParameterIn.QUERY),
            @Parameter(name = "sivukoko", schema = @Schema(implementation = Integer.class), in = ParameterIn.QUERY),
            @Parameter(name = "julkaistuJalkeen", schema = @Schema(type = "string", format = "date"), in = ParameterIn.QUERY,
                    description = "Alkupäivä (ISO 8601, esim. 2025-10-03)"),
            @Parameter(name = "julkaistuEnnen", schema = @Schema(type = "string", format = "date"), in = ParameterIn.QUERY,
                    description = "Loppupäivä (ISO 8601, esim. 2025-10-31)")
    })
    public Page<OpetussuunnitelmaJulkinenDto> getOpetussuunnitelmat(@Parameter(hidden = true) OpetussuunnitelmaJulkaistuQuery query) {
        return opetussuunnitelmaService.getAllJulkaistutOpetussuunnitelmat(query);
    }

    @Operation(
            parameters = {
                    @Parameter(name = "opetussuunnitelmaId", description = "Opetussuunnitelman id", required = true)
            },
            summary = "Opetussuunnitelman tietojen haku"
    )
    @RequestMapping(value = "/opetussuunnitelma/{opetussuunnitelmaId}", method = RequestMethod.GET)
    public ResponseEntity<OpetussuunnitelmaExportDto> getExternalOpetussuunnitelma(@PathVariable("opetussuunnitelmaId") Long opetussuunnitelmaId) {
        return new ResponseEntity<>(opetussuunnitelmaService.getOpetussuunnitelmaJulkaistuSisalto(opetussuunnitelmaId), HttpStatus.OK);
    }

    @RequestMapping(value = "/opetussuunnitelma/{opetussuunnitelmaId:\\d+}/{custompath}", method = GET)
    @ResponseBody
    @Operation(
            parameters = {
                    @Parameter(name = "opetussuunnitelmaId", description = "Opetussuunnitelman id", required = true),
                    @Parameter(name = "custompath", description = "Opetussuunnitelman rakenteen osa", required = true)
            },
            summary = "Opetussuunnitelman tietojen haku tarkalla sisältörakenteella",
            description = "Url parametreiksi voi antaa opetussuunnitelman id:n lisäksi erilaisia opetussuunnitelman rakenteen osia ja id-kenttien arvoja. Esim. /opetussuunnitelma/11548134/opintojaksot/15598911/nimi/fi antaa opetussuunnitelman (id: 11548134) opintojaksojen tietueen (id: 15598911) nimen suomenkielisenä."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = OpetussuunnitelmaExportDto.class))}),
    })
    public ResponseEntity<Object> getOpetussuunnitelmaDynamicQuery(HttpServletRequest req, @PathVariable("opetussuunnitelmaId") long id, @PathVariable("custompath") String custompath) {
        return getJulkaistuSisaltoObjectNodeWithQuery(id, requestToQueries(req, DEFAULT_PATH_SKIP_VALUE));
    }

    @Hidden
    // Springdoc ei generoi rajapintoja /** poluille, joten tämä on tehty erikseen
    @RequestMapping(value = "/opetussuunnitelma/{opetussuunnitelmaId:\\d+}/{custompath}/**", method = GET)
    public ResponseEntity<Object> getOpetussuunnitelmaDynamicQueryHidden(HttpServletRequest req, @PathVariable("opetussuunnitelmaId") long id) {
        return getJulkaistuSisaltoObjectNodeWithQuery(id, requestToQueries(req, DEFAULT_PATH_SKIP_VALUE));
    }

    @Operation(summary = "Opetussuunnitelman perusteen haku. Palauttaa perusteen version, mikä opetussuunnitelmalla oli käytössä opetussuunnitelman julkaisun hetkellä.")
    @RequestMapping(value = "/opetussuunnitelma/{id}/peruste", method = RequestMethod.GET)
    public ResponseEntity<JsonNode> getExternalOpetussuunnitelmanPeruste(@PathVariable("id") Long id) {
        return new ResponseEntity<>(opetussuunnitelmaService.getJulkaistuOpetussuunnitelmaPeruste(id), HttpStatus.OK);
    }

    private List<String> requestToQueries(HttpServletRequest req, int skipCount) {
        String[] queries = req.getServletPath().split("/");
        return Arrays.stream(queries).skip(skipCount).collect(Collectors.toList());
    }

    private ResponseEntity<Object> getJulkaistuSisaltoObjectNodeWithQuery(long id, List<String> queries) {
        Object result = opetussuunnitelmaService.getJulkaistuSisaltoObjectNode(id, queries);
        if (result == null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return ResponseEntity.ok(result);
    }
}
