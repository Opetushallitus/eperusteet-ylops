package fi.vm.sade.eperusteet.ylops.domain.lops2019;

import com.google.common.collect.Sets;
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
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.springframework.web.bind.annotation.Mapping;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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
    @Setter
    @OneToMany(mappedBy = "sisalto", fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST})
    private Set<Lops2019Oppiaine> oppiaineet;

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

    public void copyFrom(Lops2019Sisalto other) {
        this.opintojaksot = other.getOpintojaksot().stream().map(Lops2019Opintojakso::copy).collect(Collectors.toSet());
        this.piilotetutOpintojaksot = other.getPiilotetutOpintojaksot().stream().map(Lops2019Opintojakso::copy).collect(Collectors.toSet());
        this.oppiaineJarjestykset = other.getOppiaineJarjestykset().stream().map(Lops2019OppiaineJarjestys::copy).collect(Collectors.toSet());
        this.oppiaineet = Optional.ofNullable(other.getOppiaineet()).orElse(Sets.newHashSet()).stream().map(Lops2019Oppiaine::copy)
                .peek(oa -> oa.setSisalto(this))
                .collect(Collectors.toSet());
    }
}
