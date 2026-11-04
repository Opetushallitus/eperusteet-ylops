package fi.vm.sade.eperusteet.ylops.domain.tpo;

import fi.vm.sade.eperusteet.ylops.domain.AbstractAuditedReferenceableEntity;
import fi.vm.sade.eperusteet.ylops.domain.HistoriaTapahtuma;
import fi.vm.sade.eperusteet.ylops.domain.oppiaine.VapaatekstiPaikallinentarkennus;
import fi.vm.sade.eperusteet.ylops.domain.teksti.LokalisoituTeksti;
import fi.vm.sade.eperusteet.ylops.dto.navigation.NavigationType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Audited
@Table(name = "tpo_taiteenala_vaihe",
        uniqueConstraints = @UniqueConstraint(columnNames = {"taiteenala_id", "perusteen_taiteenala_vaihe_id"}))
public class TaiteenalanVaihe extends AbstractAuditedReferenceableEntity implements HistoriaTapahtuma {

    @Getter
    @Setter
    @NotNull
    @Column(name = "perusteen_taiteenala_vaihe_id", nullable = false)
    private Long perusteenTaiteenalaVaiheId;

    @Getter
    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "taiteenala_id", nullable = false)
    private Taiteenala taiteenala;

    @Getter
    @Setter
    @Column(name = "vaiheet_order")
    private Integer vaiheetOrder;

    @Getter
    @OneToMany(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST}, orphanRemoval = true)
    @JoinTable(name = "tpo_taiteenala_vaihe_vapaatekstit",
            joinColumns = @JoinColumn(name = "vaihe_id"),
            inverseJoinColumns = @JoinColumn(name = "vapaateksti_paikallinentarkennus_id"))
    private List<VapaatekstiPaikallinentarkennus> vapaatTekstit = new ArrayList<>();

    @Getter
    @OrderBy("opintokokonaisuudetOrder")
    @OneToMany(mappedBy = "vaihe", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TaiteenOpintokokonaisuus> opintokokonaisuudet = new ArrayList<>();

    public void setVapaatTekstit(List<VapaatekstiPaikallinentarkennus> vapaatTekstit) {
        this.vapaatTekstit.clear();
        if (vapaatTekstit != null) {
            this.vapaatTekstit.addAll(vapaatTekstit);
        }
    }

    public void addOpintokokonaisuus(TaiteenOpintokokonaisuus opintokokonaisuus) {
        opintokokonaisuus.setVaihe(this);
        opintokokonaisuudet.add(opintokokonaisuus);
        updateOpintokokonaisuudetOrder();
    }

    public void updateOpintokokonaisuudetOrder() {
        for (int i = 0; i < opintokokonaisuudet.size(); i++) {
            opintokokonaisuudet.get(i).setOpintokokonaisuudetOrder(i);
        }
    }

    public void removeOpintokokonaisuus(TaiteenOpintokokonaisuus opintokokonaisuus) {
        opintokokonaisuudet.remove(opintokokonaisuus);
    }

    public TaiteenOpintokokonaisuus getOpintokokonaisuus(Long id) {
        return opintokokonaisuudet.stream()
                .filter(o -> Objects.equals(o.getId(), id))
                .findFirst()
                .orElse(null);
    }

    public static TaiteenalanVaihe copy(TaiteenalanVaihe original) {
        TaiteenalanVaihe copy = new TaiteenalanVaihe();
        copy.setPerusteenTaiteenalaVaiheId(original.getPerusteenTaiteenalaVaiheId());
        original.getVapaatTekstit().forEach(vt -> {
            VapaatekstiPaikallinentarkennus vtCopy = new VapaatekstiPaikallinentarkennus();
            vtCopy.setPerusteenVapaaTekstiId(vt.getPerusteenVapaaTekstiId());
            vtCopy.setPaikallinenTarkennus(vt.getPaikallinenTarkennus());
            copy.getVapaatTekstit().add(vtCopy);
        });
        original.getOpintokokonaisuudet().stream()
                .map(TaiteenOpintokokonaisuus::copy)
                .forEach(copy::addOpintokokonaisuus);
        return copy;
    }

    @Override
    public LokalisoituTeksti getNimi() {
        return null;
    }

    @Override
    public NavigationType getNavigationType() {
        return NavigationType.taiteenalanvaihe;
    }
}
