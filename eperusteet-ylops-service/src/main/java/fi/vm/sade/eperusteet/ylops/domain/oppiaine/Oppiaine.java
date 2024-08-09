package fi.vm.sade.eperusteet.ylops.domain.oppiaine;

import fi.vm.sade.eperusteet.ylops.domain.AbstractAuditedReferenceableEntity;
import fi.vm.sade.eperusteet.ylops.domain.AbstractReferenceableEntity;
import fi.vm.sade.eperusteet.ylops.domain.HistoriaTapahtuma;
import fi.vm.sade.eperusteet.ylops.domain.Poistettava;
import fi.vm.sade.eperusteet.ylops.domain.Tila;
import fi.vm.sade.eperusteet.ylops.domain.lops2019.PoistetunTyyppi;
import fi.vm.sade.eperusteet.ylops.domain.lukio.LukiokurssiTyyppi;
import fi.vm.sade.eperusteet.ylops.domain.teksti.LokalisoituTeksti;
import fi.vm.sade.eperusteet.ylops.domain.teksti.Tekstiosa;
import fi.vm.sade.eperusteet.ylops.domain.validation.ValidHtml;
import fi.vm.sade.eperusteet.ylops.dto.navigation.NavigationType;
import fi.vm.sade.eperusteet.ylops.service.util.LambdaUtil.ConstructedCopier;
import fi.vm.sade.eperusteet.ylops.service.util.LambdaUtil.Copier;
import fi.vm.sade.eperusteet.ylops.service.util.LambdaUtil.Copyable;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;

@Entity
@Audited
@Table(name = "oppiaine")
public class Oppiaine extends AbstractAuditedReferenceableEntity implements Copyable<Oppiaine>, HistoriaTapahtuma, Poistettava {

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

    @Enumerated(value = EnumType.STRING)
    @NotNull
    @Getter
    @Setter
    @Column(name = "valinnainen_tyyppi")
    private OppiaineValinnainenTyyppi valinnainenTyyppi = OppiaineValinnainenTyyppi.EI_MAARITETTY;

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinColumn(name = "liittyva_oppiaine_id")
    @Getter
    @Setter
    private Oppiaine liittyvaOppiaine;

    @Deprecated
    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinColumn(name = "pohjan_oppiaine_id")
    @Getter
    private Oppiaine pohjanOppiaine;

    /**
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
    @JoinColumn(name = "valtakunnallinen_pakollinen_kuvaus_id")
    private LokalisoituTeksti valtakunnallinenPakollinenKuvaus;

    @Getter
    @Setter
    @ValidHtml
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinColumn(name = "valtakunnallinen_syventava_kuvaus_id")
    private LokalisoituTeksti valtakunnallinenSyventavaKurssiKuvaus;

    @Getter
    @Setter
    @ValidHtml
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinColumn(name = "valtakunnallinen_soveltava_kuvaus_id")
    private LokalisoituTeksti valtakunnallinenSoveltavaKurssiKuvaus;

    @Getter
    @Setter
    @ValidHtml
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinColumn(name = "paikallinen_syventava_kuvaus_id")
    private LokalisoituTeksti paikallinenSyventavaKurssiKuvaus;

    @Getter
    @Setter
    @ValidHtml
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinColumn(name = "paikallinen_soveltava_kuvaus_id")
    private LokalisoituTeksti paikallinenSoveltavaKurssiKuvaus;

    @OneToMany(mappedBy = "oppiaine", cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REMOVE}, fetch = FetchType.LAZY, orphanRemoval = true)
    @NotNull(groups = Strict.class)
    @Size(min = 1, groups = Strict.class)
    @Valid
    private Set<Oppiaineenvuosiluokkakokonaisuus> vuosiluokkakokonaisuudet;

    /**
     * oppiaine johon oppimäärä kuuluu tai null jos kyseessä itse oppiaine.
     */
    @Getter
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST})
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

    @Getter
    @OneToMany(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST}, orphanRemoval = true)
    @JoinTable(name = "oppiaine_vapaatekstit",
            joinColumns = @JoinColumn(name = "oppiaine_id"),
            inverseJoinColumns = @JoinColumn(name = "vapaateksti_paikallinentarkennus_id"))
    private List<VapaatekstiPaikallinentarkennus> vapaatTekstit = new ArrayList<>();

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

    public void setVapaatTekstit(List<VapaatekstiPaikallinentarkennus> vapaatTekstit) {
        this.vapaatTekstit.clear();
        if (vapaatTekstit != null) {
            this.vapaatTekstit.addAll(vapaatTekstit);
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

    public Optional<Oppiaineenvuosiluokkakokonaisuus> getVuosiluokkakokonaisuus(UUID tunniste) {
        return vuosiluokkakokonaisuudet.stream()
                .filter(v -> v.getVuosiluokkakokonaisuus().getId().equals(tunniste))
                .findAny();
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

    public void asetaPohjanOppiaine(Oppiaine pohjanOppiaine) {
        this.pohjanOppiaine = pohjanOppiaine;
        setTehtava(new Tekstiosa());
        setTavoitteet(new Tekstiosa());
        setArviointi(new Tekstiosa());
        if (vuosiluokkakokonaisuudet != null) {
            vuosiluokkakokonaisuudet.forEach(vlk -> {
                vlk.setArviointi(new Tekstiosa());
                vlk.setOhjaus(new Tekstiosa());
                vlk.setTavoitteistaJohdetutOppimisenTavoitteet(new Tekstiosa());
                vlk.setTehtava(new Tekstiosa());
                vlk.setTyotavat(new Tekstiosa());
                vlk.setYleistavoitteet(new Tekstiosa());
            });
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

    @Override
    public NavigationType getNavigationType() {
        return NavigationType.perusopetusoppiaine;
    }

    @Override
    public PoistetunTyyppi getPoistetunTyyppi() {
        return PoistetunTyyppi.OPPIAINE;
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
            to.setTyyppi(other.getTyyppi());
            to.setValinnainenTyyppi(other.getValinnainenTyyppi());
            to.setKoodi(other.getKoodi());
            to.setKoosteinen(other.isKoosteinen());
            to.setKoodiArvo(other.getKoodiArvo());
            to.setKoodiUri(other.getKoodiUri());
            to.setTavoitteet(Tekstiosa.copyOf(other.getTavoitteet()));
            to.setArviointi(Tekstiosa.copyOf(other.getArviointi()));
            to.setKieliKoodiArvo(other.getKieliKoodiArvo());
            to.setKieliKoodiUri(other.getKieliKoodiUri());
            to.setKieli(other.getKieli());
            to.setLaajuus(other.getLaajuus());

            for (LukiokurssiTyyppi tyyppi : LukiokurssiTyyppi.values()) {
                tyyppi.oppiaineKuvausCopier().copy(other, to);
            }
        };
    }

    public static Copier<Oppiaine> viitteellaCopier() {
        return (other, to) -> {
            to.setTehtava(new Tekstiosa());
            to.setTavoitteet(new Tekstiosa());
            to.setArviointi(new Tekstiosa());
        };
    }

    public static Copier<Oppiaine> perusopetusCopier() {
        return perusopetusCopier(true);
    }

    public static Copier<Oppiaine> perusopetusCopier(boolean kopioiTekstit) {
        return (other, o) -> {
            Map<Long, Opetuksenkohdealue> kohdealueet = other.getKohdealueet().stream()
                    .collect(Collectors.toMap(AbstractReferenceableEntity::getId, ka -> new Opetuksenkohdealue(ka.getNimi())));
            o.setKohdealueet(new HashSet<>(kohdealueet.values()));
            other.getVuosiluokkakokonaisuudet().forEach((vk -> {
                Oppiaineenvuosiluokkakokonaisuus ovk = Oppiaineenvuosiluokkakokonaisuus.copyOf(vk, kohdealueet, kopioiTekstit);
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
                other.getOppimaarat().stream()
                        .filter(oppimaaraFilter)
                        .forEach((om -> o.addOppimaara(with.copy(om))));
            }
        };
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
