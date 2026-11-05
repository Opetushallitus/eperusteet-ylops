package fi.vm.sade.eperusteet.ylops.domain.tpo;

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
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import java.util.ArrayList;
import java.util.List;

@Entity
@Audited
@Table(name = "tpo_opintokokonaisuus")
public class TaiteenOpintokokonaisuus extends AbstractAuditedReferenceableEntity implements HistoriaTapahtuma {

    @Getter
    @Setter
    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    private LokalisoituTeksti nimi;

    @Getter
    @Setter
    private Long laajuus;

    @Getter
    @Setter
    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @ValidHtml(whitelist = ValidHtml.WhitelistType.NORMAL)
    private LokalisoituTeksti osaamisenArviointi;

    @Getter
    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vaihe_id", nullable = false)
    private TaiteenalanVaihe vaihe;

    @Getter
    @Setter
    @Column(name = "opintokokonaisuudet_order")
    private Integer opintokokonaisuudetOrder;

    @Getter
    @ElementCollection
    @CollectionTable(name = "tpo_opintokokonaisuus_tavoitealue", joinColumns = @JoinColumn(name = "opintokokonaisuus_id"))
    @Column(name = "tavoitealue_id")
    private List<Long> tavoitealueIdt = new ArrayList<>();

    public void setTavoitealueIdt(List<Long> tavoitealueIdt) {
        this.tavoitealueIdt.clear();
        if (tavoitealueIdt != null) {
            this.tavoitealueIdt.addAll(tavoitealueIdt);
        }
    }

    public static TaiteenOpintokokonaisuus copy(TaiteenOpintokokonaisuus original) {
        TaiteenOpintokokonaisuus copy = new TaiteenOpintokokonaisuus();
        copy.setNimi(original.getNimi());
        copy.setLaajuus(original.getLaajuus());
        copy.setOsaamisenArviointi(original.getOsaamisenArviointi());
        copy.setTavoitealueIdt(original.getTavoitealueIdt());
        return copy;
    }

    @Override
    public NavigationType getNavigationType() {
        return NavigationType.taiteenopintokokonaisuus;
    }
}
