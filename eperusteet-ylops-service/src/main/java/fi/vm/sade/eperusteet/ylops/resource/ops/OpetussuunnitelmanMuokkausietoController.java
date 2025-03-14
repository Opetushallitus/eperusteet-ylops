package fi.vm.sade.eperusteet.ylops.resource.ops;

import fi.vm.sade.eperusteet.ylops.dto.ops.MuokkaustietoKayttajallaDto;
import fi.vm.sade.eperusteet.ylops.dto.ops.OpetussuunnitelmanMuokkaustietoDto;
import fi.vm.sade.eperusteet.ylops.service.ops.OpetussuunnitelmanMuokkaustietoService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/muokkaustieto")
@Tag(name = "Muokkaustieto")
public class OpetussuunnitelmanMuokkausietoController {

    @Autowired
    private OpetussuunnitelmanMuokkaustietoService muokkausTietoService;

    @RequestMapping(value = "/{opsId}", method = RequestMethod.GET)
    public ResponseEntity<List<MuokkaustietoKayttajallaDto>> getOpsMuokkausTiedotWithLuomisaika(@PathVariable("opsId") final Long opsId,
                                                                                     @RequestParam(value = "viimeisinLuomisaika", required = false) final Long viimeisinLuomisaika,
                                                                                     @RequestParam(value = "lukumaara", required = false, defaultValue="10") int lukumaara) {
        return ResponseEntity.ok(muokkausTietoService.getOpsMuokkausTietos(opsId, viimeisinLuomisaika != null ? new Date(viimeisinLuomisaika) : new Date(), lukumaara));
    }

    @GetMapping("/{opsId}/viimeisinpohjatekstisync")
    public ResponseEntity<OpetussuunnitelmanMuokkaustietoDto> getViimeisinPohjatekstiSync(@PathVariable("opsId") final Long opsId) {
        return ResponseEntity.ok(muokkausTietoService.getViimeisinPohjatekstiSync(opsId));
    }

    @GetMapping("/{opsId}/viimeisinpohjatekstisync/pohjaopetussuunnitelma")
    public ResponseEntity<OpetussuunnitelmanMuokkaustietoDto> getOpetussuunnitelmanPohjanViimeisinPohjaTekstiSync(@PathVariable("opsId") final Long opsId) {
        return ResponseEntity.ok(muokkausTietoService.getOpetussuunnitelmanPohjanViimeisinPohjaTekstiSync(opsId));
    }
}
