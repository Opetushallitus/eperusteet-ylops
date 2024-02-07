package fi.vm.sade.eperusteet.ylops.domain;

import java.io.Serializable;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class AbstractReferenceableEntity implements ReferenceableEntity, Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    protected Long id;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        if (this.id == null) {
            this.id = id;
        }

    }

}
