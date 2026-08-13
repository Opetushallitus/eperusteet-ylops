package fi.vm.sade.eperusteet.ylops.domain.tpo;

import fi.vm.sade.eperusteet.ylops.domain.AbstractAuditedReferenceableEntity;
import fi.vm.sade.eperusteet.ylops.domain.Kooditettu;
import fi.vm.sade.eperusteet.ylops.domain.teksti.LokalisoituTeksti;
import fi.vm.sade.eperusteet.ylops.domain.validation.ValidHtml;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Audited
@Table(name = "tpo_taiteenala")
public class Taiteenala extends AbstractAuditedReferenceableEntity implements Kooditettu {

    @Getter
    @Setter
    @Column(updatable = false)
    private String koodi;

    @Override
    public String getUri() {
        return koodi;
    }

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @JoinColumn(name = "paikallinen_tarkennus_id")
    @Getter
    @Setter
    @ValidHtml(whitelist = ValidHtml.WhitelistType.NORMAL)
    private LokalisoituTeksti paikallinenTarkennus;

    @Getter
    @OrderColumn(name = "taiteenosa_jarjestys")
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(name = "tpo_taiteenala_taiteenosa",
            joinColumns = @JoinColumn(name = "taiteenala_id"),
            inverseJoinColumns = @JoinColumn(name = "taiteenosa_id"))
    private List<Taiteenosa> taiteenosat = new ArrayList<>();

    public void setTaiteenosat(List<Taiteenosa> taiteenosat) {
        this.taiteenosat.clear();
        if (taiteenosat != null) {
            this.taiteenosat.addAll(taiteenosat);
        }
    }

    public static Taiteenala copy(Taiteenala original) {
        if (original == null) {
            return null;
        }
        Taiteenala result = new Taiteenala();
        result.setKoodi(original.getKoodi());
        result.setPaikallinenTarkennus(original.getPaikallinenTarkennus());
        result.setTaiteenosat(original.getTaiteenosat().stream()
                .map(Taiteenosa::copy)
                .collect(Collectors.toList()));
        return result;
    }
}
