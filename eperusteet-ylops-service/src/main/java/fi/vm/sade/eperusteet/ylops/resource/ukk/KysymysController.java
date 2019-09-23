package fi.vm.sade.eperusteet.ylops.resource.ukk;

import fi.vm.sade.eperusteet.ylops.dto.ukk.KysymysDto;
import fi.vm.sade.eperusteet.ylops.resource.config.InternalApi;
import fi.vm.sade.eperusteet.ylops.service.audit.EperusteetYlopsAudit;
import fi.vm.sade.eperusteet.ylops.service.audit.LogMessage;
import fi.vm.sade.eperusteet.ylops.service.ukk.KysymysService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static fi.vm.sade.eperusteet.ylops.service.audit.EperusteetYlopsMessageFields.OPH;
import static fi.vm.sade.eperusteet.ylops.service.audit.EperusteetYlopsOperation.*;
import static org.springframework.web.bind.annotation.RequestMethod.*;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;

@InternalApi
@RestController
@Api("Kysymykset")
@RequestMapping("/kysymykset")
public class KysymysController {

    @Autowired
    private EperusteetYlopsAudit audit;

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
        return audit.withAudit(LogMessage.builder(null, OPH, KYSYMYS_LISAYS), (Void) -> {
            dto.setId(null);
            return ResponseEntity.ok(service.createKysymys(dto));
        });
    }

    @RequestMapping(value = "/{id}", method = PUT)
    public ResponseEntity<KysymysDto> updateKysymys(
            @PathVariable Long id,
            @RequestBody KysymysDto dto
    ) {
        return audit.withAudit(LogMessage.builder(null, OPH, KYSYMYS_MUOKKAUS), (Void) -> {
            dto.setId(id);
            return ResponseEntity.ok(service.updateKysymys(dto));
        });
    }

    @RequestMapping(value = "/{id}", method = DELETE)
    public ResponseEntity deleteKysymys(
            @PathVariable Long id
    ) {
        return audit.withAudit(LogMessage.builder(null, OPH, KYSYMYS_POISTO), (Void) -> {
            service.deleteKysymys(id);
            return ResponseEntity.status(HttpStatus.OK).build();
        });
    }
}
