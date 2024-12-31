package fi.vm.sade.eperusteet.ylops.domain.lukio;

import fi.vm.sade.eperusteet.ylops.domain.oppiaine.Oppiaine;
import fi.vm.sade.eperusteet.ylops.domain.ops.Opetussuunnitelma;

import java.io.Serializable;
import java.util.UUID;
import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "ops_oppiaine_parent", schema = "public")
public class OpsOppiaineParentView implements Serializable {
    @EmbeddedId
    private OpsOppiaineId opsOppiaine;

    @MapsId("opetussuunnitelmaId")
    @ManyToOne(fetch = FetchType.LAZY)
    private Opetussuunnitelma opetussuunnitelma;

    @MapsId("oppiaineId")
    @ManyToOne(fetch = FetchType.LAZY)
    private Oppiaine oppiaine;

    @Column(name = "oppiaine_oma", nullable = false)
    private boolean oma;

    @Column(name = "oppiaine_tunniste")
    private UUID tunniste;

    // Ensimmäinen pohja, jossa ko. oppiaine on määritetty OpsOppiaineena (ei välttämättä suora parent)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ensimmaisen_pohjan_oppiaine_id")
    private Oppiaine pohjanOppiaine;
}
