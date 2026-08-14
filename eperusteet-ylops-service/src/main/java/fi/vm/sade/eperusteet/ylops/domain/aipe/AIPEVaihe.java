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
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Audited
@Table(name = "aipe_vaihe",
        uniqueConstraints = @UniqueConstraint(columnNames = {"sisalto_id", "perusteen_vaihe_id"}))
public class AIPEVaihe extends AbstractAuditedReferenceableEntity implements HistoriaTapahtuma {

    @Getter
    @Setter
    @NotNull
    @Column(name = "perusteen_vaihe_id", nullable = false)
    private Long perusteenVaiheId;

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
    @JoinColumn(name = "sisalto_id", nullable = false)
    private AIPESisalto sisalto;

    @Getter
    @OrderColumn(name = "oppiaineet_order")
    @OneToMany(mappedBy = "vaihe", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AIPEOppiaine> oppiaineet = new ArrayList<>();

    public void setOppiaineet(List<AIPEOppiaine> oppiaineet) {
        this.oppiaineet.clear();
        if (oppiaineet != null) {
            oppiaineet.forEach(oa -> oa.setVaihe(this));
            this.oppiaineet.addAll(oppiaineet);
        }
    }

    public static AIPEVaihe copy(AIPEVaihe original) {
        if (original == null) {
            return null;
        }
        AIPEVaihe copy = new AIPEVaihe();
        copy.setPerusteenVaiheId(original.getPerusteenVaiheId());
        copy.setPaikallinenTarkennus(original.getPaikallinenTarkennus());
        copy.setPiilotettu(original.isPiilotettu());
        copy.setOppiaineet(original.getOppiaineet().stream().map(AIPEOppiaine::copy).collect(Collectors.toList()));
        return copy;
    }

    @Override
    public LokalisoituTeksti getNimi() {
        return null;
    }

    @Override
    public NavigationType getNavigationType() {
        return NavigationType.aipevaihe;
    }
}
