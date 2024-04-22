package fi.vm.sade.eperusteet.ylops.service.external;

import fi.vm.sade.eperusteet.ylops.dto.kayttaja.EtusivuDto;
import fi.vm.sade.eperusteet.ylops.dto.kayttaja.KayttajanProjektitiedotDto;
import fi.vm.sade.eperusteet.ylops.dto.kayttaja.KayttajanTietoDto;

import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;

import org.springframework.security.access.prepost.PreAuthorize;

public interface KayttajanTietoService {

    @PreAuthorize("isAuthenticated()")
    KayttajanTietoDto haeKirjautaunutKayttaja();

    @PreAuthorize("isAuthenticated()")
    KayttajanTietoDto hae(String oid);

    @PreAuthorize("isAuthenticated()")
    String haeKayttajanimi(String oid);

    @PreAuthorize("isAuthenticated()")
    Future<KayttajanTietoDto> haeAsync(String oid);

    @PreAuthorize("isAuthenticated()")
    Set<String> haeOrganisaatioOikeudet();

    @PreAuthorize("isAuthenticated()")
    List<KayttajanProjektitiedotDto> haeOpetussuunnitelmat(String oid);

    @PreAuthorize("isAuthenticated()")
    KayttajanProjektitiedotDto haeOpetussuunnitelma(String oid, Long opsId);

    @PreAuthorize("isAuthenticated()")
    EtusivuDto haeKayttajanEtusivu();

    @PreAuthorize("isAuthenticated()")
    List<KayttajanTietoDto> haeKayttajatiedot(List<String> oid);

}
