package fi.vm.sade.eperusteet.ylops.domain.ops;


import fi.vm.sade.eperusteet.ylops.domain.dokumentti.Dokumentti;
import fi.vm.sade.eperusteet.ylops.domain.teksti.LokalisoituTeksti;
import fi.vm.sade.eperusteet.ylops.domain.validation.ValidHtml;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Immutable;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.hibernate.envers.RelationTargetAuditMode;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;


@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Immutable
@Getter
@Setter
@Table(name = "opetussuunnitelman_julkaisu")
public class OpetussuunnitelmanJulkaisu {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ops_id")
    @NotNull
    private Opetussuunnitelma opetussuunnitelma;

    @ValidHtml(whitelist = ValidHtml.WhitelistType.MINIMAL)
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @NotNull
    private LokalisoituTeksti tiedote;

    @ElementCollection
    @NotNull
    @Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
    @Immutable
    private Set<Long> dokumentit = new HashSet<>();

    @NotNull
    @Column(name = "peruste_json", nullable = false, updatable = false, columnDefinition = "text")
    private String opsData; // TODO: Käytä jsonb:tä

}
