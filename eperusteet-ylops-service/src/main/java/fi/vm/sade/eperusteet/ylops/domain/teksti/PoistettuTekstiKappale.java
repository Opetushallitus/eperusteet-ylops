package fi.vm.sade.eperusteet.ylops.domain.teksti;

import fi.vm.sade.eperusteet.ylops.domain.AbstractAuditedEntity;
import fi.vm.sade.eperusteet.ylops.domain.ops.Opetussuunnitelma;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "poistettu_tekstikappale")
@Audited
@Getter
@Setter
public class PoistettuTekstiKappale extends AbstractAuditedEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "opetussuunnitelma_id", nullable = false)
    private Opetussuunnitelma opetussuunnitelma;

    @Column(name = "tekstikappale_id")
    private Long tekstiKappale;

    private Boolean palautettu;

}

