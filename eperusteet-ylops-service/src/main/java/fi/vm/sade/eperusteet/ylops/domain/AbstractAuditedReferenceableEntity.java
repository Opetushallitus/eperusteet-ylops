package fi.vm.sade.eperusteet.ylops.domain;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;

@MappedSuperclass
public abstract class AbstractAuditedReferenceableEntity extends AbstractAuditedEntity implements ReferenceableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Getter
    @Setter
    @Audited
    private Long id;

}
