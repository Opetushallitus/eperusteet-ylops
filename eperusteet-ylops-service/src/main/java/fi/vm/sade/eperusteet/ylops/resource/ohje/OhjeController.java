package fi.vm.sade.eperusteet.ylops.resource.ohje;

import fi.vm.sade.eperusteet.ylops.dto.ohje.OhjeDto;
import fi.vm.sade.eperusteet.ylops.service.ohje.OhjeService;
import io.swagger.annotations.Api;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@RequestMapping("/api/ohjeet")
@ApiIgnore
@Api("Ohjeet")
public class OhjeController {

    @Autowired
    private OhjeService service;

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<OhjeDto> addOhje(@RequestBody OhjeDto ohjeDto) {
        return new ResponseEntity<>(service.addOhje(ohjeDto), HttpStatus.OK);
    }

    @RequestMapping(value = "/tekstikappale/{uuid}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List<OhjeDto>> getTekstiKappaleOhje(@PathVariable("uuid") final UUID uuid) {
        List<OhjeDto> ohjeDtos = service.getTekstiKappaleOhjeet(uuid);
        if (ohjeDtos == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(ohjeDtos, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<OhjeDto> getOhje(@PathVariable("id") final Long id) {
        return new ResponseEntity<>(service.getOhje(id), HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<OhjeDto> updateOhje(
            @PathVariable("id") final Long id,
            @RequestBody OhjeDto ohjeDto) {
        ohjeDto.setId(id);
        return new ResponseEntity<>(service.updateOhje(ohjeDto), HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public void deleteOhje(@PathVariable("id") final Long id) {
        service.removeOhje(id);
    }
}
