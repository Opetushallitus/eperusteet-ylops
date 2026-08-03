package fi.vm.sade.eperusteet.ylops.service.mocks;

import com.google.common.collect.ImmutableMap;
import fi.vm.sade.eperusteet.ylops.dto.koodisto.KoodistoKoodiDto;
import fi.vm.sade.eperusteet.ylops.dto.koodisto.KoodistoMetadataDto;
import fi.vm.sade.eperusteet.ylops.service.external.KoodistoService;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class KoodistoServiceMock implements KoodistoService {

    private static final Map<String, Map<String, String>> KOODIT = ImmutableMap.of(
            "taiteenalat", ImmutableMap.of(
                    "taiteenalat_musiikki", "Musiikki",
                    "taiteenalat_tanssi", "Tanssi"));

    @Override
    public List<KoodistoKoodiDto> getAll(String koodisto) {
        return Collections.emptyList();
    }

    @Override
    public KoodistoKoodiDto get(String koodisto, String koodi) {
        String nimi = KOODIT.getOrDefault(koodisto, Collections.emptyMap()).get(koodi);
        if (nimi == null) {
            return null;
        }

        KoodistoKoodiDto dto = new KoodistoKoodiDto();
        dto.setKoodiUri(koodi);
        dto.setKoodiArvo(koodi.substring(koodisto.length() + 1));
        dto.setMetadata(new KoodistoMetadataDto[]{new KoodistoMetadataDto(nimi, "FI")});
        return dto;
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
