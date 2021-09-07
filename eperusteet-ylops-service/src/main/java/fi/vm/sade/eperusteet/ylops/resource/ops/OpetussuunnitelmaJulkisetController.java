package fi.vm.sade.eperusteet.ylops.resource.ops;

import com.codahale.metrics.annotation.Timed;
import fi.vm.sade.eperusteet.ylops.dto.OpetussuunnitelmaExportDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpetussuunnitelmaJulkaistuQuery;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpetussuunnitelmaJulkinenDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpetussuunnitelmaQuery;
import fi.vm.sade.eperusteet.ylops.service.ops.OpetussuunnitelmaService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/opetussuunnitelmat/julkiset")
@Api(value = "Opetussuunnitelmat julkiset")
public class OpetussuunnitelmaJulkisetController {
    @Autowired
    private OpetussuunnitelmaService opetussuunnitelmaService;


    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    @Timed
    @ApiImplicitParams({
            @ApiImplicitParam(name = "koulutustyyppi", dataType = "string", paramType = "query", allowMultiple = false, value = "koulutustyyppi (koodistokoodi)"),
            @ApiImplicitParam(name = "organisaatio", dataType = "string", paramType = "query", allowMultiple = false, value = "organisaatio oid (organisaatiopalvelusta)"),
            @ApiImplicitParam(name = "tyyppi", dataType = "string", paramType = "query", allowMultiple = false, value = "ops | pohja"),
            @ApiImplicitParam(name = "perusteenId", dataType = "string", paramType = "query", allowMultiple = false, value = "perusterajaus"),
            @ApiImplicitParam(name = "perusteenDiaarinumero", dataType = "string", paramType = "query", allowMultiple = false, value = "perusterajaus")
    })
    public List<OpetussuunnitelmaJulkinenDto> getAllJulkiset(OpetussuunnitelmaQuery query) {
        return opetussuunnitelmaService.getAllJulkiset(query);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    @Timed
    public ResponseEntity<OpetussuunnitelmaJulkinenDto> getOpetussuunnitelmanJulkisetTiedot(@PathVariable("id") final Long id) {
        return new ResponseEntity<>(opetussuunnitelmaService.getOpetussuunnitelmaJulkinen(id), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/julkaisut")
    @ResponseBody
    @Timed
    @ApiImplicitParams({
            @ApiImplicitParam(name = "nimi", dataType = "string", paramType = "query", value = "nimi"),
            @ApiImplicitParam(name = "kieli", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "perusteenDiaarinumero", dataType = "string", paramType = "query", value = "perusteenDiaarinumero"),
            @ApiImplicitParam(name = "sivu", dataType = "long", paramType = "query"),
            @ApiImplicitParam(name = "sivukoko", dataType = "long", paramType = "query"),
    })
    public Page<OpetussuunnitelmaJulkinenDto> getAllJulkaistutOpetussuunnitelmat(OpetussuunnitelmaJulkaistuQuery query) {
        return opetussuunnitelmaService.getAllJulkaistutOpetussuunnitelmat(query);
    }

    @RequestMapping(value = "/{id}/julkaistu", method = RequestMethod.GET)
    @ResponseBody
    @Timed
    public ResponseEntity<OpetussuunnitelmaExportDto> getOpetussuunnitelmaJulkaistu(@PathVariable("id") final Long id) {
        return new ResponseEntity<>(opetussuunnitelmaService.getOpetussuunnitelmaJulkaistuSisalto(id), HttpStatus.OK);
    }

}
