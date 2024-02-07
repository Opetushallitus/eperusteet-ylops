package fi.vm.sade.eperusteet.ylops.domain.teksti;

import fi.vm.sade.eperusteet.ylops.domain.validation.ValidHtml;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serializable;

@Audited
@Table(name = "tekstiosa")
@Entity
public class Tekstiosa implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Getter
    private Long id;

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST}, fetch = FetchType.LAZY)
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @Getter
    @Setter
    @ValidHtml(whitelist = ValidHtml.WhitelistType.MINIMAL)
    private LokalisoituTeksti otsikko;

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST}, fetch = FetchType.LAZY)
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @Getter
    @Setter
    @ValidHtml
    private LokalisoituTeksti teksti;

    public Tekstiosa() {
    }

    public Tekstiosa(LokalisoituTeksti otsikko, LokalisoituTeksti teksti) {
        this.otsikko = otsikko;
        this.teksti = teksti;
    }

    public Tekstiosa(Tekstiosa other) {
        this.otsikko = other.getOtsikko();
        this.teksti = other.getTeksti();
    }

    public static Tekstiosa copyOf(Tekstiosa other) {
        if (other == null) return null;
        return new Tekstiosa(other);
    }

}
