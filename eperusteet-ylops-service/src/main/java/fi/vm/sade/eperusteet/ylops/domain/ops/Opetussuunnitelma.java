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

import fi.vm.sade.eperusteet.ylops.domain.*;
import fi.vm.sade.eperusteet.ylops.domain.cache.PerusteCache;
import fi.vm.sade.eperusteet.ylops.domain.koodisto.KoodistoKoodi;
import fi.vm.sade.eperusteet.ylops.domain.liite.Liite;
import fi.vm.sade.eperusteet.ylops.domain.lops2019.Lops2019Sisalto;
import fi.vm.sade.eperusteet.ylops.domain.lukio.Aihekokonaisuudet;
import fi.vm.sade.eperusteet.ylops.domain.lukio.LukioOppiaineJarjestys;
import fi.vm.sade.eperusteet.ylops.domain.lukio.OpetuksenYleisetTavoitteet;
import fi.vm.sade.eperusteet.ylops.domain.lukio.OppiaineLukiokurssi;
import fi.vm.sade.eperusteet.ylops.domain.oppiaine.Oppiaine;
import fi.vm.sade.eperusteet.ylops.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.ylops.domain.teksti.LokalisoituTeksti;
import fi.vm.sade.eperusteet.ylops.domain.teksti.PoistettuTekstiKappale;
import fi.vm.sade.eperusteet.ylops.domain.teksti.TekstiKappaleViite;
import fi.vm.sade.eperusteet.ylops.domain.validation.ValidHtml;
import fi.vm.sade.eperusteet.ylops.domain.vuosiluokkakokonaisuus.Vuosiluokkakokonaisuus;
import static fi.vm.sade.eperusteet.ylops.service.util.LambdaUtil.orEmpty;

import fi.vm.sade.eperusteet.ylops.dto.navigation.NavigationType;
import java.io.Serializable;
import java.util.*;
import static java.util.Comparator.comparing;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.groupingBy;
import javax.persistence.*;
import javax.validation.constraints.NotNull;

import fi.vm.sade.eperusteet.ylops.dto.lops2019.Validointi.ValidointiContext;
import fi.vm.sade.eperusteet.ylops.dto.lops2019.Validointi.ValidointiDto;
import fi.vm.sade.eperusteet.ylops.service.ops.Identifiable;
import fi.vm.sade.eperusteet.ylops.service.ops.OpsIdentifiable;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;
import org.springframework.util.StringUtils;

/**
 * @author mikkom
 */
@Entity
@Audited
@Table(name = "opetussuunnitelma")
public class Opetussuunnitelma extends AbstractAuditedEntity
        implements Serializable, ReferenceableEntity, Validable, OpsIdentifiable, Identifiable, HistoriaTapahtuma {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Getter
    @Setter
    private Long id;

    @Getter
    @Setter
    @NotNull
    private String perusteenDiaarinumero;

    @Getter
    @Setter
    private String hyvaksyjataho;

    @Getter
    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @JoinColumn(name = "cached_peruste")
    private PerusteCache cachedPeruste;

    @ValidHtml(whitelist = ValidHtml.WhitelistType.MINIMAL)
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @Getter
    @Setter
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    private LokalisoituTeksti nimi;

    @ValidHtml(whitelist = ValidHtml.WhitelistType.SIMPLIFIED)
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @Getter
    @Setter
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    private LokalisoituTeksti kuvaus;

    @Enumerated(value = EnumType.STRING)
    @NotNull
    @Getter
    @Setter
    private Tila tila = Tila.LUONNOS;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @Getter
    @Setter
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    private Opetussuunnitelma pohja;

    @Enumerated(value = EnumType.STRING)
    @NotNull
    @Getter
    @Setter
    private Tyyppi tyyppi = Tyyppi.OPS;

    @Getter
    @Setter
    private boolean esikatseltavissa = false;

    @Enumerated(value = EnumType.STRING)
    @Getter
    @Setter
    private KoulutusTyyppi koulutustyyppi;

    @Enumerated(value = EnumType.STRING)
    private KoulutustyyppiToteutus toteutus;

    @Temporal(TemporalType.TIMESTAMP)
    @Getter
    @Setter
    private Date paatospaivamaara;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @Getter
    @Setter
    @JoinColumn
    private TekstiKappaleViite tekstit = new TekstiKappaleViite();

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @Getter
    @Setter
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    private Set<KoodistoKoodi> kunnat = new HashSet<>();

    @ElementCollection
    @Getter
    @Setter
    private Set<String> organisaatiot = new HashSet<>();

    @ElementCollection
    @Getter
    @Setter
    @Enumerated(EnumType.STRING)
    @NotNull
    private Set<Kieli> julkaisukielet = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "opetussuunnitelma_liite", inverseJoinColumns = {@JoinColumn(name = "liite_id")}, joinColumns = {@JoinColumn(name = "opetussuunnitelma_id")})
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    private Set<Liite> liitteet = new HashSet<>();


    // FIXME: vanhat toteutuskohtaiset sisällöt mitkä eivät kuuluisi tähän entiteettiin
    // --------------------------------------------------

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(joinColumns = {
            @JoinColumn(name = "opetussuunnitelma_id")}, name = "ops_oppiaine")
    private Set<OpsOppiaine> oppiaineet = new HashSet<>();

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(joinColumns = {
            @JoinColumn(name = "opetussuunnitelma_id")}, name = "ops_vuosiluokkakokonaisuus")
    private Set<OpsVuosiluokkakokonaisuus> vuosiluokkakokonaisuudet = new HashSet<>();

    @Getter
    @Audited
    @OneToMany(mappedBy = "opetussuunnitelma", fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private Set<OppiaineLukiokurssi> lukiokurssit = new HashSet<>(0);

    @Getter
    @Audited
    @OneToMany(mappedBy = "opetussuunnitelma", fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private Set<LukioOppiaineJarjestys> oppiaineJarjestykset = new HashSet<>();

    @Getter
    @Setter
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "opetussuunnitelma",
            cascade = {CascadeType.MERGE, CascadeType.PERSIST}, orphanRemoval = true)
    private Aihekokonaisuudet aihekokonaisuudet;

    @Getter
    @Setter
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "opetussuunnitelma",
            cascade = {CascadeType.MERGE, CascadeType.PERSIST}, orphanRemoval = true)
    private OpetuksenYleisetTavoitteet opetuksenYleisetTavoitteet;

    // --------------------------------------------------

    @Getter
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "opetussuunnitelma",
            cascade = {CascadeType.ALL}, orphanRemoval = true)
    private Lops2019Sisalto lops2019;

    @Getter
    @Audited
    @OneToMany(mappedBy = "opetussuunnitelma", fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private Set<PoistettuTekstiKappale> poistetutTekstiKappaleet = new HashSet<>();

    @Getter
    @Setter
    @Column(name = "ryhmaoid")
    private String ryhmaOid;

    @Getter
    @Setter
    @Column(name = "ryhman_nimi")
    private String ryhmanNimi;

    @Getter
    @Setter
    private boolean ainepainoitteinen;

    public void addVuosiluokkaKokonaisuus(Vuosiluokkakokonaisuus vk) {
        vuosiluokkakokonaisuudet.add(new OpsVuosiluokkakokonaisuus(vk, true));
    }

    public void attachLiite(Liite liite) {
        liitteet.add(liite);
    }

    public void removeLiite(Liite liite) {
        liitteet.remove(liite);
    }

    public void attachVuosiluokkaKokonaisuus(Vuosiluokkakokonaisuus vk) {
        vuosiluokkakokonaisuudet.add(new OpsVuosiluokkakokonaisuus(vk, false));
    }

    public boolean containsViite(TekstiKappaleViite viite) {
        return viite != null && tekstit.getId().equals(viite.getRoot().getId());
    }

    public Set<OpsOppiaine> getOppiaineet() {
        return new HashSet<>(oppiaineet);
    }

    public Set<OpsOppiaine> getOppiaineetReal() {
        return oppiaineet;
    }

    public void setOppiaineet(Set<OpsOppiaine> oppiaineet) {
        if (oppiaineet == null) {
            this.oppiaineet.clear();
        } else {
            this.oppiaineet.addAll(oppiaineet);
            this.oppiaineet.retainAll(oppiaineet);
        }
    }

    public void addOppiaine(Oppiaine oppiaine) {
        if (oppiaine.getOppiaine() != null) {
            // Oppimäärä
            if (containsOppiaine(oppiaine.getOppiaine())) {
                oppiaine.getOppiaine().addOppimaara(oppiaine);
            } else {
                throw new IllegalArgumentException("Ei voida lisätä oppimäärää jonka oppiaine ei kuulu sisältöön");
            }
        } else {
            // Simppeli oppiaine
            oppiaineet.add(new OpsOppiaine(oppiaine, true));
        }
    }

    public void removeOppiaine(Oppiaine oppiaine) {
        List<OpsOppiaine> poistettavat = oppiaineet.stream()
                .filter(opsOppiaine -> opsOppiaine.getOppiaine().equals(oppiaine))
                .collect(Collectors.toList());
        for (OpsOppiaine opsOppiaine : poistettavat) {
            // TODO: Tarkista onko opsOppiaineen alla oleva oppiaine enää käytössä missään
            oppiaineet.remove(opsOppiaine);
        }
    }

    public boolean containsOppiaine(Oppiaine oppiaine) {
        if (oppiaine == null) {
            return false;
        }

        if (oppiaine.getOppiaine() != null) {
            return containsOppiaine(oppiaine.getOppiaine());
        }

        return oppiaineet.stream()
                .anyMatch(opsOppiaine -> opsOppiaine.getOppiaine().equals(oppiaine));
    }

    public Set<OpsVuosiluokkakokonaisuus> getVuosiluokkakokonaisuudet() {
        return new HashSet<>(vuosiluokkakokonaisuudet);
    }

    public void setVuosiluokkakokonaisuudet(Set<OpsVuosiluokkakokonaisuus> vuosiluokkakokonaisuudet) {
        if (vuosiluokkakokonaisuudet == null) {
            this.vuosiluokkakokonaisuudet.clear();
        } else {
            this.vuosiluokkakokonaisuudet.addAll(vuosiluokkakokonaisuudet);
            this.vuosiluokkakokonaisuudet.retainAll(vuosiluokkakokonaisuudet);
        }
    }

    public boolean removeVuosiluokkakokonaisuus(Vuosiluokkakokonaisuus vk) {
        return vuosiluokkakokonaisuudet.remove(new OpsVuosiluokkakokonaisuus(vk, false));
    }

    public Opetussuunnitelma getAlinPohja() {
        Opetussuunnitelma pohjin = this;
        while (pohjin.getPohja() != null && !Objects.equals(pohjin.getPohja().getId(), pohjin.getId())) {
            pohjin = pohjin.getPohja();
        }
        return pohjin;
    }

    public Function<Long, List<OppiaineLukiokurssi>> lukiokurssitByOppiaine() {
        return orEmpty(this.getLukiokurssit().stream()
                .sorted(comparing((OppiaineLukiokurssi oaLk) -> Optional.ofNullable(oaLk.getJarjestys()).orElse(0))
                        .thenComparing((OppiaineLukiokurssi oaLk) -> oaLk.getKurssi().getNimi().firstByKieliOrder().orElse("")))
                .collect(groupingBy(k -> k.getOppiaine().getId()))::get);
    }

    @Transient // ei pitäisi käyttää pääosin (raskas, tässä vain erikoistapaukseen)
    public Oppiaine findOppiaine(Long id) {
        return oppiaineet.stream().flatMap(opsOa -> opsOa.getOppiaine().maarineen())
                .filter(oa -> oa.getId().equals(id)).findFirst().orElse(null);
    }

    @Transient
    public Optional<Oppiaine> findYlatasonOppiaine(Predicate<Oppiaine> predicate, Predicate<OpsOppiaine> filter) {
        return oppiaineet.stream().filter(filter).map(OpsOppiaine::getOppiaine)
                .filter(predicate).findFirst();
    }

    public KoulutustyyppiToteutus getToteutus() {
        if (koulutustyyppi == null || koulutustyyppi.isYksinkertainen()) {
            return KoulutustyyppiToteutus.YKSINKERTAINEN;
        }
        else if (KoulutusTyyppi.PERUSOPETUS.equals(koulutustyyppi)) {
            return KoulutustyyppiToteutus.PERUSOPETUS;
        }
        else {
            return toteutus;
        }
    }

    public void setLops2019(Lops2019Sisalto lops2019) {
        if (this.lops2019 == null) {
            this.lops2019 = lops2019;
        }
    }

    public void setToteutus(KoulutustyyppiToteutus toteutus) {
        if (this.toteutus == null) {
            this.toteutus = toteutus;
        }
    }

    @Override
    public void validate(ValidointiDto validointi, ValidointiContext ctx) {
        validointi.virhe("vahintaan-yksi-julkaisukieli", this, getJulkaisukielet().isEmpty());
        validointi.virhe("perusteen-diaarinumero-puuttuu", this, getPerusteenDiaarinumero().isEmpty());
        validointi.virhe("nimi-oltava-kaikilla-julkaisukielilla", this, getNimi() == null || !getNimi().hasKielet(ctx.getKielet()));
        validointi.varoitus("kuvausta-ei-ole-kirjoitettu-kaikilla-julkaisukielilla", this, getNimi() == null || !getNimi().hasKielet(ctx.getKielet()));

        if (getTyyppi() == Tyyppi.OPS) {
            validointi.virhe("hyvaksyjataho-puuttuu", this, StringUtils.isEmpty(getHyvaksyjataho()));
            validointi.virhe("paatospaivamaaraa-ei-ole-asetettu", this, getPaatospaivamaara() == null);
        }
    }

    @Override
    public ValidationCategory category() {
        return ValidationCategory.OPETUSSUUNNITELMA;
    }

    @Override
    public NavigationType getNavigationType() {
        return NavigationType.opetussuunnitelma;
    }
}
