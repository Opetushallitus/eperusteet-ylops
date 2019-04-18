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
}
