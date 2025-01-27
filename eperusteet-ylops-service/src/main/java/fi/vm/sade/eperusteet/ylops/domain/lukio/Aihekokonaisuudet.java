package fi.vm.sade.eperusteet.ylops.domain.lukio;

import fi.vm.sade.eperusteet.ylops.domain.AbstractAuditedReferenceableEntity;
import fi.vm.sade.eperusteet.ylops.domain.ops.Opetussuunnitelma;
import fi.vm.sade.eperusteet.ylops.domain.teksti.LokalisoituTeksti;
import fi.vm.sade.eperusteet.ylops.domain.validation.ValidHtml;
import fi.vm.sade.eperusteet.ylops.service.util.LambdaUtil.ConstructedCopier;
import fi.vm.sade.eperusteet.ylops.service.util.LambdaUtil.Copier;
import fi.vm.sade.eperusteet.ylops.service.util.LambdaUtil.Copyable;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static java.util.stream.Collectors.toSet;

@Entity
@Audited
@Table(name = "aihekokonaisuudet", schema = "public")
public class Aihekokonaisuudet extends AbstractAuditedReferenceableEntity
        implements Copyable<Aihekokonaisuudet> {

    @Column(name = "tunniste", nullable = false, unique = true, updatable = false)
    @Getter
    @Setter
    private UUID uuidTunniste;

    public Aihekokonaisuudet() {
    }

    public Aihekokonaisuudet(Opetussuunnitelma opetussuunnitelma, UUID uuidTunniste) {
        this.opetussuunnitelma = opetussuunnitelma;
        this.uuidTunniste = uuidTunniste;
    }

    public Aihekokonaisuudet(Opetussuunnitelma opetussuunnitelma, UUID uuidTunniste, Aihekokonaisuudet parent) {
        this(opetussuunnitelma, uuidTunniste);
        this.parent = parent;
    }

    @Getter
    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", nullable = true)
    private Aihekokonaisuudet parent;

    @Getter
    @Setter
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @ValidHtml(whitelist = ValidHtml.WhitelistType.MINIMAL)
    @OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinColumn(name = "otsikko_id")
    private LokalisoituTeksti otsikko;

    @Getter
    @Setter
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @ValidHtml
    @OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinColumn(name = "yleiskuvaus_id")
    private LokalisoituTeksti yleiskuvaus;

    @Getter
    @Setter
    @OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinColumn(name = "opetussuunnitelma_id", nullable = false)
    private Opetussuunnitelma opetussuunnitelma;

    @Getter
    @OneToMany(mappedBy = "aihekokonaisuudet", cascade = {CascadeType.ALL})
    @OrderBy("jnro")
    private Set<Aihekokonaisuus> aihekokonaisuudet = new HashSet<>(0);

    public Aihekokonaisuudet copy(Opetussuunnitelma to, Aihekokonaisuudet parent) {
        Aihekokonaisuudet newAk = new Aihekokonaisuudet(to, this.uuidTunniste, parent);
        return copier().and(aihekokonaisuudetCopier(a -> a.copy(newAk, a))).copied(this, newAk);
    }

    public static Copier<Aihekokonaisuudet> aihekokonaisuudetCopier(ConstructedCopier<Aihekokonaisuus> copier) {
        return (from, to) -> {
            to.aihekokonaisuudet.clear();
            to.aihekokonaisuudet.addAll(from.aihekokonaisuudet.stream().map(copier::copy).collect(toSet()));
        };
    }

    public Aihekokonaisuudet copyInto(Aihekokonaisuudet to) {
        to.otsikko = this.otsikko;
        //to.yleiskuvaus = this.yleiskuvaus;
        return to;
    }
}
