package fi.vm.sade.eperusteet.ylops.service.mocks;

import fi.vm.sade.eperusteet.ylops.dto.kayttaja.KayttajanTietoDto;
import fi.vm.sade.eperusteet.ylops.service.external.KayttajaClient;
import java.util.Arrays;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class KayttajaClientMock implements KayttajaClient {
    @Override
    public KayttajanTietoDto hae(String oid) {
        return null;
    }

    @Override
    public List<KayttajanTietoDto> haeKayttajatiedot(List<String> oid) {
        return Arrays.asList(
                KayttajanTietoDto.builder()
                    .oidHenkilo("test")
                    .etunimet("Teppo")
                    .sukunimi("Testaaja")
                .build()
        );
    }
}
