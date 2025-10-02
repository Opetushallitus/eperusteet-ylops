package fi.vm.sade.eperusteet.ylops.domain.lops2019;

import fi.vm.sade.eperusteet.ylops.domain.AbstractAuditedReferenceableEntity;
import fi.vm.sade.eperusteet.ylops.domain.teksti.LokalisoituTeksti;
import fi.vm.sade.eperusteet.ylops.domain.validation.ValidHtml;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;
import org.hibernate.validator.constraints.NotEmpty;

import jakarta.persistence.*;

@Entity
@Audited
@Table(name = "paikallinen_laaja_alainen_osaaminen")
public class PaikallinenLaajaAlainenOsaaminen extends AbstractAuditedReferenceableEntity {

    @Getter
    @Setter
    @NotEmpty
    private String koodi;

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @Getter
    @Setter
    @ValidHtml(whitelist = ValidHtml.WhitelistType.NORMAL)
    private LokalisoituTeksti kuvaus;

    public static PaikallinenLaajaAlainenOsaaminen copy(PaikallinenLaajaAlainenOsaaminen original) {
        if (original == null) {
            return null;
        }

        PaikallinenLaajaAlainenOsaaminen copy = new PaikallinenLaajaAlainenOsaaminen();
        copy.setKoodi(original.getKoodi());
        copy.setKuvaus(original.getKuvaus());

        return copy;
    }

}
