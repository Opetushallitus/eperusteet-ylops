package fi.vm.sade.eperusteet.ylops.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Embeddable
@EqualsAndHashCode
public class LaajaalainenosaaminenViite {

    @Getter
    @Column(name = "laajaalainenosaaminen_viite")
    private String viite;

    public LaajaalainenosaaminenViite(String viite) {
        this.viite = viite;
    }

    public LaajaalainenosaaminenViite(LaajaalainenosaaminenViite other) {
        this.viite = other.getViite();
    }

    protected LaajaalainenosaaminenViite() {
        //JPA
    }
}
