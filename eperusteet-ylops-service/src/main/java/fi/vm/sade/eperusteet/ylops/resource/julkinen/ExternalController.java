package fi.vm.sade.eperusteet.ylops.resource.julkinen;

import com.fasterxml.jackson.databind.JsonNode;
import fi.vm.sade.eperusteet.ylops.dto.OpetussuunnitelmaExportDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpetussuunnitelmaJulkaistuQuery;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpetussuunnitelmaJulkinenDto;
import fi.vm.sade.eperusteet.ylops.service.ops.OpetussuunnitelmaService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
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

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@CrossOrigin
@RestController
@RequestMapping(value = "/api/external", produces = "application/json;charset=UTF-8")
@Api(value = "Julkinen")
@Description("Opetussuunnitelminen julkinen rajapinta")
public class ExternalController {

    @Autowired
    private OpetussuunnitelmaService opetussuunnitelmaService;

    private static final int DEFAULT_PATH_SKIP_VALUE = 5;

    @ApiOperation(value = "Opetussuunnitelmien haku")
    @RequestMapping(method = RequestMethod.GET, value = "/opetussuunnitelmat")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "nimi", dataType = "string", paramType = "query", value = "nimi"),
            @ApiImplicitParam(name = "kieli", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "perusteenDiaarinumero", dataType = "string", paramType = "query", value = "perusteenDiaarinumero"),
            @ApiImplicitParam(name = "koulutustyypit", dataType = "string", paramType = "query", allowMultiple = true, value = "koulutustyypit"),
            @ApiImplicitParam(name = "sivu", dataType = "long", paramType = "query"),
            @ApiImplicitParam(name = "sivukoko", dataType = "long", paramType = "query"),
    })
    public Page<OpetussuunnitelmaJulkinenDto> getOpetussuunnitelmat(OpetussuunnitelmaJulkaistuQuery query) {
        return opetussuunnitelmaService.getAllJulkaistutOpetussuunnitelmat(query);
    }

    @ApiOperation(value = "Opetussuunnitelman tietojen haku")
    @RequestMapping(value = "/opetussuunnitelma/{opetussuunnitelmaId}", method = RequestMethod.GET)
    public ResponseEntity<OpetussuunnitelmaExportDto> getExternalOpetussuunnitelma(@PathVariable("opetussuunnitelmaId") final Long opetussuunnitelmaId) {
        return new ResponseEntity<>(opetussuunnitelmaService.getOpetussuunnitelmaJulkaistuSisalto(opetussuunnitelmaId), HttpStatus.OK);
    }

    @RequestMapping(value = "/opetussuunnitelma/{opetussuunnitelmaId:\\d+}/**", method = GET)
    @ResponseBody
    @ApiOperation(
            value = "Opetussuunnitelman tietojen haku tarkalla sisältörakenteella",
            notes = "Url parametreiksi voi antaa opetussuunnitelman id:n lisäksi erilaisia opetussuunnitelman rakenteen osia ja id-kenttien arvoja. Esim. /opetussuunnitelma/11548134/opintojaksot/15598911/nimi/fi antaa opetussuunnitelman (id: 11548134) opintojaksojen tietueen (id: 15598911) nimen suomenkielisenä.",
            response= OpetussuunnitelmaJulkinenDto.class
    )
    public ResponseEntity<Object> getOpetussuunnitelmaDynamicQuery(HttpServletRequest req, @PathVariable("opetussuunnitelmaId") final long id) {
        return getJulkaistuSisaltoObjectNodeWithQuery(id, requestToQueries(req, DEFAULT_PATH_SKIP_VALUE));
    }

    @ApiOperation(value = "Opetussuunnitelman perusteen haku. Palauttaa perusteen version, mikä opetussuunnitelmalla oli käytössä opetussuunnitelman julkaisun hetkellä.")
    @RequestMapping(value = "/opetussuunnitelma/{id}/peruste", method = RequestMethod.GET)
    public ResponseEntity<JsonNode> getExternalOpetussuunnitelmanPeruste(@PathVariable("id") final Long id) {
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
