package fi.vm.sade.eperusteet.ylops.domain.oppiaine;

import fi.vm.sade.eperusteet.ylops.domain.AbstractReferenceableEntity;
import fi.vm.sade.eperusteet.ylops.domain.LaajaalainenosaaminenViite;
import fi.vm.sade.eperusteet.ylops.domain.teksti.LokalisoituTeksti;
import fi.vm.sade.eperusteet.ylops.domain.validation.ValidHtml;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import jakarta.persistence.CascadeType;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Entity
@Table(name = "opetuksen_tavoite")
@Audited
public class Opetuksentavoite extends AbstractReferenceableEntity {

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @Getter
    @Setter
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @ValidHtml(whitelist = ValidHtml.WhitelistType.NORMAL)
    private LokalisoituTeksti tavoite;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @Getter
    @Setter
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @ValidHtml(whitelist = ValidHtml.WhitelistType.SIMPLIFIED)
    /**
     * Opetuksen tavoitteen paikallisesti laadittu kuvaus.
     */
    private LokalisoituTeksti kuvaus;

    @Getter
    @Setter
    @OneToMany(orphanRemoval = true, cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY, mappedBy = "opetuksentavoite")
    @Fetch(FetchMode.SUBSELECT)
    private Set<OpetuksenKeskeinensisaltoalue> sisaltoalueet = new HashSet<>();

    @Getter
    @Setter
    private UUID tunniste;

    @Getter
    @Setter
    @ElementCollection(fetch = FetchType.LAZY)
    @BatchSize(size = 25)
    private Set<LaajaalainenosaaminenViite> laajattavoitteet = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @BatchSize(size = 25)
    @JoinTable(name = "opetuksen_tavoite_tavoitteen_arviointi",
            joinColumns = @JoinColumn(name = "opetuksen_tavoite_id"),
            inverseJoinColumns = @JoinColumn(name = "arvioinninkohteet_id"))
    private Set<Tavoitteenarviointi> arvioinninkohteet = new HashSet<>();

    @Getter
    @Setter
    @ManyToMany(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @BatchSize(size = 25)
    @JoinTable(name = "opetuksen_tavoite_oppiaine_kohdealue",
            joinColumns = @JoinColumn(name = "opetuksen_tavoite_id"),
            inverseJoinColumns = @JoinColumn(name = "kohdealueet_id"))
    private Set<Opetuksenkohdealue> kohdealueet = new HashSet<>();

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @Getter
    @Setter
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @ValidHtml(whitelist = ValidHtml.WhitelistType.SIMPLIFIED)
    private LokalisoituTeksti arvioinninKuvaus;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @Getter
    @Setter
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @ValidHtml(whitelist = ValidHtml.WhitelistType.SIMPLIFIED)
    private LokalisoituTeksti vapaaTeksti;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @Getter
    @Setter
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @ValidHtml(whitelist = ValidHtml.WhitelistType.SIMPLIFIED)
    private LokalisoituTeksti tavoitteistaJohdetutOppimisenTavoitteet;

    public Set<Tavoitteenarviointi> getArvioinninkohteet() {
        return new HashSet<>(arvioinninkohteet);
    }

    public void setArvioinninkohteet(Set<Tavoitteenarviointi> kohteet) {
        this.arvioinninkohteet.clear();
        if (kohteet != null) {
            this.arvioinninkohteet.addAll(kohteet);
        }
    }

    public static Opetuksentavoite copyOf(Opetuksentavoite other) {
        return copyOf(other, null);
    }

    static Opetuksentavoite copyOf(Opetuksentavoite other, Map<Long, Opetuksenkohdealue> kohdealueet) {
        Opetuksentavoite ot = new Opetuksentavoite();
        ot.setTunniste(other.getTunniste());
        ot.setTavoite(other.getTavoite());
        ot.setLaajattavoitteet(
                other.getLaajattavoitteet().stream()
                        .map(LaajaalainenosaaminenViite::new)
                        .collect(Collectors.toSet())
        );

        ot.setSisaltoalueet(
                other.getSisaltoalueet().stream()
                        .map(s -> OpetuksenKeskeinensisaltoalue.copyOf(s, ot))
                        .collect(Collectors.toSet())
        );

        ot.setKohdealueet(
                other.getKohdealueet().stream()
                        .map(k -> kohdealueet.get(k.getId()))
                        .collect(Collectors.toSet())
        );
        ot.setArvioinninkohteet(
                other.getArvioinninkohteet().stream()
                        .map(a -> Tavoitteenarviointi.copyOf(a))
                        .collect(Collectors.toSet())
        );
        ot.setVapaaTeksti(other.getVapaaTeksti());
        ot.setArvioinninKuvaus(other.getArvioinninKuvaus());
        ot.setTavoitteistaJohdetutOppimisenTavoitteet(other.getTavoitteistaJohdetutOppimisenTavoitteet());
        return ot;
    }

    public Optional<OpetuksenKeskeinensisaltoalue> getOpetuksenkeskeinenSisaltoalueById(Long id) {
        return this.sisaltoalueet.stream()
                .filter(k -> (Long.compare(k.getId(), id) == 0))
                .findAny();
    }

    public Optional<OpetuksenKeskeinensisaltoalue> getOpetuksenkeskeinenSisaltoalueBySisaltoalueId(Long id) {
        return this.sisaltoalueet.stream()
                .filter(k -> (id != null && k.getSisaltoalueet() != null && k.getSisaltoalueet().getId() != null
                        && Long.compare(k.getSisaltoalueet().getId(), id) == 0))
                .findAny();
    }

    public void connectSisaltoalueet(Set<Keskeinensisaltoalue> connectSisaltoalueet) {
        connectSisaltoalueet.forEach(sisaltoalue -> {
            Optional<OpetuksenKeskeinensisaltoalue> keskeinensisaltoalue = getOpetuksenkeskeinenSisaltoalueBySisaltoalueId(sisaltoalue.getId());
            OpetuksenKeskeinensisaltoalue opetuksenKeskeinensisaltoalue = (keskeinensisaltoalue.isPresent()) ?
                    keskeinensisaltoalue.get() : new OpetuksenKeskeinensisaltoalue();

            opetuksenKeskeinensisaltoalue.setSisaltoalueet(sisaltoalue);
            opetuksenKeskeinensisaltoalue.setOpetuksentavoite(this);
            sisaltoalueet.add(opetuksenKeskeinensisaltoalue);
        });
    }
}
