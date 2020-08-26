package fi.vm.sade.eperusteet.ylops.domain;

import fi.vm.sade.eperusteet.ylops.domain.ops.OpetussuunnitelmanMuokkaustietoLisaparametrit;
import fi.vm.sade.eperusteet.ylops.domain.teksti.LokalisoituTeksti;
import fi.vm.sade.eperusteet.ylops.dto.navigation.NavigationType;
import java.util.Collections;
import java.util.Date;
import java.util.Set;

public interface HistoriaTapahtuma {

    Date getLuotu();
    Date getMuokattu();
    String getLuoja();
    String getMuokkaaja();

    Long getId();
    LokalisoituTeksti getNimi();
    NavigationType getNavigationType();

    default Set<OpetussuunnitelmanMuokkaustietoLisaparametrit> getLisaparametrit(){
        return Collections.emptySet();
    }
}
