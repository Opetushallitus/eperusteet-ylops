package fi.vm.sade.eperusteet.ylops.domain.ops;

import fi.vm.sade.eperusteet.ylops.domain.AbstractAuditedEntity;
import fi.vm.sade.eperusteet.ylops.domain.AikatauluTapahtuma;
import fi.vm.sade.eperusteet.ylops.domain.teksti.LokalisoituTeksti;
import fi.vm.sade.eperusteet.ylops.domain.validation.ValidHtml;
import java.util.Date;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Audited
@Table(name = "opetussuunnitelman_aikataulu")
public class OpetussuunnitelmaAikataulu extends AbstractAuditedEntity {

    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Id
    private Long id;

    @NotNull
    @Column(name = "opetussuunnitelma_id")
    private Long opetussuunnitelmaId;

    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @ValidHtml(whitelist = ValidHtml.WhitelistType.SIMPLIFIED)
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private LokalisoituTeksti tavoite;

    @Enumerated(value = EnumType.STRING)
    @NotNull
    private AikatauluTapahtuma tapahtuma;

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    private Date tapahtumapaiva;

}
