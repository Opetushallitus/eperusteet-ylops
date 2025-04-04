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
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Audited
@Table(name = "lops2019_oppiaine_arviointi")
public class Lops2019PaikallinenArviointi extends AbstractAuditedReferenceableEntity {

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @Getter
    @Setter
    @ValidHtml(whitelist = ValidHtml.WhitelistType.NORMAL)
    private LokalisoituTeksti kuvaus;

    public static Lops2019PaikallinenArviointi copy(Lops2019PaikallinenArviointi original) {
        if (original == null) {
            return null;
        }
        Lops2019PaikallinenArviointi result = new Lops2019PaikallinenArviointi();
        result.setKuvaus(original.getKuvaus());
        return result;
    }
}
