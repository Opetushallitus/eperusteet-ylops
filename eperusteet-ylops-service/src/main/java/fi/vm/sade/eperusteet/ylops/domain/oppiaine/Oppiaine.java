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
import fi.vm.sade.eperusteet.ylops.domain.Tila;
import fi.vm.sade.eperusteet.ylops.domain.lukio.LukiokurssiTyyppi;
import fi.vm.sade.eperusteet.ylops.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.ylops.domain.teksti.LokalisoituTeksti;
import fi.vm.sade.eperusteet.ylops.domain.teksti.Tekstiosa;
import fi.vm.sade.eperusteet.ylops.domain.validation.ValidHtml;
import fi.vm.sade.eperusteet.ylops.service.util.LambdaUtil.ConstructedCopier;
import fi.vm.sade.eperusteet.ylops.service.util.LambdaUtil.Copier;
import fi.vm.sade.eperusteet.ylops.service.util.LambdaUtil.Copyable;
import fi.vm.sade.eperusteet.ylops.service.util.Validointi;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;

/**
 *
 * @author jhyoty
 */
@Entity
@Audited
@Table(name = "oppiaine")
public class Oppiaine extends AbstractAuditedReferenceableEntity implements Copyable<Oppiaine> {

    @Getter
    @NotNull
    @Column(updatable = false)
    private UUID tunniste;

    @Getter
    @Setter
    private String koodi;

    @Enumerated(value = EnumType.STRING)
    @NotNull
    @Getter
    @Setter
    private OppiaineTyyppi tyyppi = OppiaineTyyppi.YHTEINEN;

    /**
     *
     * Laajuus vuosiviikkotunteina (vvh)
     */
    @Getter
    @Setter
    private String laajuus;

    @Getter
    @Setter
    @Column(name = "koodi_arvo")
    private String koodiArvo;

    @Getter
    @Setter
    @Column(name = "koodi_uri")
    private String koodiUri;

    @Getter
    @Setter
    @Column(name = "kieli_koodi_uri")
    private String kieliKoodiUri;

    @Getter
    @Setter
    @Column(name = "kieli_koodi_arvo")
    private String kieliKoodiArvo;

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @Getter
    @Setter
    @NotNull(groups = Strict.class)
    @ValidHtml(whitelist = ValidHtml.WhitelistType.MINIMAL)
    private LokalisoituTeksti kieli;

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @Getter
    @Setter
    @NotNull(groups = Strict.class)
    @ValidHtml(whitelist = ValidHtml.WhitelistType.MINIMAL)
    private LokalisoituTeksti nimi;

    @Getter
    @Setter
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private Tekstiosa tehtava;

    @Getter
    @Setter
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private Tekstiosa tavoitteet;

    @Getter
    @Setter
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private Tekstiosa arviointi;

    @Getter
    @Setter
    @ValidHtml
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinColumn(name = "valtakunnallinen_pakollinen_kuvaus_id", nullable = true)
    private LokalisoituTeksti valtakunnallinenPakollinenKuvaus;

    @Getter
    @Setter
    @ValidHtml
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinColumn(name = "valtakunnallinen_syventava_kuvaus_id", nullable = true)
    private LokalisoituTeksti valtakunnallinenSyventavaKurssiKuvaus;

    @Getter
    @Setter
    @ValidHtml
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinColumn(name = "valtakunnallinen_soveltava_kuvaus_id", nullable = true)
    private LokalisoituTeksti valtakunnallinenSoveltavaKurssiKuvaus;

    @Getter
    @Setter
    @ValidHtml
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinColumn(name = "paikallinen_syventava_kuvaus_id", nullable = true)
    private LokalisoituTeksti paikallinenSyventavaKurssiKuvaus;

    @Getter
    @Setter
    @ValidHtml
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinColumn(name = "paikallinen_soveltava_kuvaus_id", nullable = true)
    private LokalisoituTeksti paikallinenSoveltavaKurssiKuvaus;

    @OneToMany(mappedBy = "oppiaine", cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REMOVE}, fetch = FetchType.LAZY, orphanRemoval = true)
    @NotNull(groups = Strict.class)
    @Size(min = 1, groups = Strict.class)
    @Valid
    @BatchSize(size = 3)
    private Set<Oppiaineenvuosiluokkakokonaisuus> vuosiluokkakokonaisuudet;

    @Getter
    @ManyToOne(optional = true, fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST})
    /**
     * oppiaine johon oppimäärä kuuluu tai null jos kyseessä itse oppiaine.
     */
    private Oppiaine oppiaine;

    /**
     * kertoo koostuuko oppiaine oppimääristä (esim. äidinkieli ja kirjallisuus) vai onko se "yksinkertainen" kuten matematiikka.
     */
    @Getter
    private boolean koosteinen = false;

    @Getter
    @Setter
    private Boolean abstrakti;

    @OneToMany(mappedBy = "oppiaine", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @BatchSize(size = 5)
    private Set<Oppiaine> oppimaarat = new HashSet<>(0);

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable
    private Set<Opetuksenkohdealue> kohdealueet = new HashSet<>();

    @Enumerated(value = EnumType.STRING)
    @NotNull
    @Getter
    private Tila tila = Tila.LUONNOS;

    public Oppiaine(UUID tunniste) {
        this.tunniste = tunniste;
    }

    public Oppiaine(OppiaineTyyppi tyyppi) {
        if (tyyppi != OppiaineTyyppi.YHTEINEN) {
            this.tunniste = UUID.randomUUID();
        } else {
            throw new IllegalArgumentException("Oppiaine ei ole valinnainen");
        }
    }

    protected Oppiaine() {
        //for JPA
    }

    /**
     * Palauttaa oppimäärät jos kyseessä on koosteinen oppiaine.
     *
     * @return oppimäärät (joukkoa ei voi muokata) tai null jos oppiaine ei ole koosteinen
     */
    public Set<Oppiaine> getOppimaarat() {
        if (!koosteinen) {
            return null;
        }
        return oppimaarat == null ? new HashSet<>() : new HashSet<>(oppimaarat);
    }

    public Set<Oppiaine> getOppimaaratReal() {
        return oppimaarat;
    }

    public Set<Oppiaineenvuosiluokkakokonaisuus> getVuosiluokkakokonaisuudet() {
        return vuosiluokkakokonaisuudet == null ? Collections.emptySet()
            : Collections.unmodifiableSet(vuosiluokkakokonaisuudet);
    }

    public void addVuosiluokkaKokonaisuus(Oppiaineenvuosiluokkakokonaisuus ovk) {
        if (vuosiluokkakokonaisuudet == null) {
            vuosiluokkakokonaisuudet = new HashSet<>();
        }


        ovk.setOppiaine(this);
        if (vuosiluokkakokonaisuudet.add(ovk)) {
            this.muokattu();
        }
    }

    public void setKoosteinen(boolean koosteinen) {
        if (koosteinen && this.oppiaine != null) {
            throw new IllegalStateException("Oppimäärä ei voi olla koosteinen");
        }
        this.koosteinen = koosteinen;
    }

    public void removeVuosiluokkaKokonaisuus(Oppiaineenvuosiluokkakokonaisuus ovk) {
        if (!ovk.getOppiaine().equals(this)) {
            throw new IllegalArgumentException("Vuosiluokkakokonaisuus ei kuulu tähän oppiaineeseen");
        }
        vuosiluokkakokonaisuudet.remove(ovk);
        ovk.setOppiaine(null);
    }

    public void addOppimaara(Oppiaine oppimaara) {
        if (!koosteinen) {
            throw new IllegalStateException("Oppiaine ei ole koosteinen eikä tue oppimääriä");
        }
        if (oppimaarat == null) {
            oppimaarat = new HashSet<>();
        }
        oppimaara.setOppiaine(this);
        if (oppimaarat.add(oppimaara)) {
            this.muokattu();
        }
    }

    public void removeOppimaara(Oppiaine aine) {
        if (!koosteinen) {
            throw new IllegalStateException("Oppiaine ei ole koosteinen eikä tue oppimääriä");
        }
        if (aine.getOppiaine().equals(this) && oppimaarat.remove(aine)) {
            aine.oppiaine = null;
        } else {
            throw new IllegalArgumentException("Oppimäärä ei kuulu tähän oppiaineeseen");
        }
    }

    public void setOppiaine(Oppiaine oppiaine) {
        if (this.oppiaine == null || this.oppiaine.equals(oppiaine)) {
            this.oppiaine = oppiaine;
        } else {
            throw new IllegalStateException("Oppiaineviittausta ei voi muuttaa");
        }
    }

    public Set<Opetuksenkohdealue> getKohdealueet() {
        return new HashSet<>(kohdealueet);
    }

    public void setKohdealueet(Set<Opetuksenkohdealue> kohdealueet) {
        if (kohdealueet == null) {
            this.kohdealueet.clear();
        } else {
            Set<Opetuksenkohdealue> added = new HashSet<>(kohdealueet.size());
            //kohdealueita ei ole paljon (<10), joten O(n^2) OK tässä
            for (Opetuksenkohdealue k : kohdealueet) {
                added.add(addKohdealue(k));
            }
            //TODO: tarkista onko jokin poistettava kohdealue käytössä
            this.kohdealueet.retainAll(added);
        }
    }

    /**
     * Lisää uuden kohdealueen. Jos samanniminen kohdealue on jo olemassa, palauttaa tämän.
     *
     * @param kohdealue
     * @return Lisätty kohdealue tai samanniminen olemassa oleva.
     */
    public Opetuksenkohdealue addKohdealue(Opetuksenkohdealue kohdealue) {
        for (Opetuksenkohdealue k : kohdealueet) {
            if (Objects.equals(k.getNimi(), kohdealue.getNimi())) {
                return k;
            }
        }
        this.kohdealueet.add(kohdealue);
        return kohdealue;
    }

    public void removeKohdealue(Opetuksenkohdealue kohdealue) {
        this.kohdealueet.remove(kohdealue);
    }

    //hiberate javaassist proxy "workaround"
    //ilman equals-metodia objectX.equals(proxy-objectX) on aina false
    @Override
    public boolean equals(Object other) {
        return this == other;
    }

    @Override
    public int hashCode() {
        return System.identityHashCode(this);
    }

    public interface Strict {
    }

    public static Oppiaine copyOf(Oppiaine other) {
        return copyOf(other, true);
    }

    public static Oppiaine copyOf(Oppiaine other, boolean copyOppimaarat) {
        Copier<Oppiaine> copier = basicCopier().and(perusopetusCopier());
        if (copyOppimaarat) {
            copier = copier.and(oppimaaraCopier(om -> copyOf(om, true)));
        }
        return copier.copied(other, new Oppiaine(other.getTunniste()));
    }

    @Override
    public Oppiaine copyInto(Oppiaine to) {
        return basicCopier().copied(this, to);
    }

    public static Copier<Oppiaine> basicCopier() {
        return (other, to) -> {
            to.setNimi(other.getNimi());
            to.setTehtava(Tekstiosa.copyOf(other.getTehtava()));
            to.setKoodi(other.getKoodi());
            to.setKoosteinen(other.isKoosteinen());
            to.setKoodiArvo(other.getKoodiArvo());
            to.setKoodiUri(other.getKoodiUri());
            to.setTavoitteet(Tekstiosa.copyOf(other.getTavoitteet()));
            to.setArviointi(Tekstiosa.copyOf(other.getArviointi()));
            to.setKieliKoodiArvo(other.getKieliKoodiArvo());
            to.setKieliKoodiUri(other.getKieliKoodiUri());
            to.setKieli(other.getKieli());
            for (LukiokurssiTyyppi tyyppi : LukiokurssiTyyppi.values()) {
                tyyppi.oppiaineKuvausCopier().copy(other, to);
            }
        };
    }

    public static Copier<Oppiaine> perusopetusCopier() {
        return (other, o) -> {
            Map<Long, Opetuksenkohdealue> kohdealueet = other.getKohdealueet().stream()
                    .collect(Collectors.toMap(ka -> ka.getId(), ka -> new Opetuksenkohdealue(ka.getNimi())));
            o.setKohdealueet(new HashSet<>(kohdealueet.values()));
            other.getVuosiluokkakokonaisuudet().forEach((vk -> {
                Oppiaineenvuosiluokkakokonaisuus ovk = Oppiaineenvuosiluokkakokonaisuus.copyOf(vk, kohdealueet);
                o.addVuosiluokkaKokonaisuus(ovk);
            }));
        };
    }

    public static Copier<Oppiaine> oppimaaraCopier(ConstructedCopier<Oppiaine> with) {
        return oppimaaraCopier(any -> true, with);
    }

    public static Copier<Oppiaine> oppimaaraCopier(Predicate<Oppiaine> oppimaaraFilter, ConstructedCopier<Oppiaine> with) {
        return (other, o) -> {
            if (other.isKoosteinen() && other.getOppiaine() == null) {
                other.getOppimaarat().stream().filter(oppimaaraFilter).forEach((om -> {
                    o.addOppimaara(with.copy(om));
                }));
            }
        };
    }

    static public void validoi(Validointi validointi, Oppiaine oa, Set<Kieli> kielet) {
        LokalisoituTeksti.validoi(validointi, kielet, oa.getNimi());

        /*for (Oppiaineenvuosiluokkakokonaisuus ovlk : oa.getVuosiluokkakokonaisuudet()) {
            Oppiaineenvuosiluokkakokonaisuus.validoi(validointi, ovlk, kielet);
        }*/

        if (oa.getOppimaarat() != null) {
            for (Oppiaine om : oa.getOppimaarat()) {
                validoi(validointi, om, kielet);
            }
        }
    }

    public Stream<Oppiaine> maarineen() {
        return Stream.concat(Stream.of(this), oppimaarat.stream());
    }

    @Transient
    public Map<LukiokurssiTyyppi, Optional<LokalisoituTeksti>> getKurssiTyyppiKuvaukset() {
        return Stream.of(LukiokurssiTyyppi.values()).collect(toMap(k -> k,
            k -> Optional.ofNullable(k.oppiaineKuvausGetter().apply(this))
        ));
    }

    public boolean isAbstraktiBool() {
        return abstrakti != null && abstrakti;
    }

    @Transient
    public OppiaineOpsTunniste getOpsUniikkiTunniste() {
        return new OppiaineOpsTunniste(this.tunniste, this.kieliKoodiArvo, this.kieli);
    }
}
