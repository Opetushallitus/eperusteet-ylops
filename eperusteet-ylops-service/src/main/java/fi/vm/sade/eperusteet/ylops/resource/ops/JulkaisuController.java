package fi.vm.sade.eperusteet.ylops.resource.ops;

import fi.vm.sade.eperusteet.ylops.service.util.JulkaisuService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Description;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@RequestMapping("/opetussuunnitelmat/{opsId}")
@Api(value = "Julkaisut")
@Description("Opetussuunnitelmien julkaisut")
public class JulkaisuController {

    @Autowired
    private JulkaisuService julkaisutService;

    @RequestMapping(method = GET, value = "/julkaisu/onkoMuutoksia")
    public boolean onkoMuutoksia(
            @PathVariable("opsId") final long opsId) {
        return julkaisutService.onkoMuutoksia(opsId);
    }

}
