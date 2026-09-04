package fi.vm.sade.eperusteet.ylops.domain.lukio;

import java.io.Serializable;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
@Embeddable
public class OpsOppiaineId implements Serializable {
    @Column(name = "oppiaine_id")
    private Long oppiaineId;

    @Column(name = "opetussuunnitelma_id")
    private Long opetussuunnitelmaId;
}
