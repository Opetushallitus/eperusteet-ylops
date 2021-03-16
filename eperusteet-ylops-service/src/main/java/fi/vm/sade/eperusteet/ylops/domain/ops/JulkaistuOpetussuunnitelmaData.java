package fi.vm.sade.eperusteet.ylops.domain.ops;

import com.fasterxml.jackson.databind.node.ObjectNode;
import fi.vm.sade.eperusteet.ylops.repository.dialect.JsonBType;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import org.springframework.context.annotation.Profile;

@Profile("!test")
@Entity
@Immutable
@Table(name = "opetussuunnitelman_julkaisu_data")
@TypeDef(name = "jsonb", defaultForType = JsonBType.class, typeClass = JsonBType.class)
public class JulkaistuOpetussuunnitelmaData {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Getter
    @Setter
    private Long id;

    @NotNull
    @Getter
    private int hash;

    @NotNull
    @Getter
    @Setter
    @Type(type = "jsonb")
    @Column(name = "opsdata")
    private ObjectNode opsData;

    @PrePersist
    void prepersist() {
        hash = opsData.hashCode();
    }

    public JulkaistuOpetussuunnitelmaData() {
    }

    public JulkaistuOpetussuunnitelmaData(ObjectNode opsData) {
        this.opsData = opsData;
    }

}
