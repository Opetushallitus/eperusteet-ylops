package fi.vm.sade.eperusteet.ylops.domain.lops2019;

import fi.vm.sade.eperusteet.ylops.domain.AbstractAuditedReferenceableEntity;
import fi.vm.sade.eperusteet.ylops.domain.HistoriaTapahtuma;
import fi.vm.sade.eperusteet.ylops.domain.Poistettava;
import fi.vm.sade.eperusteet.ylops.domain.teksti.LokalisoituTeksti;
import fi.vm.sade.eperusteet.ylops.domain.validation.ValidHtml;
import fi.vm.sade.eperusteet.ylops.dto.navigation.NavigationType;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;
import org.hibernate.validator.constraints.NotEmpty;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Audited
@Table(name = "lops2019_opintojakso")
public class Lops2019Opintojakso extends AbstractAuditedReferenceableEntity implements HistoriaTapahtuma, Poistettava {

    @Getter
    @Setter
    private String koodi;

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @Getter
    @Setter
    @ValidHtml(whitelist = ValidHtml.WhitelistType.MINIMAL)
    private LokalisoituTeksti nimi;

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @Getter
    @Setter
    @ValidHtml(whitelist = ValidHtml.WhitelistType.NORMAL)
    private LokalisoituTeksti kuvaus;

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @Getter
    @Setter
    @ValidHtml(whitelist = ValidHtml.WhitelistType.NORMAL)
    private LokalisoituTeksti arviointi;

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @Getter
    @Setter
    @ValidHtml(whitelist = ValidHtml.WhitelistType.NORMAL)
    private LokalisoituTeksti opiskeluymparistoTyotavat;

    @Getter
    @OrderColumn
    @OneToMany(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST}, orphanRemoval = true)
    @JoinTable(name = "lops2019_opintojakso_tavoite",
            joinColumns = @JoinColumn(name = "opintojakso_id"),
            inverseJoinColumns = @JoinColumn(name = "tavoite_id"))
    private List<Lops2019OpintojaksonTavoite> tavoitteet = new ArrayList<>();

    @Getter
    @OrderColumn
    @OneToMany(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST}, orphanRemoval = true)
    @JoinTable(name = "lops2019_opintojakso_sisalto",
            joinColumns = @JoinColumn(name = "opintojakso_id"),
            inverseJoinColumns = @JoinColumn(name = "keskeinen_sisalto_id"))
    private List<Lops2019OpintojaksonKeskeinenSisalto> keskeisetSisallot = new ArrayList<>();

    @Getter
    @OrderColumn
    @OneToMany(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST}, orphanRemoval = true)
    @JoinTable(name = "lops2019_opintojakso_laajaalainen",
            joinColumns = @JoinColumn(name = "opintojakso_id"),
            inverseJoinColumns = @JoinColumn(name = "laaja_alainen_id"))
    private List<Lops2019LaajaAlainenOsaaminen> laajaAlainenOsaaminen = new ArrayList<>();

    @Getter
    @OneToMany(fetch = FetchType.EAGER, cascade = {CascadeType.MERGE, CascadeType.PERSIST}, orphanRemoval = true)
    @Fetch(FetchMode.SUBSELECT)
    @JoinTable(name = "lops2019_opintojakso_moduuli",
            joinColumns = @JoinColumn(name = "opintojakso_id"),
            inverseJoinColumns = @JoinColumn(name = "moduuli_id"))
    private List<Lops2019OpintojaksonModuuli> moduulit = new ArrayList<>();

    @Getter
    @OneToMany(fetch = FetchType.EAGER, cascade = {CascadeType.MERGE, CascadeType.PERSIST}, orphanRemoval = true)
    @NotEmpty
    @Fetch(FetchMode.SUBSELECT)
    @JoinTable(name = "lops2019_opintojakso_oppiaine",
            joinColumns = @JoinColumn(name = "opintojakso_id"),
            inverseJoinColumns = @JoinColumn(name = "oj_oppiaine_id"))
    private Set<Lops2019OpintojaksonOppiaine> oppiaineet = new HashSet<>();

    @Getter
    @ManyToMany
    @JoinTable(name = "lops2019_opintojakso_opintojakso",
            joinColumns = @JoinColumn(name = "opintojakso_id"),
            inverseJoinColumns = @JoinColumn(name = "oj_opintojakso_id"))
    private Set<Lops2019Opintojakso> paikallisetOpintojaksot = new HashSet<>();

    public void setTavoitteet(List<Lops2019OpintojaksonTavoite> tavoitteet) {
        this.tavoitteet.clear();
        this.tavoitteet.addAll(tavoitteet);
    }

    public void setKeskeisetSisallot(List<Lops2019OpintojaksonKeskeinenSisalto> keskeisetSisallot) {
        this.keskeisetSisallot.clear();
        this.keskeisetSisallot.addAll(keskeisetSisallot);
    }

    public void setModuulit(Set<Lops2019OpintojaksonModuuli> moduulit) {
        this.moduulit.clear();
        this.moduulit.addAll(moduulit);
    }

    public void setOppiaineet(Set<Lops2019OpintojaksonOppiaine> oppiaineet) {
        this.oppiaineet.clear();
        this.oppiaineet.addAll(oppiaineet);
    }

    public void setLaajaAlainenOsaaminen(Set<Lops2019LaajaAlainenOsaaminen> laajaAlaiset) {
        this.laajaAlainenOsaaminen.clear();
        this.laajaAlainenOsaaminen.addAll(laajaAlaiset);
    }

    public void setPaikallisetOpintojaksot(Set<Lops2019Opintojakso> paikallisetOpintojaksot) {
        if(this.paikallisetOpintojaksot == null) {
            this.paikallisetOpintojaksot = new HashSet<>();
        }

        this.paikallisetOpintojaksot.clear();
        this.paikallisetOpintojaksot.addAll(paikallisetOpintojaksot);
    }

    static public Lops2019Opintojakso copy(Lops2019Opintojakso original) {
        if (original != null) {
            Lops2019Opintojakso result = new Lops2019Opintojakso();
            result.setKeskeisetSisallot(original.getKeskeisetSisallot());
            result.setKoodi(original.getKoodi());
            result.setKuvaus(original.getKuvaus());
            result.setTavoitteet(original.getTavoitteet());
            result.setNimi(original.getNimi());
            result.setLaajaAlainenOsaaminen(original.getLaajaAlainenOsaaminen().stream()
                    .map(Lops2019LaajaAlainenOsaaminen::copy)
                    .collect(Collectors.toSet()));
            result.setModuulit(original.getModuulit().stream()
                    .map(Lops2019OpintojaksonModuuli::copy)
                    .collect(Collectors.toSet()));
            result.setOppiaineet(original.getOppiaineet().stream()
                    .map(Lops2019OpintojaksonOppiaine::copy)
                    .collect(Collectors.toSet()));
            result.setPaikallisetOpintojaksot(original.getPaikallisetOpintojaksot().stream()
                    .map(Lops2019Opintojakso::copy)
                    .collect(Collectors.toSet()));
            return result;
        }
        return null;
    }

    @Override
    public NavigationType getNavigationType() {
        return NavigationType.opintojakso;
    }

    @Override
    public PoistetunTyyppi getPoistetunTyyppi() {
        return PoistetunTyyppi.OPINTOJAKSO;
    }
}
