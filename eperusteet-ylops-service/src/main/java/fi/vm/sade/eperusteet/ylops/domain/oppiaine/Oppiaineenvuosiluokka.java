package fi.vm.sade.eperusteet.ylops.domain.oppiaine;

import fi.vm.sade.eperusteet.ylops.domain.AbstractAuditedReferenceableEntity;
import fi.vm.sade.eperusteet.ylops.domain.HistoriaTapahtuma;
import fi.vm.sade.eperusteet.ylops.domain.Vuosiluokka;
import fi.vm.sade.eperusteet.ylops.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.ylops.domain.teksti.LokalisoituTeksti;
import fi.vm.sade.eperusteet.ylops.domain.validation.ValidHtml;
import fi.vm.sade.eperusteet.ylops.dto.navigation.NavigationType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Entity
@Audited
@Table(name = "oppiaineenvuosiluokka", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"kokonaisuus_id", "vuosiluokka"})})
public class Oppiaineenvuosiluokka extends AbstractAuditedReferenceableEntity implements Serializable, HistoriaTapahtuma {

    @Getter
    @Setter(AccessLevel.PACKAGE)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @NotNull
    private Oppiaineenvuosiluokkakokonaisuus kokonaisuus;

    @Getter
    @Setter
    @Enumerated(EnumType.STRING)
    private Vuosiluokka vuosiluokka;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinTable
    @OrderColumn
    @BatchSize(size = 25)
    private List<Opetuksentavoite> tavoitteet = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinTable
    @OrderColumn
    @BatchSize(size = 25)
    private List<Keskeinensisaltoalue> sisaltoalueet = new ArrayList<>();

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @Getter
    @Setter
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @ValidHtml(whitelist = ValidHtml.WhitelistType.SIMPLIFIED)
    private LokalisoituTeksti vapaaTeksti;

    public Oppiaineenvuosiluokka() {
    }

    public Oppiaineenvuosiluokka(Vuosiluokka vuosiluokka) {
        this.vuosiluokka = vuosiluokka;
    }

    public Optional<Keskeinensisaltoalue> getSisaltoalue(UUID tunniste) {
        return this.sisaltoalueet.stream()
                .filter(k -> Objects.equals(k.getTunniste(), tunniste))
                .findAny();
    }

    public Optional<Opetuksentavoite> getTavoite(UUID tunniste) {
        return this.tavoitteet.stream()
                .filter(k -> Objects.equals(k.getTunniste(), tunniste))
                .findAny();
    }

    public List<Opetuksentavoite> getTavoitteet() {
        return new ArrayList<>(tavoitteet);
    }

    public void setTavoitteet(List<Opetuksentavoite> tavoitteet) {
        this.tavoitteet.clear();
        if (tavoitteet != null) {
            this.tavoitteet.addAll(tavoitteet);
        }
    }

    public List<Keskeinensisaltoalue> getSisaltoalueet() {
        return new ArrayList<>(sisaltoalueet);
    }

    public void setSisaltoalueet(List<Keskeinensisaltoalue> sisaltoalueet) {
        this.sisaltoalueet.clear();
        if (sisaltoalueet != null) {
            this.sisaltoalueet.addAll(sisaltoalueet);
        }
    }

    static Oppiaineenvuosiluokka copyOf(final Oppiaineenvuosiluokka other, final Map<Long, Opetuksenkohdealue> kohdealueet) {
        Oppiaineenvuosiluokka ovl = new Oppiaineenvuosiluokka();
        ovl.setVuosiluokka(other.getVuosiluokka());
        ovl.setTavoitteet(
                other.tavoitteet.stream()
                        .map(t -> Opetuksentavoite.copyOf(t, kohdealueet))
                        .collect(Collectors.toList()));
        ovl.setSisaltoalueet(other.getSisaltoalueet().stream().map(Keskeinensisaltoalue::copyOf).collect(Collectors.toList()));

        if (other.getVapaaTeksti() != null) {
            ovl.setVapaaTeksti(LokalisoituTeksti.of(other.getVapaaTeksti().getTeksti()));
        }

        return ovl;
    }

    @Override
    public LokalisoituTeksti getNimi() {
        if (vuosiluokka == null) {
            return null;
        }
        return LokalisoituTeksti.of(Kieli.FI, vuosiluokka.toString());
    }

    @Override
    public NavigationType getNavigationType() {
        return NavigationType.oppiaineenvuosiluokka;
    }

}
