package fi.vm.sade.eperusteet.ylops.domain.lops2019;

import fi.vm.sade.eperusteet.ylops.domain.AbstractAuditedReferenceableEntity;
import fi.vm.sade.eperusteet.ylops.domain.teksti.LokalisoituTeksti;
import fi.vm.sade.eperusteet.ylops.domain.validation.ValidHtml;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

@Entity
@Audited
@Table(name = "lops2019_opintojakson_moduuli")
public class Lops2019OpintojaksonModuuli extends AbstractAuditedReferenceableEntity {

    @Getter
    @NotNull
    @Setter
    private String koodiUri;

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @Getter
    @Setter
    @ValidHtml(whitelist = ValidHtml.WhitelistType.NORMAL)
    private LokalisoituTeksti kuvaus;

    static Lops2019OpintojaksonModuuli copy(Lops2019OpintojaksonModuuli original) {
        if (original != null) {
            Lops2019OpintojaksonModuuli result = new Lops2019OpintojaksonModuuli();
            result.setKoodiUri(original.getKoodiUri());
            result.setKuvaus(original.getKuvaus());
            return result;
        }
        return null;
    }

}
