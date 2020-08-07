package fi.vm.sade.eperusteet.ylops.domain.lops2019;

import fi.vm.sade.eperusteet.ylops.domain.AbstractAuditedReferenceableEntity;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

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
