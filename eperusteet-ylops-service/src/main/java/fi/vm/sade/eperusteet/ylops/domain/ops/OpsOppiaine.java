package fi.vm.sade.eperusteet.ylops.domain.ops;

import fi.vm.sade.eperusteet.ylops.domain.oppiaine.Oppiaine;
import fi.vm.sade.eperusteet.ylops.service.util.LambdaUtil.ConstructedCopier;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Embeddable
@EqualsAndHashCode(of = "oppiaine")
public class OpsOppiaine implements Serializable {

    @Getter
    @Setter
    @ManyToOne(optional = false, cascade = CascadeType.PERSIST)
    @NotNull
    private Oppiaine oppiaine;

    @Getter
    @Setter
    private Integer jnro;

    /**
     * Ilmaisee onko oppiaine oma vai lainattu. Vain omaa oppiainetta voidaan muokata,
     * lainatusta oppiaineesta täytyy ensin tehdä oma kopio ennen kuin muokkaus on mahdollista.
     */
    @Getter
    @Setter
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
