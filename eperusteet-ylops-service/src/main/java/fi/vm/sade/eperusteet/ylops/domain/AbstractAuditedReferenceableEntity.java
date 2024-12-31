package fi.vm.sade.eperusteet.ylops.domain;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

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
