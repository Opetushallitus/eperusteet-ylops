/*
 *  Copyright (c) 2013 The Finnish Board of Education - Opetushallitus
 *
 *  This program is free software: Licensed under the EUPL, Version 1.1 or - as
 *  soon as they will be approved by the European Commission - subsequent versions
 *  of the EUPL (the "Licence");
 *
 *  You may not use this work except in compliance with the Licence.
 *  You may obtain a copy of the Licence at: http://ec.europa.eu/idabc/eupl
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  European Union Public Licence for more details.
 */
package fi.vm.sade.eperusteet.ylops.domain.lukio;

import fi.vm.sade.eperusteet.ylops.domain.AbstractAuditedReferenceableEntity;
import fi.vm.sade.eperusteet.ylops.domain.teksti.LokalisoituTeksti;
import fi.vm.sade.eperusteet.ylops.domain.validation.ValidHtml;
import fi.vm.sade.eperusteet.ylops.domain.validation.ValidHtml.WhitelistType;
import fi.vm.sade.eperusteet.ylops.service.util.LambdaUtil.Copyable;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Audited
@Table(name = "aihekokonaisuus", schema = "public")
public class Aihekokonaisuus extends AbstractAuditedReferenceableEntity
        implements Copyable<Aihekokonaisuus> {
    @Column(nullable = false, unique = true, updatable = false)
    @Getter
    private UUID tunniste;

    protected Aihekokonaisuus() {
    }

    public Aihekokonaisuus(Aihekokonaisuudet aihekokonaisuudet) {
        this.aihekokonaisuudet = aihekokonaisuudet;
        this.tunniste = UUID.randomUUID();
    }

    public Aihekokonaisuus(Aihekokonaisuudet aihekokonaisuudet, UUID tunniste) {
        this.aihekokonaisuudet = aihekokonaisuudet;
        this.tunniste = tunniste;
    }

    public Aihekokonaisuus(Aihekokonaisuudet aihekokonaisuudet, UUID tunniste, Aihekokonaisuus parent) {
        this(aihekokonaisuudet, tunniste);
        this.parent = parent;
    }

    @Getter
    @Setter
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @ValidHtml(whitelist = WhitelistType.MINIMAL)
    @OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinColumn(name = "otsikko_id", nullable = true)
    private LokalisoituTeksti otsikko;

    @Getter
    @Setter
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinColumn(name = "yleiskuvaus_id")
    private LokalisoituTeksti yleiskuvaus;

    @Getter
    @Setter
    private Long jnro;

    @Getter
    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", nullable = true)
    private Aihekokonaisuus parent;

    @Getter
    @Setter
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE})
    @JoinColumn(name = "aihekokonaisuudet_id", nullable = false)
    private Aihekokonaisuudet aihekokonaisuudet;

    public Aihekokonaisuus copy(Aihekokonaisuudet kokonaisuudet, Aihekokonaisuus parent) {
        return copyInto(new Aihekokonaisuus(kokonaisuudet, this.tunniste, parent));
    }

    public Aihekokonaisuus copyInto(Aihekokonaisuus to) {
        to.tunniste = this.tunniste;
        to.jnro = this.jnro;
        to.otsikko = this.otsikko;
        //to.yleiskuvaus = this.yleiskuvaus;
        return to;
    }
}
