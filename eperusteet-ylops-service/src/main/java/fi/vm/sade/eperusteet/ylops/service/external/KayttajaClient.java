package fi.vm.sade.eperusteet.ylops.service.external;

import fi.vm.sade.eperusteet.ylops.dto.kayttaja.KayttajanTietoDto;
import java.util.List;

public interface KayttajaClient {

    KayttajanTietoDto hae(String oid);

    List<KayttajanTietoDto> haeKayttajatiedot(List<String> oid);
}
