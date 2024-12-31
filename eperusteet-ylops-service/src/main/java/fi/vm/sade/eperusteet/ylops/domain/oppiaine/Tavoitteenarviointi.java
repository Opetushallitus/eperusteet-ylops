package fi.vm.sade.eperusteet.ylops.domain.oppiaine;

import fi.vm.sade.eperusteet.ylops.domain.AbstractReferenceableEntity;
import fi.vm.sade.eperusteet.ylops.domain.teksti.LokalisoituTeksti;
import fi.vm.sade.eperusteet.ylops.domain.validation.ValidHtml;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

@Entity
@Table(name = "tavoitteen_arviointi")
@Audited
public class Tavoitteenarviointi extends AbstractReferenceableEntity {

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @Getter
    @Setter
    @ValidHtml(whitelist = ValidHtml.WhitelistType.MINIMAL)
    private LokalisoituTeksti arvioinninKohde;

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @Getter
    @Setter
    @ValidHtml(whitelist = ValidHtml.WhitelistType.MINIMAL)
    private LokalisoituTeksti osaamisenKuvaus;

    @Getter
    @Setter
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    private Integer arvosana;

    static Tavoitteenarviointi copyOf(Tavoitteenarviointi other) {
        Tavoitteenarviointi ta = new Tavoitteenarviointi();
        ta.setArvioinninKohde(other.getArvioinninKohde());
        ta.setOsaamisenKuvaus(other.getOsaamisenKuvaus());
        ta.setArvosana(other.getArvosana());
        return ta;
    }

    public Tavoitteenarviointi() {
        //JPA
    }


    public Tavoitteenarviointi(LokalisoituTeksti arvioinninKohde, LokalisoituTeksti osaamisenKuvaus, Integer arvosana) {
        this.arvioinninKohde = arvioinninKohde;
        this.osaamisenKuvaus = osaamisenKuvaus;
        this.arvosana = arvosana;
    }

}
