package fi.vm.sade.eperusteet.ylops.domain.ops;

import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.context.annotation.Profile;

@Profile("!test")
@Entity
@Immutable
@Table(name = "opetussuunnitelman_julkaisu_data")
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
    @JdbcTypeCode(SqlTypes.JSON)
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
