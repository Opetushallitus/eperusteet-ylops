package fi.vm.sade.eperusteet.ylops.domain.aipe;

import fi.vm.sade.eperusteet.ylops.domain.AbstractAuditedReferenceableEntity;
import fi.vm.sade.eperusteet.ylops.domain.HistoriaTapahtuma;
import fi.vm.sade.eperusteet.ylops.domain.teksti.LokalisoituTeksti;
import fi.vm.sade.eperusteet.ylops.domain.validation.ValidHtml;
import fi.vm.sade.eperusteet.ylops.dto.navigation.NavigationType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
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
@Table(name = "aipe_oppiaine")
public class AIPEOppiaine extends AbstractAuditedReferenceableEntity implements HistoriaTapahtuma {

    @Getter
    @Setter
    @NotNull
    @Column(name = "perusteen_oppiaine_id", nullable = false)
    private Long perusteenOppiaineId;

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
    private AIPEVaihe vaihe;

    @Getter
    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    private AIPEOppiaine parent;

    @Getter
    @OrderColumn(name = "kurssit_order")
    @OneToMany(mappedBy = "oppiaine", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AIPEKurssi> kurssit = new ArrayList<>();

    @Getter
    @OrderColumn(name = "oppimaarat_order")
    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AIPEOppiaine> oppimaarat = new ArrayList<>();

    @Getter
    @ElementCollection
    @CollectionTable(name = "aipe_oppiaine_piilotettu_tavoite", joinColumns = @JoinColumn(name = "oppiaine_id"))
    @Column(name = "tavoite_id")
    private List<Long> piilotetutTavoitteet = new ArrayList<>();

    public void setKurssit(List<AIPEKurssi> kurssit) {
        this.kurssit.clear();
        if (kurssit != null) {
            kurssit.forEach(k -> k.setOppiaine(this));
            this.kurssit.addAll(kurssit);
        }
    }

    public void setOppimaarat(List<AIPEOppiaine> oppimaarat) {
        this.oppimaarat.clear();
        if (oppimaarat != null) {
            oppimaarat.forEach(om -> om.setParent(this));
            this.oppimaarat.addAll(oppimaarat);
        }
    }

    public static AIPEOppiaine copy(AIPEOppiaine original) {
        if (original == null) {
            return null;
        }
        AIPEOppiaine copy = new AIPEOppiaine();
        copy.setPerusteenOppiaineId(original.getPerusteenOppiaineId());
        copy.setPaikallinenTarkennus(original.getPaikallinenTarkennus());
        copy.setPiilotettu(original.isPiilotettu());
        copy.getPiilotetutTavoitteet().addAll(original.getPiilotetutTavoitteet());
        copy.setKurssit(original.getKurssit().stream().map(AIPEKurssi::copy).collect(Collectors.toList()));
        copy.setOppimaarat(original.getOppimaarat().stream().map(AIPEOppiaine::copy).collect(Collectors.toList()));
        return copy;
    }

    @Override
    public LokalisoituTeksti getNimi() {
        return null;
    }

    @Override
    public NavigationType getNavigationType() {
        return parent != null ? NavigationType.aipeoppimaara : NavigationType.aipeoppiaine;
    }
}
