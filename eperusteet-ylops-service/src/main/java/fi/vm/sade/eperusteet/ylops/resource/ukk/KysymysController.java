package fi.vm.sade.eperusteet.ylops.resource.ukk;

import fi.vm.sade.eperusteet.ylops.dto.ukk.KysymysDto;
import fi.vm.sade.eperusteet.ylops.resource.config.InternalApi;
import fi.vm.sade.eperusteet.ylops.service.ukk.KysymysService;
import io.swagger.annotations.Api;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

@InternalApi
@RestController
@Api("Kysymykset")
@RequestMapping("/api/kysymykset")
public class KysymysController {

    @Autowired
    private KysymysService service;

    @RequestMapping(method = GET)
    public ResponseEntity<List<KysymysDto>> getKysymykset(

    ) {
        return ResponseEntity.ok(service.getKysymykset());
    }

    @RequestMapping(method = POST)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<KysymysDto> createKysymys(
            @RequestBody KysymysDto dto
    ) {
        dto.setId(null);
        return ResponseEntity.ok(service.createKysymys(dto));
    }

    @RequestMapping(value = "/{id}", method = PUT)
    public ResponseEntity<KysymysDto> updateKysymys(
            @PathVariable Long id,
            @RequestBody KysymysDto dto
    ) {
        dto.setId(id);
        return ResponseEntity.ok(service.updateKysymys(dto));
    }

    @RequestMapping(value = "/{id}", method = DELETE)
    public ResponseEntity deleteKysymys(
            @PathVariable Long id
    ) {
        service.deleteKysymys(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
