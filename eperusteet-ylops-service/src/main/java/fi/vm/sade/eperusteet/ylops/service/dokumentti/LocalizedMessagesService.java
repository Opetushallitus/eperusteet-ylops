package fi.vm.sade.eperusteet.ylops.service.dokumentti;

import fi.vm.sade.eperusteet.ylops.domain.teksti.Kieli;

public interface LocalizedMessagesService {
    String translate(String key, Kieli kieli);
}
