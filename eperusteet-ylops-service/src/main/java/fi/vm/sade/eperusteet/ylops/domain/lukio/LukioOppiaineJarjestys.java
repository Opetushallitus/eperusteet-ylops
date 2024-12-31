package fi.vm.sade.eperusteet.ylops.domain.lukio;

import fi.vm.sade.eperusteet.ylops.domain.AbstractAuditedEntity;
import fi.vm.sade.eperusteet.ylops.domain.oppiaine.Oppiaine;
import fi.vm.sade.eperusteet.ylops.domain.ops.Opetussuunnitelma;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;

import jakarta.persistence.*;

@Getter
@Entity
@Audited
@Table(name = "lukio_oppiaine_jarjestys", schema = "public")
public class LukioOppiaineJarjestys extends AbstractAuditedEntity {
    @EmbeddedId
    private LukioOppiaineId id;

    @MapsId("opetusuunnitelmaId")
    @ManyToOne(fetch = FetchType.LAZY)
    private Opetussuunnitelma opetussuunnitelma;

    @MapsId("oppiaineId")
    @ManyToOne(fetch = FetchType.LAZY)
    private Oppiaine oppiaine;

    @Setter
    @Column(name = "jarjestys")
    private Integer jarjestys;

    protected LukioOppiaineJarjestys() {
    }

    public LukioOppiaineJarjestys(Opetussuunnitelma opetussuunnitelma, Oppiaine oppiaine) {
        this.id = new LukioOppiaineId(opetussuunnitelma.getId(), oppiaine.getId());
        this.opetussuunnitelma = opetussuunnitelma;
        this.oppiaine = oppiaine;
    }

    public LukioOppiaineJarjestys(Opetussuunnitelma opetussuunnitelma, Oppiaine oppiaine, Integer jarjestys) {
        this(opetussuunnitelma, oppiaine);
        this.jarjestys = jarjestys;
    }
}
