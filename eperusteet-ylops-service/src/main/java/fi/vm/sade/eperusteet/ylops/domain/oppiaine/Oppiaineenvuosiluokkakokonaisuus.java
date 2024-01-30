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
package fi.vm.sade.eperusteet.ylops.domain.oppiaine;

import fi.vm.sade.eperusteet.ylops.domain.AbstractAuditedReferenceableEntity;
import fi.vm.sade.eperusteet.ylops.domain.Vuosiluokka;
import fi.vm.sade.eperusteet.ylops.domain.Vuosiluokkakokonaisuusviite;
import fi.vm.sade.eperusteet.ylops.domain.teksti.Tekstiosa;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * Kuvaa oppimäärän yhteen vuosiluokkakokonaisuuteen osalta.
 *
 * @author jhyoty
 */
@Entity
@Audited
@Table(name = "oppiaineen_vlkok", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"oppiaine_id", "vuosiluokkakokonaisuus_id"})})
public class Oppiaineenvuosiluokkakokonaisuus extends AbstractAuditedReferenceableEntity {

    @Getter
    @Setter(AccessLevel.PACKAGE)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @NotNull
    @JoinColumn(name = "oppiaine_id")
    private Oppiaine oppiaine;

    @Getter
    @Setter
    @NotNull
    @ManyToOne
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @JoinColumn(name = "vuosiluokkakokonaisuus_id")
    private Vuosiluokkakokonaisuusviite vuosiluokkakokonaisuus;

    @Getter
    @Setter
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private Tekstiosa tehtava;

    // Yleistavoitteet ovat tavoitteita, joita ei vuosiluokkaisteta, vaan annetaan vapaana tekstinä.
    @Getter
    @Setter
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private Tekstiosa yleistavoitteet;

    @Getter
    @Setter
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private Tekstiosa tyotavat;

    @Getter
    @Setter
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private Tekstiosa ohjaus;

    @Getter
    @Setter
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private Tekstiosa arviointi;

    @Getter
    @Setter
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "tavoitteista_johdetut_oppimisen_tavoitteet_id")
    private Tekstiosa tavoitteistaJohdetutOppimisenTavoitteet;

    @Getter
    @Setter
    private Integer jnro;

    @Getter
    @Setter
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    private Boolean piilotettu = false;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinTable(name = "oppiaineen_vlkok_oppiaineenvuosiluokka",
            joinColumns = @JoinColumn(name = "oppiaineen_vlkok_id"),
            inverseJoinColumns = @JoinColumn(name = "vuosiluokat_id"))
    @OrderColumn
    @BatchSize(size = 5)
    private Set<Oppiaineenvuosiluokka> vuosiluokat = new HashSet<>();

    public Set<Oppiaineenvuosiluokka> getVuosiluokat() {
        return new HashSet<>(vuosiluokat);
    }

    public void setVuosiluokat(Set<Oppiaineenvuosiluokka> vuosiluokat) {
        if (vuosiluokat == null) {
            this.vuosiluokat.clear();
        } else {
            this.vuosiluokat.addAll(vuosiluokat);
            this.vuosiluokat.retainAll(vuosiluokat);
        }

        if (vuosiluokat != null) {
            for (Oppiaineenvuosiluokka o : vuosiluokat) {
                o.setKokonaisuus(this);
            }
        }
    }

    public void addVuosiluokka(Oppiaineenvuosiluokka vuosiluokka) {
        if (!vuosiluokkakokonaisuus.contains(vuosiluokka.getVuosiluokka())) {
            throw new IllegalArgumentException("Vuosiluokka ei kelpaa");
        }
        vuosiluokka.setKokonaisuus(this);
        this.vuosiluokat.add(vuosiluokka);
    }

    public boolean removeVuosiluokka(Oppiaineenvuosiluokka vuosiluokka) {

        if (this.vuosiluokat.remove(vuosiluokka)) {
            vuosiluokka.setKokonaisuus(null);
            return true;
        }

        return false;
    }

    public Optional<Oppiaineenvuosiluokka> getVuosiluokka(Vuosiluokka luokka) {
        return vuosiluokat.stream()
                .filter(l -> Objects.equals(l.getVuosiluokka(), luokka))
                .findAny();
    }

    static Oppiaineenvuosiluokkakokonaisuus copyOf(final Oppiaineenvuosiluokkakokonaisuus other, Map<Long, Opetuksenkohdealue> kohdealueet) {
        Oppiaineenvuosiluokkakokonaisuus ovk = new Oppiaineenvuosiluokkakokonaisuus();

        ovk.setVuosiluokkakokonaisuus(other.getVuosiluokkakokonaisuus());
        ovk.setArviointi(Tekstiosa.copyOf(other.getArviointi()));
        ovk.setOhjaus(Tekstiosa.copyOf(other.getOhjaus()));
        ovk.setTehtava(Tekstiosa.copyOf(other.getTehtava()));
        ovk.setTyotavat(Tekstiosa.copyOf(other.getTyotavat()));
        ovk.setJnro(other.getJnro());

        other.getVuosiluokat().forEach(vl -> ovk.addVuosiluokka(Oppiaineenvuosiluokka.copyOf(vl, kohdealueet)));

        return ovk;
    }

}
