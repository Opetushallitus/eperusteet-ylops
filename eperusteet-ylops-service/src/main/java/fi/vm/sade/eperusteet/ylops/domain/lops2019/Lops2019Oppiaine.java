package fi.vm.sade.eperusteet.ylops.domain.lops2019;


import fi.vm.sade.eperusteet.ylops.domain.AbstractAuditedReferenceableEntity;
import fi.vm.sade.eperusteet.ylops.domain.Validable;
import fi.vm.sade.eperusteet.ylops.domain.ValidationCategory;
import fi.vm.sade.eperusteet.ylops.domain.teksti.LokalisoituTeksti;
import fi.vm.sade.eperusteet.ylops.domain.validation.ValidHtml;
import fi.vm.sade.eperusteet.ylops.dto.lops2019.Lops2019OppiaineenArviointi;
import fi.vm.sade.eperusteet.ylops.dto.lops2019.Validointi.ValidointiContext;
import fi.vm.sade.eperusteet.ylops.dto.lops2019.Validointi.ValidointiDto;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Audited
@Table(name = "lops2019_oppiaine")
public class Lops2019Oppiaine extends AbstractAuditedReferenceableEntity implements Validable {

    @ManyToOne
    @Getter
    @NotNull
    private Lops2019Sisalto sisalto;

    @Getter
    @Setter
    @Column(name = "perusteen_oppiaine_uri", updatable = false)
    private String perusteenOppiaineUri;

    @Getter
    @Setter
    private String koodi;

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
    private LokalisoituTeksti pakollistenModuulienKuvaus;

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @Getter
    @Setter
    @ValidHtml(whitelist = ValidHtml.WhitelistType.NORMAL)
    private LokalisoituTeksti valinnaistenModuulienKuvaus;

    @Getter
    @Setter
    @OneToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST}, orphanRemoval = true)
    private Lops2019PaikallinenArviointi arviointi;

    @Getter
    @Setter
    @OneToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST}, orphanRemoval = true)
    private Lops2019LaajaAlainenOsaaminen laajaAlainenOsaaminen;

    @Getter
    @Setter
    @OneToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST}, orphanRemoval = true)
    private Lops2019Tehtava tehtava;

    @Getter
    @Setter
    @OneToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST}, orphanRemoval = true)
    @JoinTable(name = "lops2019_oppiaine_tavoitteet")
    private Lops2019Tavoitteet tavoitteet;

    public void setSisalto(Lops2019Sisalto uusi) {
        if (this.sisalto == null) {
            this.sisalto = uusi;
        }
    }

    @Override
    public void validate(ValidointiDto validointi, ValidointiContext ctx) {
    }

    @Override
    public ValidationCategory category() {
        return ValidationCategory.OPPIAINE;
    }
}
