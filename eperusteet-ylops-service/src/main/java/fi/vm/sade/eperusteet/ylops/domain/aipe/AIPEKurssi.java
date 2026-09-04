package fi.vm.sade.eperusteet.ylops.domain.aipe;

import fi.vm.sade.eperusteet.ylops.domain.AbstractAuditedReferenceableEntity;
import fi.vm.sade.eperusteet.ylops.domain.HistoriaTapahtuma;
import fi.vm.sade.eperusteet.ylops.domain.teksti.LokalisoituTeksti;
import fi.vm.sade.eperusteet.ylops.domain.validation.ValidHtml;
import fi.vm.sade.eperusteet.ylops.dto.navigation.NavigationType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

@Entity
@Audited
@Table(name = "aipe_kurssi")
public class AIPEKurssi extends AbstractAuditedReferenceableEntity implements HistoriaTapahtuma {

    @Getter
    @Setter
    @NotNull
    @Column(name = "perusteen_kurssi_id", nullable = false)
    private Long perusteenKurssiId;

    @Getter
    @Setter
    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @ValidHtml(whitelist = ValidHtml.WhitelistType.NORMAL)
    private LokalisoituTeksti paikallinenTarkennus;

    @Getter
    @Setter
    @Column(nullable = false)
    private boolean piilotettu = false;

    @Getter
    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    private AIPEOppiaine oppiaine;

    @Getter
    @Setter
    @Column(name = "kurssit_order")
    private Integer kurssitOrder;

    public static AIPEKurssi copy(AIPEKurssi original) {
        if (original == null) {
            return null;
        }
        AIPEKurssi copy = new AIPEKurssi();
        copy.setPerusteenKurssiId(original.getPerusteenKurssiId());
        copy.setPaikallinenTarkennus(original.getPaikallinenTarkennus());
        copy.setPiilotettu(original.isPiilotettu());
        return copy;
    }

    @Override
    public LokalisoituTeksti getNimi() {
        return null;
    }

    @Override
    public NavigationType getNavigationType() {
        return NavigationType.aipekurssi;
    }
}
