package fi.vm.sade.eperusteet.ylops.test.docker;

import fi.vm.sade.eperusteet.ylops.dto.lops2019.Lops2019OpintojaksoDto;
import fi.vm.sade.eperusteet.ylops.service.ops.ValidointiService;
import fi.vm.sade.eperusteet.ylops.service.util.Validointi;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Primary
@Profile("docker")
public class ValidointiServiceDockerMock implements ValidointiService {

    @Override
    public List<Validointi> validoiOpetussuunnitelma(Long id) {
        return List.of();
    }

    @Override
    public List<Validointi> validoiLukio2019(Long opsId) {
        return List.of();
    }

    @Override
    public List<Validointi.Virhe> tarkistaOpintojaksot(Long opsId) {
        return List.of();
    }

    @Override
    public void tarkistaOpintojakso(Long opsId, Lops2019OpintojaksoDto opintojaksoDto) {
    }
}
