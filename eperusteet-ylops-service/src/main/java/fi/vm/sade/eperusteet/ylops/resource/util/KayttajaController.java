package fi.vm.sade.eperusteet.ylops.resource.util;

import fi.vm.sade.eperusteet.ylops.dto.kayttaja.EtusivuDto;
import fi.vm.sade.eperusteet.ylops.dto.kayttaja.KayttajanTietoDto;
import fi.vm.sade.eperusteet.ylops.service.external.KayttajanTietoService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Set;

@RestController
@RequestMapping("/api/kayttaja")
@Api("Kayttajat")
@ApiIgnore
public class KayttajaController {

    @Autowired
    private KayttajanTietoService kayttajat;

    @RequestMapping(method = RequestMethod.GET)
    public KayttajanTietoDto getKayttaja() {
        return kayttajat.haeKirjautaunutKayttaja();
    }

    @RequestMapping(value = "/organisaatiot", method = RequestMethod.GET)
    public Set<String> getOrganisaatioOikeudet() {
        return kayttajat.haeOrganisaatioOikeudet();
    }

    @RequestMapping(value = "/etusivu", method = RequestMethod.GET)
    public EtusivuDto getKayttajanEtusivu() {
        return kayttajat.haeKayttajanEtusivu();
    }

}
