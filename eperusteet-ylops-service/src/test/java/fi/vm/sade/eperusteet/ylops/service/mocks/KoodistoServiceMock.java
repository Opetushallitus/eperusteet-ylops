package fi.vm.sade.eperusteet.ylops.service.mocks;

import fi.vm.sade.eperusteet.ylops.dto.koodisto.KoodistoKoodiDto;
import fi.vm.sade.eperusteet.ylops.service.external.KoodistoService;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class KoodistoServiceMock implements KoodistoService {
    @Override
    public List<KoodistoKoodiDto> getAll(String koodisto) {
        return Collections.emptyList();
    }

    @Override
    public KoodistoKoodiDto get(String koodisto, String koodi) {
        return null;
    }

    @Override
    public List<KoodistoKoodiDto> filterBy(String koodisto, String haku) {
        return Collections.emptyList();
    }

    @Override
    public List<KoodistoKoodiDto> getAlarelaatio(String koodi) {
        return Collections.emptyList();
    }

    @Override
    public List<KoodistoKoodiDto> getYlarelaatio(String koodi) {
        return Collections.emptyList();
    }
}
