package fi.vm.sade.eperusteet.ylops.dto.lops2019.Validointi;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Lops2019KohdeDto {
    private String kohde;
    private List<String> varoitukset = new ArrayList<>();
    private List<String> virheet = new ArrayList<>();

    public Lops2019KohdeDto virhe(String kuvaus) {
        return this;
    }

    public Lops2019KohdeDto varoitus(String kuvaus) {
        return this;
    }
}
