package fi.vm.sade.eperusteet.ylops.resource.julkinen;

import com.fasterxml.jackson.core.JsonProcessingException;
import fi.vm.sade.eperusteet.ylops.dto.PalauteDto;
import fi.vm.sade.eperusteet.ylops.service.external.EperusteetService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/palaute")
@Api(value = "Palautteet")
public class PalauteController {

    @Autowired
    private EperusteetService eperusteetService;

    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.ACCEPTED)
    @ResponseBody
    public PalauteDto sendPalaute(@RequestBody PalauteDto palauteDto) throws JsonProcessingException {
        return eperusteetService.lahetaPalaute(palauteDto);
    }

}
