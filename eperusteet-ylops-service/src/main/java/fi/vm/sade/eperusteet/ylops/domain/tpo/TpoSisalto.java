package fi.vm.sade.eperusteet.ylops.domain.tpo;

import fi.vm.sade.eperusteet.ylops.domain.AbstractAuditedEntity;
import fi.vm.sade.eperusteet.ylops.domain.ReferenceableEntity;
import fi.vm.sade.eperusteet.ylops.domain.ops.Opetussuunnitelma;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Audited
@Table(name = "tpo_sisalto")
public class TpoSisalto extends AbstractAuditedEntity implements ReferenceableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Getter
    @Setter
    private Long id;

    @Getter
    @Setter
    @OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinColumn(name = "opetussuunnitelma_id", nullable = false)
    private Opetussuunnitelma opetussuunnitelma;

    @Getter
    @OrderBy("taiteenalatOrder")
    @OneToMany(mappedBy = "sisalto", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Taiteenala> taiteenalat = new ArrayList<>();

    public void addTaiteenala(Taiteenala taiteenala) {
        taiteenala.setSisalto(this);
        taiteenalat.add(taiteenala);
        updateTaiteenalatOrder();
    }

    public void removeTaiteenala(Taiteenala taiteenala) {
        taiteenalat.remove(taiteenala);
    }

    public Taiteenala getTaiteenala(Long id) {
        return taiteenalat.stream()
                .filter(t -> Objects.equals(t.getId(), id))
                .findFirst()
                .orElse(null);
    }

    public boolean hasPerusteenTaiteenala(Long perusteenTaiteenalaId) {
        return taiteenalat.stream().anyMatch(t -> Objects.equals(t.getPerusteenTaiteenalaId(), perusteenTaiteenalaId));
    }

    public void updateTaiteenalatOrder() {
        for (int i = 0; i < taiteenalat.size(); i++) {
            taiteenalat.get(i).setTaiteenalatOrder(i);
        }
    }

    public void copyFrom(TpoSisalto other) {
        this.taiteenalat.clear();
        other.getTaiteenalat().stream()
                .map(Taiteenala::copy)
                .forEach(this::addTaiteenala);
    }
}
