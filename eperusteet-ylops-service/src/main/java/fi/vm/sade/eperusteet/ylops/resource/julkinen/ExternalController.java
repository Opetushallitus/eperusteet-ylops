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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/external", produces = "application/json;charset=UTF-8")
@Api(value = "Julkinen")
@Description("Opetussuunnitelminen julkinen rajapinta")
public class ExternalController {

    @Autowired
    private OpetussuunnitelmaService opetussuunnitelmaService;

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
    @RequestMapping(value = "/opetussuunnitelma/{id}", method = RequestMethod.GET)
    public ResponseEntity<OpetussuunnitelmaExportDto> getExternalOpetussuunnitelma(@PathVariable("id") final Long id) {
        return new ResponseEntity<>(opetussuunnitelmaService.getOpetussuunnitelmaJulkaistuSisalto(id), HttpStatus.OK);
    }

    @ApiOperation(value = "Opetussuunnitelman perusteen haku. Palauttaa perusteen version, mikä opetussuunnitelmalla oli käytössä opetussuunnitelman julkaisun hetkellä.")
    @RequestMapping(value = "/opetussuunnitelma/{id}/peruste", method = RequestMethod.GET)
    public ResponseEntity<JsonNode> getExternalOpetussuunnitelmanPeruste(@PathVariable("id") final Long id) {
        return new ResponseEntity<>(opetussuunnitelmaService.getJulkaistuOpetussuunnitelmaPeruste(id), HttpStatus.OK);
    }
}
