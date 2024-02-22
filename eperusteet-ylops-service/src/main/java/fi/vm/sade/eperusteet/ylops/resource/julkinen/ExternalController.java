package fi.vm.sade.eperusteet.ylops.resource.julkinen;

import com.codahale.metrics.annotation.Timed;
import fi.vm.sade.eperusteet.ylops.dto.OpetussuunnitelmaExportDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpetussuunnitelmaJulkaistuQuery;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpetussuunnitelmaJulkinenDto;
import fi.vm.sade.eperusteet.ylops.service.ops.OpetussuunnitelmaService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Description;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/external", produces = "application/json;charset=UTF-8")
@Api(value = "Julkinen")
@Description("Opetussuunnitelminen julkinen rajapinta")
public class ExternalController {

    @Autowired
    private OpetussuunnitelmaService opetussuunnitelmaService;

    @RequestMapping(method = RequestMethod.GET, value = "/opetussuunnitelmat")
    @ResponseBody
    @Timed
    @ApiImplicitParams({
            @ApiImplicitParam(name = "nimi", dataType = "string", paramType = "query", value = "nimi"),
            @ApiImplicitParam(name = "kieli", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "perusteenDiaarinumero", dataType = "string", paramType = "query", value = "perusteenDiaarinumero"),
            @ApiImplicitParam(name = "koulutustyypit", dataType = "string", paramType = "query", allowMultiple = true, value = "koulutustyypit"),
            @ApiImplicitParam(name = "sivu", dataType = "long", paramType = "query"),
            @ApiImplicitParam(name = "sivukoko", dataType = "long", paramType = "query"),
    })
    public Page<OpetussuunnitelmaJulkinenDto> getPublicOpetussuunnitelmat(OpetussuunnitelmaJulkaistuQuery query) {
        return opetussuunnitelmaService.getAllJulkaistutOpetussuunnitelmat(query);
    }

    @RequestMapping(value = "/opetussuunnitelma/{id}", method = RequestMethod.GET)
    @ResponseBody
    @Timed
    public ResponseEntity<OpetussuunnitelmaExportDto> getPublicOpetussuunnitelma(@PathVariable("id") final Long id) {
        return new ResponseEntity<>(opetussuunnitelmaService.getOpetussuunnitelmaJulkaistuSisalto(id), HttpStatus.OK);
    }
}
