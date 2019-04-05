package fi.vm.sade.eperusteet.ylops.domain.lops2019;

import com.google.common.primitives.UnsignedLong;
import fi.vm.sade.eperusteet.ylops.domain.AbstractAuditedReferenceableEntity;
import fi.vm.sade.eperusteet.ylops.domain.teksti.LokalisoituTeksti;
import fi.vm.sade.eperusteet.ylops.domain.validation.ValidHtml;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

@Entity
@Audited
@Table(name = "lops2019_opintojakso")
public class Lops2019Opintojakso extends AbstractAuditedReferenceableEntity {

    @Getter
    @Setter
    private String koodi;

    @Setter
    private Long laajuus;

    @Getter
    @Setter
    @NotEmpty
    @NotNull
    @ElementCollection
    // FIXME: @Koodi("oppiaineet")
    private Set<String> oppiaineet = new HashSet<>();

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @Getter
    @Setter
    @ValidHtml(whitelist = ValidHtml.WhitelistType.MINIMAL)
    private LokalisoituTeksti nimi;

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @Getter
    @Setter
    @ValidHtml(whitelist = ValidHtml.WhitelistType.NORMAL)
    private LokalisoituTeksti kuvaus;

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @Getter
    @Setter
    @ValidHtml(whitelist = ValidHtml.WhitelistType.NORMAL)
    private LokalisoituTeksti tavoitteet;

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @Getter
    @Setter
    @ValidHtml(whitelist = ValidHtml.WhitelistType.NORMAL)
    private LokalisoituTeksti keskeisetSisallot;

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @Getter
    @Setter
    @ValidHtml(whitelist = ValidHtml.WhitelistType.NORMAL)
    private LokalisoituTeksti laajaAlainenOsaaminen;

    @Getter
    @OrderColumn
    @OneToMany(fetch = FetchType.EAGER, cascade = {CascadeType.MERGE, CascadeType.PERSIST}, orphanRemoval = true)
    @JoinTable(name = "lops2019_opintojakso_moduuli",
            joinColumns = @JoinColumn(name = "opintojakso_id"),
            inverseJoinColumns = @JoinColumn(name = "moduuli_id"))
    private Set<Lops2019OpintojaksonModuuli> moduulit = new HashSet<>();

    public void setModuulit(Set<Lops2019OpintojaksonModuuli> moduulit) {
        this.moduulit.clear();
        this.moduulit.addAll(moduulit);
    }

    public Long getLaajuus() {
        if (this.getModuulit().size() > 0) {
            return null;
        }
        else {
            return laajuus;
        }
    }
}
