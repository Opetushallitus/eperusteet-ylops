package fi.vm.sade.eperusteet.ylops.domain.tpo;

import fi.vm.sade.eperusteet.ylops.domain.AbstractAuditedReferenceableEntity;
import fi.vm.sade.eperusteet.ylops.domain.teksti.LokalisoituTeksti;
import fi.vm.sade.eperusteet.ylops.domain.validation.ValidHtml;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

@Entity
@Audited
@Table(name = "tpo_taiteenosa")
public class Taiteenosa extends AbstractAuditedReferenceableEntity {

    @Getter
    @Setter
    @Column(name = "perusteen_taiteenosan_id")
    private Long perusteenTaiteenosanId;

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @JoinColumn(name = "paikallinen_tarkennus_id")
    @Getter
    @Setter
    @ValidHtml(whitelist = ValidHtml.WhitelistType.NORMAL)
    private LokalisoituTeksti paikallinenTarkennus;

    public static Taiteenosa copy(Taiteenosa original) {
        if (original == null) {
            return null;
        }
        Taiteenosa result = new Taiteenosa();
        result.setPerusteenTaiteenosanId(original.getPerusteenTaiteenosanId());
        result.setPaikallinenTarkennus(original.getPaikallinenTarkennus());
        return result;
    }
}
