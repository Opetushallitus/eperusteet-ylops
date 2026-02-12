package fi.vm.sade.eperusteet.ylops.resource.hallinta;

import fi.vm.sade.eperusteet.ylops.repository.ops.PoistettuPerusteRepository;
import fi.vm.sade.eperusteet.ylops.service.util.PoistettuPerusteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping("/api/peruste/arkistoitu")
@Tag(name = "Arkistoidut perusteet")
public class PoistettuPerusteController {

    @Autowired
    private PoistettuPerusteService poistettuPerusteService;

    @RequestMapping(value = "/{perusteId}", method = GET)
    @Operation(summary = "Tarkistaa onko peruste arkistoitujen perusteiden listalla")
    public boolean perusteArkistoitu(
            @Parameter(description = "Perusteen id") @PathVariable("perusteId") Long perusteId) {
        return poistettuPerusteService.exists(perusteId);
    }

    @RequestMapping(value = "/{perusteId}/arkistoi", method = POST)
    @Operation(summary = "Lisää peruste arkistoitujen perusteiden listalle")
    public ResponseEntity<Void> addArkistoituPeruste(
            @Parameter(description = "Perusteen id") @PathVariable("perusteId") Long perusteId) {
        poistettuPerusteService.add(perusteId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @RequestMapping(value = "/{perusteId}/poista", method = POST)
    @Operation(summary = "Poistaa perusteen arkistoitujen perusteiden listalta")
    public ResponseEntity<Void> deleteArkistoituPeruste(
            @Parameter(description = "Perusteen id") @PathVariable("perusteId") Long perusteId) {
        poistettuPerusteService.delete(perusteId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

}
