package fi.vm.sade.eperusteet.ylops.domain.ops;

import fi.vm.sade.eperusteet.ylops.domain.AbstractReferenceableEntity;
import fi.vm.sade.eperusteet.ylops.domain.teksti.LokalisoituTeksti;
import fi.vm.sade.eperusteet.ylops.domain.validation.ValidHtml;
import fi.vm.sade.eperusteet.ylops.service.util.SecurityUtil;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Immutable;

import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Immutable
@Getter
@Setter
@Table(name = "opetussuunnitelman_julkaisu")
public class OpetussuunnitelmanJulkaisu extends AbstractReferenceableEntity {

    @NotNull
    private int revision;

    @ManyToOne
    @JoinColumn(name = "ops_id")
    @NotNull
    private Opetussuunnitelma opetussuunnitelma;

    @ValidHtml(whitelist = ValidHtml.WhitelistType.SIMPLIFIED)
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private LokalisoituTeksti tiedote;

    @Temporal(TemporalType.TIMESTAMP)
    private Date luotu;

    @Getter
    @NotNull
    private String luoja;

    @ElementCollection
    @NotNull
    @Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
    private Set<Long> dokumentit = new HashSet<>();

    @NotNull
    @OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.ALL}, orphanRemoval = true)
    private JulkaistuOpetussuunnitelmaData data;

    @PrePersist
    private void prepersist() {
        if (luotu == null) {
            luotu = new Date();
        }
        if (luoja == null) {
            luoja = SecurityUtil.getAuthenticatedPrincipal().getName();
        }
    }

}
