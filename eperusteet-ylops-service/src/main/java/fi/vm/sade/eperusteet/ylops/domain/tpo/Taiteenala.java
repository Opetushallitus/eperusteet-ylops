package fi.vm.sade.eperusteet.ylops.domain.tpo;

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
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Audited
@Table(name = "tpo_taiteenala",
        uniqueConstraints = @UniqueConstraint(columnNames = {"sisalto_id", "perusteen_taiteenala_id"}))
public class Taiteenala extends AbstractAuditedReferenceableEntity implements HistoriaTapahtuma {

    @Getter
    @Setter
    @NotNull
    @Column(name = "perusteen_taiteenala_id", nullable = false)
    private Long perusteenTaiteenalaId;

    @Getter
    @Setter
    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @ValidHtml(whitelist = ValidHtml.WhitelistType.NORMAL)
    private LokalisoituTeksti paikallinenTarkennus;

    @Getter
    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sisalto_id", nullable = false)
    private TpoSisalto sisalto;

    @Getter
    @Setter
    @Column(name = "taiteenalat_order")
    private Integer taiteenalatOrder;

    @Getter
    @OrderBy("vaiheetOrder")
    @OneToMany(mappedBy = "taiteenala", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TaiteenalanVaihe> vaiheet = new ArrayList<>();

    public void addVaihe(TaiteenalanVaihe vaihe) {
        vaihe.setTaiteenala(this);
        vaiheet.add(vaihe);
        updateVaiheetOrder();
    }

    public TaiteenalanVaihe getVaihe(Long id) {
        return vaiheet.stream()
                .filter(v -> Objects.equals(v.getId(), id))
                .findFirst()
                .orElse(null);
    }

    public void updateVaiheetOrder() {
        for (int i = 0; i < vaiheet.size(); i++) {
            vaiheet.get(i).setVaiheetOrder(i);
        }
    }

    public static Taiteenala copy(Taiteenala original) {
        Taiteenala copy = new Taiteenala();
        copy.setPerusteenTaiteenalaId(original.getPerusteenTaiteenalaId());
        copy.setPaikallinenTarkennus(original.getPaikallinenTarkennus());
        original.getVaiheet().stream()
                .map(TaiteenalanVaihe::copy)
                .forEach(copy::addVaihe);
        return copy;
    }

    @Override
    public LokalisoituTeksti getNimi() {
        return null;
    }

    @Override
    public NavigationType getNavigationType() {
        return NavigationType.taiteenala;
    }
}
