package fi.vm.sade.eperusteet.ylops.domain.lops2019;

import fi.vm.sade.eperusteet.ylops.domain.AbstractAuditedReferenceableEntity;
import fi.vm.sade.eperusteet.ylops.domain.teksti.LokalisoituTeksti;
import fi.vm.sade.eperusteet.ylops.domain.validation.ValidHtml;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Audited
@Table(name = "lops2019_oppiaine_tavoitealue")
@EqualsAndHashCode
public class Lops2019OppiaineenTavoitealue extends AbstractAuditedReferenceableEntity {

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
    @ValidHtml(whitelist = ValidHtml.WhitelistType.MINIMAL)
    private LokalisoituTeksti kohde;

    @Getter
    @OneToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE }, orphanRemoval = true)
    @JoinTable(name = "lops2019_tavoitealueen_tavoitteet",
            joinColumns = @JoinColumn(name = "lops2019_oppiaine_tavoitealue_id"),
            inverseJoinColumns = @JoinColumn(name = "tavoitteet_id"))
    private List<Lops2019Tavoite> tavoitteet = new ArrayList<>();

    public void setTavoitteet(Set<Lops2019Tavoite> tavoitteet) {
        this.tavoitteet.clear();
        this.tavoitteet.addAll(tavoitteet);
    }

    public static Lops2019OppiaineenTavoitealue copy(Lops2019OppiaineenTavoitealue original) {
        if (original == null) {
            return null;
        }
        Lops2019OppiaineenTavoitealue result = new Lops2019OppiaineenTavoitealue();
        result.setKohde(original.getKohde());
        result.setNimi(original.getNimi());
        result.setTavoitteet(original.getTavoitteet().stream()
            .map(Lops2019Tavoite::copy)
            .collect(Collectors.toSet()));
        return result;
    }
}
