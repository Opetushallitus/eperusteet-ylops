package fi.vm.sade.eperusteet.ylops.resource.ops;

import fi.vm.sade.eperusteet.ylops.dto.ops.OpetussuunnitelmanAikatauluDto;
import fi.vm.sade.eperusteet.ylops.service.ops.OpetussuunnitelmanAikatauluService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/aikataulu")
@Api("Aikataulu")
public class OpetussuunnitelmanAikatauluController {

    @Autowired
    private OpetussuunnitelmanAikatauluService opetussuunnitelmanAikatauluService;

    @RequestMapping(value = "/{opsId}", method = RequestMethod.GET)
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public List<OpetussuunnitelmanAikatauluDto> getAikataulu(@PathVariable("opsId") final Long opsId) {
        return opetussuunnitelmanAikatauluService.getAll(opsId);
    }

    @RequestMapping(value = "/{opsId}", method = RequestMethod.POST)
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public OpetussuunnitelmanAikatauluDto save(@PathVariable("opsId") final Long opsId,
                                                                     @RequestBody(required = true) OpetussuunnitelmanAikatauluDto opetussuunnitelmanAikatauluDto) {
        return opetussuunnitelmanAikatauluService.add(opsId, opetussuunnitelmanAikatauluDto);
    }

    @RequestMapping(value = "/{opsId}", method = RequestMethod.PUT)
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public OpetussuunnitelmanAikatauluDto update(@PathVariable("opsId") final Long opsId,
                                               @RequestBody(required = true) OpetussuunnitelmanAikatauluDto opetussuunnitelmanAikatauluDto) {
        return opetussuunnitelmanAikatauluService.update(opsId, opetussuunnitelmanAikatauluDto);
    }

    @RequestMapping(value = "/{opsId}", method = RequestMethod.DELETE)
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable("opsId") final Long opsId,
                                                 @RequestBody(required = true) OpetussuunnitelmanAikatauluDto opetussuunnitelmanAikatauluDto) {
        opetussuunnitelmanAikatauluService.delete(opsId, opetussuunnitelmanAikatauluDto);
    }
}
