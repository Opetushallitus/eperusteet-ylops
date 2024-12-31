package fi.vm.sade.eperusteet.ylops.domain.ops;

import fi.vm.sade.eperusteet.ylops.domain.vuosiluokkakokonaisuus.Vuosiluokkakokonaisuus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import java.io.Serializable;

@Embeddable
@EqualsAndHashCode(of = "vuosiluokkakokonaisuus")
public class OpsVuosiluokkakokonaisuus implements Serializable {

    @Getter
    @Setter
    @ManyToOne(optional = false, cascade = {CascadeType.PERSIST})
    @NotNull
    private Vuosiluokkakokonaisuus vuosiluokkakokonaisuus;

    /**
     * Ilmaisee onko vuosiluokkakokonaisuus oma vai lainattu. Vain omaa vuosiluokkakokonaisuutta
     * voidaan muokata, lainatusta kokonaisuudesta täytyy ensin tehdä oma kopio ennen kuin
     * muokkaus on mahdollista.
     */
    @Getter
    @Setter
    @Column(updatable = false)
    private boolean oma;

    @Getter
    @Setter
    @OneToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST}, orphanRemoval = true)
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    private OpsVuosiluokkakokonaisuusLisatieto lisatieto;

    protected OpsVuosiluokkakokonaisuus() {
        //JPA
    }

    public OpsVuosiluokkakokonaisuus(Vuosiluokkakokonaisuus vuosiluokkakokonaisuus, boolean oma) {
        this.oma = oma;
        this.vuosiluokkakokonaisuus = vuosiluokkakokonaisuus;
    }

}
