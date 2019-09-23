package fi.vm.sade.eperusteet.ylops.domain.ops;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Immutable;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
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
    @Column(nullable = false, updatable = false, columnDefinition = "text")
    private String opsData; // TODO: Käytä jsonb:tä

    @PrePersist
    void prepersist() {
        hash = opsData.hashCode();
    }

    public JulkaistuOpetussuunnitelmaData() {
    }

    public JulkaistuOpetussuunnitelmaData(String opsData) {
        this.opsData = opsData;
    }
}
