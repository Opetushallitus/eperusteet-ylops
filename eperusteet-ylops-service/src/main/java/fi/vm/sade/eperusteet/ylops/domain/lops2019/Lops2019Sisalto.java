package fi.vm.sade.eperusteet.ylops.domain.lops2019;

import fi.vm.sade.eperusteet.ylops.domain.AbstractAuditedEntity;
import fi.vm.sade.eperusteet.ylops.domain.ReferenceableEntity;
import fi.vm.sade.eperusteet.ylops.domain.ops.Opetussuunnitelma;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Audited
@Table(name = "lops2019_sisalto")
public class Lops2019Sisalto extends AbstractAuditedEntity implements Serializable, ReferenceableEntity {

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
    @OrderColumn
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(name = "lops2019_sisalto_opintojakso",
        joinColumns = @JoinColumn(name = "sisalto_id"),
        inverseJoinColumns = @JoinColumn(name = "opintojakso_id"))
    private Set<Lops2019Opintojakso> opintojaksot = new HashSet<>();

    @Getter
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE})
    @JoinTable(name = "lops2019_sisalto_piilotettu_opintojakso",
            joinColumns = @JoinColumn(name = "sisalto_id"),
            inverseJoinColumns = @JoinColumn(name = "opintojakso_id"))
    private Set<Lops2019Opintojakso> piilotetutOpintojaksot = new HashSet<>();

    @Getter
    @OrderColumn
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(name = "lops2019_sisalto_oppiaine_jarjestys",
            joinColumns = @JoinColumn(name = "sisalto_id"),
            inverseJoinColumns = @JoinColumn(name = "oppiaine_jarjestys_id"))
    private Set<Lops2019OppiaineJarjestys> oppiaineJarjestykset = new HashSet<>();

    public void addOpintojakso(Lops2019Opintojakso opintojakso) {
        opintojaksot.add(opintojakso);
    }

    public Lops2019Opintojakso getOpintojakso(Long id) {
        return this.opintojaksot.stream()
                .filter(oj -> id.equals(oj.getId()))
                .findFirst().orElse(null);
    }

}
