package fi.vm.sade.eperusteet.ylops.service.util;

import fi.vm.sade.eperusteet.ylops.domain.ValidationCategory;
import fi.vm.sade.eperusteet.ylops.dto.navigation.NavigationNodeDto;
import fi.vm.sade.eperusteet.ylops.service.exception.ValidointiException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
public class Validointi {

    @Setter
    private ValidationCategory kategoria;
    private List<Virhe> virheet = new ArrayList<>();
    private List<Virhe> huomautukset = new ArrayList<>();
    private List<Virhe> huomiot = new ArrayList<>();

    public Validointi(ValidationCategory kategoria) {
        this.kategoria = kategoria;
    }

    @Getter
    @AllArgsConstructor
    @Builder
    static public class Virhe {
        private String kuvaus;
        private NavigationNodeDto navigationNode;
    }

    public Validointi addAll(List<Virhe> virheet) {
        this.virheet.addAll(virheet);
        return this;
    }

    public Validointi addVirhe(Virhe virhe) {
        this.virheet.add(virhe);
        return this;
    }

    public Validointi addHuomautus(Virhe huomautukset) {
        this.huomautukset.add(huomautukset);
        return this;
    }

    public Validointi virhe(String kuvaus, NavigationNodeDto navigationNode) {
        virheet.add(new Virhe(kuvaus, navigationNode));
        return this;
    }

    public void tuomitse() {
        if (!virheet.isEmpty()) {
            throw new ValidointiException(this);
        }
    }
}
