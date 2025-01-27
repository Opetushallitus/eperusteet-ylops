package fi.vm.sade.eperusteet.ylops.domain.lops2019;

import fi.vm.sade.eperusteet.ylops.domain.AbstractAuditedReferenceableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;

@Entity
@Audited
@Table(name = "lops2019_oppiaine_jarjestys")
public class Lops2019OppiaineJarjestys extends AbstractAuditedReferenceableEntity {

    @Getter
    @Setter
    @NotNull
    private String koodi;

    @Getter
    @Setter
    @Column
    private Integer jarjestys;
}
