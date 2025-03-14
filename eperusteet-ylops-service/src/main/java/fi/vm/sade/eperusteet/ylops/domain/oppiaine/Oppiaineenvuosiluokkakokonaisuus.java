package fi.vm.sade.eperusteet.ylops.domain.oppiaine;

import fi.vm.sade.eperusteet.ylops.domain.AbstractAuditedReferenceableEntity;
import fi.vm.sade.eperusteet.ylops.domain.Vuosiluokka;
import fi.vm.sade.eperusteet.ylops.domain.Vuosiluokkakokonaisuusviite;
import fi.vm.sade.eperusteet.ylops.domain.teksti.Tekstiosa;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * Kuvaa oppimäärän yhteen vuosiluokkakokonaisuuteen osalta.
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

    static Oppiaineenvuosiluokkakokonaisuus copyOf(final Oppiaineenvuosiluokkakokonaisuus other, Map<Long, Opetuksenkohdealue> kohdealueet, boolean kopioiTekstit) {
        Oppiaineenvuosiluokkakokonaisuus ovk = new Oppiaineenvuosiluokkakokonaisuus();

        ovk.setVuosiluokkakokonaisuus(other.getVuosiluokkakokonaisuus());

        if (kopioiTekstit) {
            ovk.setArviointi(Tekstiosa.copyOf(other.getArviointi()));
            ovk.setOhjaus(Tekstiosa.copyOf(other.getOhjaus()));
            ovk.setTehtava(Tekstiosa.copyOf(other.getTehtava()));
            ovk.setTyotavat(Tekstiosa.copyOf(other.getTyotavat()));
            ovk.setYleistavoitteet(Tekstiosa.copyOf(other.getYleistavoitteet()));
        } else {
            ovk.setArviointi(new Tekstiosa());
            ovk.setOhjaus(new Tekstiosa());
            ovk.setTehtava(new Tekstiosa());
            ovk.setTyotavat(new Tekstiosa());
            ovk.setYleistavoitteet(new Tekstiosa());
        }

        ovk.setJnro(other.getJnro());
        other.getVuosiluokat().forEach(vl -> ovk.addVuosiluokka(Oppiaineenvuosiluokka.copyOf(vl, kohdealueet)));

        return ovk;
    }

}
