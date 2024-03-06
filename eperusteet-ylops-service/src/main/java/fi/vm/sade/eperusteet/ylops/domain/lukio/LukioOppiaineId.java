package fi.vm.sade.eperusteet.ylops.domain.lukio;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Getter
@Setter
@Embeddable
public class LukioOppiaineId implements Serializable {
    @Column(name = "oppiaine_id")
    private Long oppiaineId;
    @Column(name = "opetussuunnielma_id")
    private Long opetusuunnitelmaId;

    protected LukioOppiaineId() {
    }

    public LukioOppiaineId(Long opetusuunnitelmaId, Long oppiaineId) {
        this.opetusuunnitelmaId = opetusuunnitelmaId;
        this.oppiaineId = oppiaineId;
    }
}
