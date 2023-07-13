package fi.vm.sade.eperusteet.ylops.service.util;

import fi.vm.sade.eperusteet.ylops.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.ylops.domain.teksti.LokalisoituTeksti;
import fi.vm.sade.eperusteet.ylops.service.exception.ValidointiException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lombok.Getter;

@Getter
public class Validointi {
    @Getter
    static public class Virhe {
        private String syy;
        private Map<Kieli, String> nimi;

        Virhe(String syy) {
            this.syy = syy;
            this.nimi = null;
        }

        Virhe(String syy, LokalisoituTeksti t) {
            this.syy = syy;
            if (t != null) {
                this.nimi = t.getTeksti();
            }
        }
    }

    private List<Virhe> virheet = new ArrayList<>();
    private List<Virhe> varoitukset = new ArrayList<>();
    private List<Virhe> huomiot = new ArrayList<>();

    public void virhe(String syy) {
        virheet.add(new Virhe(syy));
    }

    public void virhe(String syy, LokalisoituTeksti... args) {
        for (LokalisoituTeksti arg : args) {
            virheet.add(new Virhe(syy, arg));
        }
    }

    public void varoitus(String syy) {
        varoitukset.add(new Virhe(syy));
    }

    public void varoitus(String syy, LokalisoituTeksti... args) {
        for (LokalisoituTeksti arg : args) {
            varoitukset.add(new Virhe(syy, arg));
        }
    }

    public void huomio(String syy) {
        huomiot.add(new Virhe(syy));
    }

    public void huomio(String syy, LokalisoituTeksti... args) {
        for (LokalisoituTeksti arg : args) {
            huomiot.add(new Virhe(syy, arg));
        }
    }

    public void tuomitse() {
        if (!virheet.isEmpty()) {
            throw new ValidointiException(this);
        }
    }
}
