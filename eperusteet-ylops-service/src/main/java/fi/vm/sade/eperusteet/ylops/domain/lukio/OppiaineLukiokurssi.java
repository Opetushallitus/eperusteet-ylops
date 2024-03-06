package fi.vm.sade.eperusteet.ylops.domain.lukio;

import fi.vm.sade.eperusteet.ylops.domain.AbstractAuditedReferenceableEntity;
import fi.vm.sade.eperusteet.ylops.domain.oppiaine.Oppiaine;
import fi.vm.sade.eperusteet.ylops.domain.ops.Opetussuunnitelma;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Entity
@Audited
@Table(name = "oppiaine_lukiokurssi", schema = "public",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"opetussuunnitelma_id", "kurssi_id", "oppiaine_id"})
        })
public class OppiaineLukiokurssi extends AbstractAuditedReferenceableEntity {

    @Getter
    @Setter
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "kurssi_id", nullable = false)
    private Lukiokurssi kurssi;

    @Getter
    @Setter
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "oppiaine_id", nullable = false)
    private Oppiaine oppiaine;

    @Getter
    @Setter
    @Column
    private Integer jarjestys;

    @Getter
    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "opetussuunnitelma_id", nullable = false)
    private Opetussuunnitelma opetussuunnitelma;

    @Getter
    @Setter
    @Column(name = "oma", nullable = false)
    private boolean oma;

    protected OppiaineLukiokurssi() {
    }

    public OppiaineLukiokurssi(Opetussuunnitelma opetussuunnitelma,
                               Oppiaine oppiaine, Lukiokurssi kurssi,
                               Integer jarjestys, boolean oma) {
        this.opetussuunnitelma = opetussuunnitelma;
        this.oppiaine = oppiaine;
        this.kurssi = kurssi;
        this.jarjestys = jarjestys;
        this.oma = oma;
    }
}
