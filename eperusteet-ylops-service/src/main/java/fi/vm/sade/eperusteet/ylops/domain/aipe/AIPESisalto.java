package fi.vm.sade.eperusteet.ylops.domain.aipe;

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
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
@Audited
@Table(name = "aipe_sisalto")
public class AIPESisalto extends AbstractAuditedEntity implements ReferenceableEntity {

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
    @OrderColumn(name = "vaiheet_order")
    @OneToMany(mappedBy = "sisalto", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AIPEVaihe> vaiheet = new ArrayList<>();

    public void addVaihe(AIPEVaihe vaihe) {
        vaihe.setSisalto(this);
        vaiheet.add(vaihe);
    }

    public AIPEVaihe getVaihe(Long id) {
        return vaiheet.stream()
                .filter(v -> v.getId() != null && v.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public void copyFrom(AIPESisalto other) {
        copyFrom(other, null);
    }

    public void copyFrom(AIPESisalto other, Set<Long> perusteenVaiheIdt) {
        this.vaiheet.clear();
        other.getVaiheet().stream()
                .filter(v -> perusteenVaiheIdt == null || perusteenVaiheIdt.contains(v.getPerusteenVaiheId()))
                .map(AIPEVaihe::copy)
                .forEach(this::addVaihe);
    }
}
