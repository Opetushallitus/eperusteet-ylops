/*
 * Copyright (c) 2013 The Finnish Board of Education - Opetushallitus
 *
 * This program is free software: Licensed under the EUPL, Version 1.1 or - as
 * soon as they will be approved by the European Commission - subsequent versions
 * of the EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at: http://ec.europa.eu/idabc/eupl
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * European Union Public Licence for more details.
 */
package fi.vm.sade.eperusteet.ylops.domain;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

/**
 * @author mikkom
 */
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
            return EnumSet.of(LUONNOS, POISTETTU);
        }
    },
    JULKAISTU("julkaistu") {
        @Override
        public Set<Tila> mahdollisetSiirtymat(boolean isPohja) {
            return EnumSet.of(LUONNOS);
        }
    };

    private final String tila;

    private Tila(String tila) {
        this.tila = tila;
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
}
