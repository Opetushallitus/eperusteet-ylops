package fi.vm.sade.eperusteet.ylops.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.google.common.collect.Sets;

import java.util.EnumSet;
import java.util.Set;

public enum Tila {
    LUONNOS("luonnos") {
        @Override
        public Set<Tila> mahdollisetSiirtymat(boolean isPohja) {
            return EnumSet.of(VALMIS, POISTETTU);
        }
    },
    VALMIS("valmis") {
        @Override
        public Set<Tila> mahdollisetSiirtymat(boolean isPohja) {
            return isPohja ? EnumSet.of(LUONNOS, POISTETTU) : EnumSet.of(LUONNOS, POISTETTU, JULKAISTU);
        }
    },
    POISTETTU("poistettu") {
        @Override
        public Set<Tila> mahdollisetSiirtymat(boolean isPohja) {
            return EnumSet.of(LUONNOS, POISTETTU, JULKAISTU);
        }
    },
    JULKAISTU("julkaistu") {
        @Override
        public Set<Tila> mahdollisetSiirtymat(boolean isPohja) {
            return EnumSet.of(LUONNOS, POISTETTU);
        }
    };

    private final String tila;

    Tila(String tila) {
        this.tila = tila;
    }

    static public Set<Tila> poistetut() {
        return Sets.newHashSet(POISTETTU);
    }

    static public Set<Tila> julkaisemattomat() {
        return Sets.newHashSet(LUONNOS, VALMIS);
    }

    static public Set<Tila> julkiset() {
        return Sets.newHashSet(JULKAISTU);
    }

    @Override
    public String toString() {
        return tila;
    }

    public Set<Tila> mahdollisetSiirtymat() {
        return mahdollisetSiirtymat(false);
    }

    public Set<Tila> mahdollisetSiirtymat(boolean isPohja) {
        return EnumSet.noneOf(Tila.class);
    }

    @JsonCreator
    public static Tila of(String tila) {
        for (Tila s : values()) {
            if (s.tila.equalsIgnoreCase(tila)) {
                return s;
            }
        }
        throw new IllegalArgumentException(tila + " ei ole kelvollinen tila");
    }
}
