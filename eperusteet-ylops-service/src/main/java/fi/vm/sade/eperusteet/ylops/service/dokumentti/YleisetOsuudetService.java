package fi.vm.sade.eperusteet.ylops.service.dokumentti;

import fi.vm.sade.eperusteet.ylops.service.dokumentti.impl.util.DokumenttiBase;

public interface YleisetOsuudetService {
    void addYleisetOsuudet(DokumenttiBase docBase);

    void addLiitteet(DokumenttiBase docBase);
}
