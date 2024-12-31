package fi.vm.sade.eperusteet.ylops.domain.lops2019;

import fi.vm.sade.eperusteet.ylops.domain.AbstractAuditedReferenceableEntity;
import fi.vm.sade.eperusteet.ylops.domain.teksti.LokalisoituTeksti;
import fi.vm.sade.eperusteet.ylops.domain.validation.ValidHtml;
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
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Audited
@Table(name = "lops2019_oppiaine_tavoitteet")
public class Lops2019Tavoitteet extends AbstractAuditedReferenceableEntity {

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @Getter
    @Setter
    @ValidHtml(whitelist = ValidHtml.WhitelistType.NORMAL)
    private LokalisoituTeksti kuvaus;

    @Getter
    @OneToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE }, orphanRemoval = true)
    @JoinTable(name = "lops2019_tavoitteiden_tavoitealueet",
            joinColumns = @JoinColumn(name = "lops2019_oppiaine_tavoitteet_id"),
            inverseJoinColumns = @JoinColumn(name = "tavoitealueet_id"))
    private Set<Lops2019OppiaineenTavoitealue> tavoitealueet = new HashSet<>();

    public static Lops2019Tavoitteet copy(Lops2019Tavoitteet original) {
        if (original == null) {
            return null;
        }
        Lops2019Tavoitteet result = new Lops2019Tavoitteet();
        result.setKuvaus(original.getKuvaus());
        result.setTavoitealueet(original.getTavoitealueet().stream()
            .map(Lops2019OppiaineenTavoitealue::copy)
            .collect(Collectors.toSet()));
        return result;
    }

    public void setTavoitealueet(Set<Lops2019OppiaineenTavoitealue> tavoitealueet) {
        this.tavoitealueet.clear();
        this.tavoitealueet.addAll(tavoitealueet);
    }

}
