package fi.vm.sade.eperusteet.ylops.domain.ops;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "poistetut_perusteet")
@AllArgsConstructor
@NoArgsConstructor
public class PoistettuPeruste implements Serializable {

    @Id
    @Column(name = "peruste_id")
    @NotNull
    private Long perusteId;

    @Column(name = "poistettu_aikaleima", nullable = false)
    @NotNull
    private LocalDateTime poistettuAikaleima;

    @PrePersist
    private void prepersist() {
        if (poistettuAikaleima == null) {
            poistettuAikaleima = LocalDateTime.now();
        }
    }

}
