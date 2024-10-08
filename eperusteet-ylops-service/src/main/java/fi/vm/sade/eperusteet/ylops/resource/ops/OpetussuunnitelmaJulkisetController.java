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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/opetussuunnitelmat/julkiset")
@Api(value = "Opetussuunnitelmat julkiset")
public class OpetussuunnitelmaJulkisetController {
    @Autowired
    private OpetussuunnitelmaService opetussuunnitelmaService;

    @RequestMapping(method = RequestMethod.GET)
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
    @Timed
    public ResponseEntity<OpetussuunnitelmaJulkinenDto> getOpetussuunnitelmanJulkisetTiedot(@PathVariable("id") final Long id) {
        return new ResponseEntity<>(opetussuunnitelmaService.getOpetussuunnitelmaJulkinen(id), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/julkaisut")
    @Timed
    @ApiImplicitParams({
            @ApiImplicitParam(name = "koulutustyypit", dataType = "string", paramType = "query", allowMultiple = true, value = "koulutustyypit"),
            @ApiImplicitParam(name = "nimi", dataType = "string", paramType = "query", value = "nimi"),
            @ApiImplicitParam(name = "kieli", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "perusteenDiaarinumero", dataType = "string", paramType = "query", value = "perusteenDiaarinumero"),
            @ApiImplicitParam(name = "sivu", dataType = "long", paramType = "query"),
            @ApiImplicitParam(name = "sivukoko", dataType = "long", paramType = "query"),
    })
    public Page<OpetussuunnitelmaJulkinenDto> getAllJulkaistutOpetussuunnitelmat(OpetussuunnitelmaJulkaistuQuery query) {
        return opetussuunnitelmaService.getAllJulkaistutOpetussuunnitelmat(query);
    }

    @RequestMapping(value = "/{id}/julkaisu", method = RequestMethod.GET)
    @Timed
    public ResponseEntity<OpetussuunnitelmaExportDto> getOpetussuunnitelmaJulkaistu(
            @PathVariable("id") final Long id,
            @RequestParam(required = false) Integer revision) {
        return new ResponseEntity<>(opetussuunnitelmaService.getOpetussuunnitelmaJulkaistuSisalto(id, revision), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/kaikki")
    public List<OpetussuunnitelmaJulkinenDto> getKaikkiJulkaistutOpetussuunnitelmat() {
        return opetussuunnitelmaService.getKaikkiJulkaistutOpetussuunnitelmat();
    }

}
