package fi.vm.sade.eperusteet.ylops.resource.ops;

import fi.vm.sade.eperusteet.ylops.dto.ops.MuokkaustietoKayttajallaDto;
import fi.vm.sade.eperusteet.ylops.service.ops.OpetussuunnitelmanMuokkaustietoService;
import io.swagger.annotations.Api;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/muokkaustieto")
@Api("Muokkaustieto")
public class OpetussuunnitelmanMuokkausietoController {

    @Autowired
    private OpetussuunnitelmanMuokkaustietoService muokkausTietoService;

    @RequestMapping(value = "/{opsId}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List<MuokkaustietoKayttajallaDto>> getOpsMuokkausTiedotWithLuomisaika(@PathVariable("opsId") final Long opsId,
                                                                                     @RequestParam(value = "viimeisinLuomisaika", required = false) final Long viimeisinLuomisaika,
                                                                                     @RequestParam(value = "lukumaara", required = false, defaultValue="10") int lukumaara) {
        return ResponseEntity.ok(muokkausTietoService.getOpsMuokkausTietos(opsId, viimeisinLuomisaika != null ? new Date(viimeisinLuomisaika) : new Date(), lukumaara));
    }
}
