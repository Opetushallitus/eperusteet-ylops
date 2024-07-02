package fi.vm.sade.eperusteet.ylops.domain.vuosiluokkakokonaisuus;

import fi.vm.sade.eperusteet.ylops.domain.AbstractAuditedReferenceableEntity;
import fi.vm.sade.eperusteet.ylops.domain.HistoriaTapahtuma;
import fi.vm.sade.eperusteet.ylops.domain.Tila;
import fi.vm.sade.eperusteet.ylops.domain.Vuosiluokkakokonaisuusviite;
import fi.vm.sade.eperusteet.ylops.domain.oppiaine.VapaatekstiPaikallinentarkennus;
import fi.vm.sade.eperusteet.ylops.domain.teksti.LokalisoituTeksti;
import fi.vm.sade.eperusteet.ylops.domain.teksti.Tekstiosa;
import fi.vm.sade.eperusteet.ylops.domain.validation.ValidHtml;
import fi.vm.sade.eperusteet.ylops.dto.navigation.NavigationType;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import javax.persistence.CascadeType;
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
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "vlkokonaisuus")
@Audited
public class Vuosiluokkakokonaisuus extends AbstractAuditedReferenceableEntity implements HistoriaTapahtuma {

    @Getter
    @Setter
    @ManyToOne(optional = false)
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @NotNull
    private Vuosiluokkakokonaisuusviite tunniste;

    @Getter
    @Setter
    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @ValidHtml(whitelist = ValidHtml.WhitelistType.MINIMAL)
    private LokalisoituTeksti nimi;

    @Getter
    @Setter
    @ManyToOne(cascade = CascadeType.ALL, optional = true)
    private Tekstiosa siirtymaEdellisesta;

    @Getter
    @Setter
    @OneToOne(cascade = CascadeType.ALL, optional = true, orphanRemoval = true)
    private Tekstiosa tehtava;

    @Getter
    @Setter
    @ManyToOne(cascade = CascadeType.ALL, optional = true)
    private Tekstiosa siirtymaSeuraavaan;

    @Getter
    @Setter
    @OneToOne(cascade = CascadeType.ALL, optional = true, orphanRemoval = true)
    private Tekstiosa laajaalainenOsaaminen;

    @OneToMany(mappedBy = "vuosiluokkakokonaisuus", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Laajaalainenosaaminen> laajaalaisetosaamiset = new HashSet<>();

    @Enumerated(value = EnumType.STRING)
    @NotNull
    @Getter
    private Tila tila = Tila.LUONNOS;

    @Getter
    @OneToMany(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST}, orphanRemoval = true)
    @JoinTable(name = "vlk_vapaatekstit",
            joinColumns = @JoinColumn(name = "vlk_id"),
            inverseJoinColumns = @JoinColumn(name = "vapaateksti_paikallinentarkennus_id"))
    private List<VapaatekstiPaikallinentarkennus> vapaatTekstit = new ArrayList<>();

    public void setVapaatTekstit(List<VapaatekstiPaikallinentarkennus> vapaatTekstit) {
        this.vapaatTekstit.clear();
        if (vapaatTekstit != null) {
            this.vapaatTekstit.addAll(vapaatTekstit);
        }
    }

    public Vuosiluokkakokonaisuus() {
    }

    private Vuosiluokkakokonaisuus(Vuosiluokkakokonaisuus other) {
        this.tunniste = other.getTunniste();
        this.nimi = other.getNimi();
        this.siirtymaEdellisesta = new Tekstiosa();
        this.siirtymaSeuraavaan = new Tekstiosa();
        this.tehtava = new Tekstiosa();
        this.tila = Tila.LUONNOS;

        other.getLaajaalaisetosaamiset().forEach(l -> {
            Laajaalainenosaaminen lo = new Laajaalainenosaaminen(l);
            lo.setVuosiluokkaKokonaisuus(this);
            laajaalaisetosaamiset.add(lo);
        });

    }

    public Set<Laajaalainenosaaminen> getLaajaalaisetosaamiset() {
        return new HashSet<>(laajaalaisetosaamiset);
    }

    public void setLaajaalaisetosaamiset(Set<Laajaalainenosaaminen> osaamiset) {

        if (osaamiset == null) {
            this.laajaalaisetosaamiset.clear();
            return;
        }

        this.laajaalaisetosaamiset.retainAll(osaamiset);
        this.laajaalaisetosaamiset.addAll(osaamiset);
        for (Laajaalainenosaaminen v : osaamiset) {
            v.setVuosiluokkaKokonaisuus(this);
        }
    }

    public void setTila(Tila tila) {
        if (tila == null) {
            this.tila = Tila.LUONNOS;
        } else if (this.tila == Tila.LUONNOS) {
            this.tila = tila;
        }
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

    public static Vuosiluokkakokonaisuus copyOf(Vuosiluokkakokonaisuus vuosiluokkakokonaisuus) {
        return new Vuosiluokkakokonaisuus(vuosiluokkakokonaisuus);
    }

    @Override
    public NavigationType getNavigationType() {
        return NavigationType.vuosiluokkakokonaisuus;
    }
}
