package fi.vm.sade.eperusteet.ylops.domain.lops2019;

import fi.vm.sade.eperusteet.ylops.domain.AbstractAuditedReferenceableEntity;
import fi.vm.sade.eperusteet.ylops.domain.teksti.LokalisoituTeksti;
import fi.vm.sade.eperusteet.ylops.domain.validation.ValidHtml;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

@Entity
@Audited
@Table(name = "lops2019_oppiaine_opiskeluymparisto_tyotavat")
public class Lops2019OpiskeluymparistoTyotavat extends AbstractAuditedReferenceableEntity {

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @Getter
    @Setter
    @ValidHtml(whitelist = ValidHtml.WhitelistType.NORMAL)
    private LokalisoituTeksti kuvaus;

    public static Lops2019OpiskeluymparistoTyotavat copy(Lops2019OpiskeluymparistoTyotavat original) {
        if (original == null) {
            return null;
        }
        Lops2019OpiskeluymparistoTyotavat result = new Lops2019OpiskeluymparistoTyotavat();
        result.setKuvaus(original.getKuvaus());
        return result;
    }
}
