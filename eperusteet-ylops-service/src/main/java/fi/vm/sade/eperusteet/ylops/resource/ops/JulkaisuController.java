package fi.vm.sade.eperusteet.ylops.resource.ops;

import fi.vm.sade.eperusteet.ylops.domain.ops.JulkaisuTila;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpetussuunnitelmanJulkaisuDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.UusiJulkaisuDto;
import fi.vm.sade.eperusteet.ylops.service.util.JulkaisuService;
import io.swagger.annotations.Api;
import org.skyscreamer.jsonassert.FieldComparisonFailure;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Description;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@RequestMapping("/opetussuunnitelmat/{opsId}")
@Api(value = "Julkaisut")
@Description("Opetussuunnitelmien julkaisut")
public class JulkaisuController {

    @Autowired
    private JulkaisuService julkaisuService;

    @RequestMapping(method = GET, value = "/julkaisu/muutokset")
    public List<FieldComparisonFailure> julkaisuversioMuutokset(
            @PathVariable("opsId") final long opsId) {
        return julkaisuService.julkaisuversioMuutokset(opsId);
    }

    @RequestMapping(method = GET, value = "/viimeisinjulkaisutila")
    public JulkaisuTila viimeisinJulkaisuTila(
            @PathVariable("opsId") final long opsId) {
        return julkaisuService.viimeisinJulkaisuTila(opsId);
    }

    @RequestMapping(value = "/julkaise", method = RequestMethod.POST)
    public void julkaise(
            @PathVariable final Long opsId,
            @RequestBody final UusiJulkaisuDto julkaisuDto) {
        julkaisuService.addJulkaisu(opsId, julkaisuDto);
    }

    @RequestMapping(value = "/aktivoi/{revision}", method = RequestMethod.POST)
    public OpetussuunnitelmanJulkaisuDto aktivoiJulkaisu(
            @PathVariable final Long opsId,
            @PathVariable final int revision) {
        return julkaisuService.aktivoiJulkaisu(opsId, revision);
    }

    @RequestMapping(value = "/julkaisut", method = RequestMethod.GET)
    public List<OpetussuunnitelmanJulkaisuDto> getJulkaisut(
            @PathVariable final Long opsId) {
        return julkaisuService.getJulkaisut(opsId);
    }

    @RequestMapping(value = "/julkaisut/kevyt", method = RequestMethod.GET)
    public List<OpetussuunnitelmanJulkaisuDto> getJulkaisutKevyt(
            @PathVariable final Long opsId) {
        return julkaisuService.getJulkaisutKevyt(opsId);
    }

}
