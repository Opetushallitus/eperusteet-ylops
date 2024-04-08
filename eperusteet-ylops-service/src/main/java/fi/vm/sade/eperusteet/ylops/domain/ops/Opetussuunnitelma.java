package fi.vm.sade.eperusteet.ylops.domain.ops;

import fi.vm.sade.eperusteet.ylops.domain.AbstractAuditedEntity;
import fi.vm.sade.eperusteet.ylops.domain.HistoriaTapahtuma;
import fi.vm.sade.eperusteet.ylops.domain.KoulutusTyyppi;
import fi.vm.sade.eperusteet.ylops.domain.KoulutustyyppiToteutus;
import fi.vm.sade.eperusteet.ylops.domain.ReferenceableEntity;
import fi.vm.sade.eperusteet.ylops.domain.Tila;
import fi.vm.sade.eperusteet.ylops.domain.Tyyppi;
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
import fi.vm.sade.eperusteet.ylops.dto.navigation.NavigationType;
import fi.vm.sade.eperusteet.ylops.service.ops.Identifiable;
import fi.vm.sade.eperusteet.ylops.service.ops.OpsIdentifiable;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.hibernate.envers.RelationTargetAuditMode;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static fi.vm.sade.eperusteet.ylops.service.util.LambdaUtil.orEmpty;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.groupingBy;

@Entity
@Audited
@Table(name = "opetussuunnitelma")
public class Opetussuunnitelma extends AbstractAuditedEntity
        implements Serializable, ReferenceableEntity, OpsIdentifiable, Identifiable, HistoriaTapahtuma {
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

    @Getter
    @Setter
    private boolean tuoPohjanOpintojaksot = false;

    @Getter
    @Setter
    private boolean tuoPohjanOppimaarat = false;

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
    @JoinTable(name = "opetussuunnitelma_liite",
            inverseJoinColumns = {@JoinColumn(name = "liite_id")},
            joinColumns = {@JoinColumn(name = "opetussuunnitelma_id")})
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

    @Temporal(TemporalType.TIMESTAMP)
    @Getter
    @Setter
    @Column(name = "peruste_data_tuonti_pvm")
    private Date perusteDataTuontiPvm;

    @Temporal(TemporalType.TIMESTAMP)
    @Getter
    @Setter
    @Column(name = "viimeisin_sync_pvm")
    private Date viimeisinSyncPvm;

    @NotAudited
    @OneToMany(mappedBy = "opetussuunnitelma", fetch = FetchType.LAZY)
    @Getter
    private List<OpetussuunnitelmanJulkaisu> julkaisut;

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
        } else if (KoulutusTyyppi.PERUSOPETUS.equals(koulutustyyppi)) {
            return KoulutustyyppiToteutus.PERUSOPETUS;
        } else if (toteutus == null && KoulutusTyyppi.LUKIOKOULUTUS.equals(koulutustyyppi)) {
            return KoulutustyyppiToteutus.LOPS;
        } else {
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
    public NavigationType getNavigationType() {
        return NavigationType.opetussuunnitelma;
    }

    public Date getPerusteenVoimassaoloAlkaa() {
        return cachedPeruste != null ? cachedPeruste.getVoimassaoloAlkaa() : null;
    }

    public Date getPerusteenVoimassaoloLoppuu(){
        return cachedPeruste != null ? cachedPeruste.getVoimassaoloLoppuu() : null;
    }

    public void setPerusteenVoimassaoloAlkaa() {}

    public void setPerusteenVoimassaoloLoppuu() {}

    public Date getViimeisinJulkaisuAika() {
        if (CollectionUtils.isNotEmpty(julkaisut)) {
            return julkaisut.stream()
                    .sorted(Comparator.comparing(OpetussuunnitelmanJulkaisu::getLuotu).reversed())
                    .map(OpetussuunnitelmanJulkaisu::getLuotu)
                    .findFirst().get();
        }

        return null;
    }

    public Date getJulkaistu() {
        if (CollectionUtils.isNotEmpty(julkaisut)) {
            return julkaisut.stream().max(Comparator.comparing(OpetussuunnitelmanJulkaisu::getLuotu)).map(OpetussuunnitelmanJulkaisu::getLuotu).orElse(null);
        }
        return null;
    }

    public Date getEnsijulkaisu() {
        if (CollectionUtils.isNotEmpty(julkaisut)) {
            return julkaisut.stream().min(Comparator.comparing(OpetussuunnitelmanJulkaisu::getLuotu)).map(OpetussuunnitelmanJulkaisu::getLuotu).orElse(null);
        }
        return null;
    }
}
