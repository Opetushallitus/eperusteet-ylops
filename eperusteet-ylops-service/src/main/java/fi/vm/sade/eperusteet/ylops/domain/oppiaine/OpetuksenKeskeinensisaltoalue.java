package fi.vm.sade.eperusteet.ylops.domain.oppiaine;

import fi.vm.sade.eperusteet.ylops.domain.teksti.LokalisoituTeksti;
import fi.vm.sade.eperusteet.ylops.domain.validation.ValidHtml;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import java.io.Serializable;

/**
 * Created by autio on 28.10.2015.
 */
@EqualsAndHashCode
@Entity
@Audited
@Table(name = "opetuksen_tavoite_keskeinen_sisaltoalue")
public class OpetuksenKeskeinensisaltoalue implements Serializable {

    @Getter
    @Setter
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Getter
    @Setter
    @ManyToOne(optional = false, cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinColumn(name = "sisaltoalueet_id")
    @NotNull
    private Keskeinensisaltoalue sisaltoalueet;

    @Getter
    @Setter
    @ManyToOne(optional = false, cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinColumn(name = "opetuksen_tavoite_id")
    @NotNull
    private Opetuksentavoite opetuksentavoite;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @Getter
    @Setter
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @ValidHtml(whitelist = ValidHtml.WhitelistType.NORMAL)
    private LokalisoituTeksti omaKuvaus;

    public static OpetuksenKeskeinensisaltoalue copyOf(OpetuksenKeskeinensisaltoalue other, Opetuksentavoite opetuksentavoite) {
        OpetuksenKeskeinensisaltoalue ks = new OpetuksenKeskeinensisaltoalue();
        ks.setOmaKuvaus(other.getOmaKuvaus());
        ks.setSisaltoalueet(Keskeinensisaltoalue.copyOf(other.getSisaltoalueet()));
        ks.setOpetuksentavoite(opetuksentavoite);
        return ks;
    }

}
