package fi.vm.sade.eperusteet.ylops.service.mocks;

import fi.vm.sade.eperusteet.ylops.dto.kayttaja.EtusivuDto;
import fi.vm.sade.eperusteet.ylops.dto.kayttaja.KayttajanProjektitiedotDto;
import fi.vm.sade.eperusteet.ylops.dto.kayttaja.KayttajanTietoDto;
import fi.vm.sade.eperusteet.ylops.service.external.KayttajanTietoService;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;

@Service
public class KayttajanTietoServiceMock implements KayttajanTietoService {
    @Override
    public KayttajanTietoDto hae(String oid) {
        return null;
    }

    @Override
    public String haeKayttajanimi(String oid) {
        return null;
    }

    @Override
    public Future<KayttajanTietoDto> haeAsync(String oid) {
        return new AsyncResult<>(hae(oid));
    }

    @Override
    public KayttajanTietoDto haeKirjautaunutKayttaja() {
        return hae(null);
    }

    @Override
    public List<KayttajanProjektitiedotDto> haeOpetussuunnitelmat(String oid) {
        return Collections.emptyList();
    }

    @Override
    public KayttajanProjektitiedotDto haeOpetussuunnitelma(String oid, Long opsId) {
        return null;
    }

    @Override
    public Set<String> haeOrganisaatioOikeudet() {
        return null;
    }

    @Override
    public EtusivuDto haeKayttajanEtusivu() {
        return null;
    }

    @Override
    public List<KayttajanTietoDto> haeKayttajatiedot(List<String> oid) {
        return new ArrayList<>();
    }

}
