package fi.vm.sade.eperusteet.ylops.domain.lops2019;

import fi.vm.sade.eperusteet.ylops.domain.AbstractAuditedReferenceableEntity;
import fi.vm.sade.eperusteet.ylops.domain.HistoriaTapahtuma;
import fi.vm.sade.eperusteet.ylops.domain.Poistettava;
import fi.vm.sade.eperusteet.ylops.domain.teksti.LokalisoituTeksti;
import fi.vm.sade.eperusteet.ylops.domain.validation.ValidHtml;
import fi.vm.sade.eperusteet.ylops.dto.navigation.NavigationType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
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
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Audited
@Table(name = "lops2019_oppiaine",
       uniqueConstraints = @UniqueConstraint(columnNames = { "koodi", "sisalto_id" }))
public class Lops2019Oppiaine extends AbstractAuditedReferenceableEntity implements HistoriaTapahtuma, Poistettava {

    @ManyToOne
    @Getter
    @NotNull
    private Lops2019Sisalto sisalto;

    @Getter
    @Setter
    @Column(name = "perusteen_oppiaine_uri")
    private String perusteenOppiaineUri;

    @Getter
    @Setter
    @NotEmpty
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
    private LokalisoituTeksti pakollistenModuulienKuvaus;

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @Getter
    @Setter
    @ValidHtml(whitelist = ValidHtml.WhitelistType.NORMAL)
    private LokalisoituTeksti valinnaistenModuulienKuvaus;

    @Getter
    @Setter
    @OneToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST}, orphanRemoval = true)
    private Lops2019PaikallinenArviointi arviointi;

    @Getter
    @OrderColumn
    @OneToMany(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST}, orphanRemoval = true)
    @JoinTable(name = "lops2019_oppiaine_paikallinen_laaja_alainen_osaaminen",
            joinColumns = @JoinColumn(name = "lops2019_oppiaine_id"),
            inverseJoinColumns = @JoinColumn(name = "laajaalainenosaaminen_id"))
    private List<PaikallinenLaajaAlainenOsaaminen> laajaAlainenOsaaminen = new ArrayList<>();

    @Getter
    @Setter
    @OneToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST}, orphanRemoval = true)
    private Lops2019Tehtava tehtava;

    @Getter
    @Setter
    @OneToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST}, orphanRemoval = true)
    private Lops2019OpiskeluymparistoTyotavat opiskeluymparistoTyotavat;

    @Getter
    @Setter
    @OneToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST}, orphanRemoval = true)
    @JoinTable(name = "lops2019_oppiaine_tavoitteet")
    private Lops2019Tavoitteet tavoitteet;

    void setLaajaAlainenOsaaminen(Collection<PaikallinenLaajaAlainenOsaaminen> osaamiset) {
        if (laajaAlainenOsaaminen == null) {
            laajaAlainenOsaaminen = new ArrayList<>();
        }
        laajaAlainenOsaaminen.clear();
        laajaAlainenOsaaminen.addAll(osaamiset);
    }

    public void setSisalto(Lops2019Sisalto uusi) {
        if (this.sisalto == null) {
            this.sisalto = uusi;
        }
    }

    static public Lops2019Oppiaine copy(Lops2019Oppiaine original) {
        if (original != null) {
            Lops2019Oppiaine result = new Lops2019Oppiaine();
            result.setPerusteenOppiaineUri(original.getPerusteenOppiaineUri());
            result.setKoodi(original.getKoodi());
            result.setNimi(original.getNimi());
            result.setKuvaus(original.getKuvaus());
            result.setPakollistenModuulienKuvaus(original.getPakollistenModuulienKuvaus());
            result.setValinnaistenModuulienKuvaus(original.getPakollistenModuulienKuvaus());
            result.setLaajaAlainenOsaaminen(original.getLaajaAlainenOsaaminen()
                    .stream()
                    .map(PaikallinenLaajaAlainenOsaaminen::copy)
                    .toList());
            result.setArviointi(Lops2019PaikallinenArviointi.copy(original.getArviointi()));
            result.setTehtava(Lops2019Tehtava.copy(original.getTehtava()));
            result.setOpiskeluymparistoTyotavat(Lops2019OpiskeluymparistoTyotavat.copy(original.getOpiskeluymparistoTyotavat()));
            result.setTavoitteet(Lops2019Tavoitteet.copy(original.getTavoitteet()));
            return result;
        }
        return null;
    }

    @Override
    public NavigationType getNavigationType() {
        return NavigationType.poppiaine;
    }

    @Override
    public PoistetunTyyppi getPoistetunTyyppi() {
        return PoistetunTyyppi.LOPS2019OPPIAINE;
    }
}
