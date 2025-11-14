package fi.vm.sade.eperusteet.ylops.service.util;

import fi.vm.sade.eperusteet.ylops.domain.ValidationCategory;
import fi.vm.sade.eperusteet.ylops.dto.navigation.NavigationNodeDto;
import fi.vm.sade.eperusteet.ylops.service.exception.ValidointiException;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
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

    @Data
    @AllArgsConstructor
    @Builder
    static public class Virhe {
        private String kuvaus;
        private NavigationNodeDto navigationNode;
    }

    public Validointi addAll(List<Virhe> virheet) {
        this.virheet.addAll(virheet.stream().filter(v -> !sisaltaaVirheen(this.virheet, v)).toList());
        return this;
    }

    public Validointi addVirhe(Virhe virhe) {
        if (sisaltaaVirheen(this.virheet, virhe)) {
            return this;
        }
        this.virheet.add(virhe);
        return this;
    }

    public Validointi addHuomautus(Virhe huomautukset) {
        if (sisaltaaVirheen(this.huomautukset, huomautukset)) {
            return this;
        }
        this.huomautukset.add(huomautukset);
        return this;
    }

    public Validointi virhe(String kuvaus, NavigationNodeDto navigationNode) {
        if (sisaltaaVirheen(this.virheet, new Virhe(kuvaus, navigationNode))) {
            return this;
        }
        virheet.add(new Virhe(kuvaus, navigationNode));
        return this;
    }

    private boolean sisaltaaVirheen(List<Virhe> virheet, Virhe virhe) {
        return virheet.stream().anyMatch(v -> v.equals(virhe));
    }
}
