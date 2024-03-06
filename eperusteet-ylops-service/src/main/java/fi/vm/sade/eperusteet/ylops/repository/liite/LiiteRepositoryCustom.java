package fi.vm.sade.eperusteet.ylops.repository.liite;

import fi.vm.sade.eperusteet.ylops.domain.liite.Liite;

import java.io.InputStream;

public interface LiiteRepositoryCustom {
    Liite add(String tyyppi, String nimi, long length, InputStream is);
}
