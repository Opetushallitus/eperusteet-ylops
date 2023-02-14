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
package fi.vm.sade.eperusteet.ylops.domain.ops;

import fi.vm.sade.eperusteet.ylops.domain.vuosiluokkakokonaisuus.Vuosiluokkakokonaisuus;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Opetussuunnitelman oppiaine
 *
 * @author jhyoty
 */
@Embeddable
@EqualsAndHashCode(of = "vuosiluokkakokonaisuus")
public class OpsVuosiluokkakokonaisuus implements Serializable {

    @Getter
    @Setter
    @ManyToOne(optional = false, cascade = {CascadeType.PERSIST})
    @NotNull
    private Vuosiluokkakokonaisuus vuosiluokkakokonaisuus;

    /**
     * Ilmaisee onko vuosiluokkakokonaisuus oma vai lainattu. Vain omaa vuosiluokkakokonaisuutta
     * voidaan muokata, lainatusta kokonaisuudesta täytyy ensin tehdä oma kopio ennen kuin
     * muokkaus on mahdollista.
     */
    @Getter
    @Setter
    @Column(updatable = false)
    private boolean oma;

    @Getter
    @Setter
    @OneToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST}, orphanRemoval = true)
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    private OpsVuosiluokkakokonaisuusLisatieto lisatieto;

    protected OpsVuosiluokkakokonaisuus() {
        //JPA
    }

    public OpsVuosiluokkakokonaisuus(Vuosiluokkakokonaisuus vuosiluokkakokonaisuus, boolean oma) {
        this.oma = oma;
        this.vuosiluokkakokonaisuus = vuosiluokkakokonaisuus;
    }

}
