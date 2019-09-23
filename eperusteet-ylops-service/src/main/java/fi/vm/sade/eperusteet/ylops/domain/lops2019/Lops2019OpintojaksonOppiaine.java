package fi.vm.sade.eperusteet.ylops.domain.lops2019;

import fi.vm.sade.eperusteet.ylops.domain.AbstractAuditedReferenceableEntity;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Audited
@Table(name = "lops2019_opintojakson_oppiaine")
public class Lops2019OpintojaksonOppiaine extends AbstractAuditedReferenceableEntity {
    @Getter
    @Setter
    private String koodi;

    @Getter
    @Setter
    private Long laajuus;

    static Lops2019OpintojaksonOppiaine copy(Lops2019OpintojaksonOppiaine original) {
        if (original != null) {
            Lops2019OpintojaksonOppiaine result = new Lops2019OpintojaksonOppiaine();
            result.setKoodi(original.getKoodi());
            result.setLaajuus(original.getLaajuus());
            return result;
        }
        return null;
    }

}
