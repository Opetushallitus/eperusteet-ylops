package fi.vm.sade.eperusteet.ylops.domain.ops;

import fi.vm.sade.eperusteet.ylops.domain.oppiaine.Oppiaine;
import fi.vm.sade.eperusteet.ylops.service.util.LambdaUtil.ConstructedCopier;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@Embeddable
@EqualsAndHashCode(of = "oppiaine")
public class OpsOppiaine implements Serializable {

    @ManyToOne(optional = false, cascade = CascadeType.PERSIST)
    @NotNull
    private Oppiaine oppiaine;

    private Integer jnro;

    /**
     * Ilmaisee onko oppiaine oma vai lainattu. Vain omaa oppiainetta voidaan muokata,
     * lainatusta oppiaineesta täytyy ensin tehdä oma kopio ennen kuin muokkaus on mahdollista.
     */
    @Column(updatable = false)
    private boolean oma;

    public OpsOppiaine(Oppiaine oppiaine, boolean oma) {
        this.oppiaine = oppiaine;
        this.oma = oma;
    }

    protected OpsOppiaine() {
        //JPA
    }

    public static ConstructedCopier<OpsOppiaine> copier(ConstructedCopier<Oppiaine> oppiaineCopier, boolean oma) {
        return oa -> {
            OpsOppiaine uusi = new OpsOppiaine(oppiaineCopier.copy(oa.getOppiaine()), oma);
            uusi.setJnro(oa.getJnro());
            return uusi;
        };
    }
}
