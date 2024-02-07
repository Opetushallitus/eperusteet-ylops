package fi.vm.sade.eperusteet.ylops.domain.lukio;

import fi.vm.sade.eperusteet.ylops.domain.AbstractAuditedReferenceableEntity;
import fi.vm.sade.eperusteet.ylops.domain.ops.Opetussuunnitelma;
import fi.vm.sade.eperusteet.ylops.domain.teksti.LokalisoituTeksti;
import fi.vm.sade.eperusteet.ylops.domain.validation.ValidHtml;
import fi.vm.sade.eperusteet.ylops.service.util.LambdaUtil.Copyable;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Audited
@Table(name = "lukiokoulutuksen_opetuksen_yleiset_tavoitteet", schema = "public")
public class OpetuksenYleisetTavoitteet extends AbstractAuditedReferenceableEntity
        implements Copyable<OpetuksenYleisetTavoitteet> {

    @Column(name = "tunniste", nullable = false, unique = true, updatable = false)
    @Getter
    private UUID uuidTunniste;

    public OpetuksenYleisetTavoitteet() {
    }

    public OpetuksenYleisetTavoitteet(Opetussuunnitelma opetussuunnitelma, UUID uuidTunniste) {
        this.opetussuunnitelma = opetussuunnitelma;
        this.uuidTunniste = uuidTunniste;
    }

    public OpetuksenYleisetTavoitteet(Opetussuunnitelma opetussuunnitelma, UUID uuidTunniste, OpetuksenYleisetTavoitteet parent) {
        this(opetussuunnitelma, uuidTunniste);
        this.parent = parent;
    }

    @Getter
    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", nullable = true)
    private OpetuksenYleisetTavoitteet parent;

    @Getter
    @Setter
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @ValidHtml(whitelist = ValidHtml.WhitelistType.MINIMAL)
    @OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinColumn(name = "otsikko_id", nullable = true)
    private LokalisoituTeksti otsikko;

    @Getter
    @Setter
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @ValidHtml
    @OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinColumn(name = "kuvaus_id")
    private LokalisoituTeksti kuvaus;

    @Getter
    @Setter
    @OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinColumn(name = "opetussuunnitelma_id", nullable = false)
    private Opetussuunnitelma opetussuunnitelma;

    public OpetuksenYleisetTavoitteet copy(Opetussuunnitelma to, OpetuksenYleisetTavoitteet parent) {
        return copyInto(new OpetuksenYleisetTavoitteet(to, this.uuidTunniste, parent));
    }

    public OpetuksenYleisetTavoitteet copyInto(OpetuksenYleisetTavoitteet to) {
        to.uuidTunniste = this.uuidTunniste;
        to.otsikko = this.otsikko;
        // to.kuvaus = this.kuvaus;
        return to;
    }
}
