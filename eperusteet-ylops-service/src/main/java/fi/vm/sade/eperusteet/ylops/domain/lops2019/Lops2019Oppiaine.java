package fi.vm.sade.eperusteet.ylops.domain.lops2019;


import fi.vm.sade.eperusteet.ylops.domain.AbstractAuditedReferenceableEntity;
import fi.vm.sade.eperusteet.ylops.domain.Validable;
import fi.vm.sade.eperusteet.ylops.domain.ValidationCategory;
import fi.vm.sade.eperusteet.ylops.domain.teksti.LokalisoituTeksti;
import fi.vm.sade.eperusteet.ylops.domain.validation.ValidHtml;
import fi.vm.sade.eperusteet.ylops.dto.lops2019.Validointi.ValidointiContext;
import fi.vm.sade.eperusteet.ylops.dto.lops2019.Validointi.ValidointiDto;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;

@Entity
@Audited
@Table(name = "lops2019_oppiaine",
       uniqueConstraints = @UniqueConstraint(columnNames = { "koodi", "sisalto_id" }))
public class Lops2019Oppiaine extends AbstractAuditedReferenceableEntity implements Validable {

    @ManyToOne
    @Getter
    @NotNull
    private Lops2019Sisalto sisalto;

    @Getter
    @Setter
    @Column(name = "perusteen_oppiaine_uri")
    private String perusteenOppiaineUri;

    @Getter
    @Setter
    @NotEmpty
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
    @OneToMany(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST}, orphanRemoval = true)
    @OrderColumn
    private List<PaikallinenLaajaAlainenOsaaminen> laajaAlainenOsaaminen = new ArrayList<>();

    @Getter
    @Setter
    @OneToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST}, orphanRemoval = true)
    private Lops2019Tehtava tehtava;

    @Getter
    @Setter
    @OneToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST}, orphanRemoval = true)
    @JoinTable(name = "lops2019_oppiaine_tavoitteet")
    private Lops2019Tavoitteet tavoitteet;

    void setLaajaAlainenOsaaminen(Collection<PaikallinenLaajaAlainenOsaaminen> osaamiset) {
        if (laajaAlainenOsaaminen == null) {
            laajaAlainenOsaaminen = new ArrayList<>();
        }
        laajaAlainenOsaaminen.clear();
        laajaAlainenOsaaminen.addAll(osaamiset);
    }

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

    static public Lops2019Oppiaine copy(Lops2019Oppiaine original) {
        if (original != null) {
            Lops2019Oppiaine result = new Lops2019Oppiaine();
            result.setPerusteenOppiaineUri(original.getPerusteenOppiaineUri());
            result.setKoodi(original.getKoodi());
            result.setNimi(original.getNimi());
            result.setKuvaus(original.getKuvaus());
            result.setPakollistenModuulienKuvaus(original.getPakollistenModuulienKuvaus());
            result.setValinnaistenModuulienKuvaus(original.getPakollistenModuulienKuvaus());
            result.setLaajaAlainenOsaaminen(original.getLaajaAlainenOsaaminen());
            result.setArviointi(Lops2019PaikallinenArviointi.copy(original.getArviointi()));
            result.setTehtava(Lops2019Tehtava.copy(original.getTehtava()));
            result.setTavoitteet(Lops2019Tavoitteet.copy(original.getTavoitteet()));
            return result;
        }
        return null;
    }

}
