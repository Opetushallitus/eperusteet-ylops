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
import org.apache.commons.lang.StringUtils;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Audited
@Table(name = "lops2019_opintojakso")
public class Lops2019Opintojakso extends AbstractAuditedReferenceableEntity implements Validable {

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

    @Getter
    @OneToMany(fetch = FetchType.EAGER, cascade = {CascadeType.MERGE, CascadeType.PERSIST}, orphanRemoval = true)
    @NotEmpty
    @JoinTable(name = "lops2019_opintojakso_oppiaine",
            joinColumns = @JoinColumn(name = "opintojakso_id"),
            inverseJoinColumns = @JoinColumn(name = "oj_oppiaine_id"))
    private Set<Lops2019OpintojaksonOppiaine> oppiaineet = new HashSet<>();

    public void setModuulit(Set<Lops2019OpintojaksonModuuli> moduulit) {
        this.moduulit.clear();
        this.moduulit.addAll(moduulit);
    }

    public void setOppiaineet(Set<Lops2019OpintojaksonOppiaine> oppiaineet) {
        this.oppiaineet.clear();
        this.oppiaineet.addAll(oppiaineet);
    }


    @Override
    public void validate(ValidointiDto validointi, ValidointiContext ctx) {
        validointi.virhe("koodi-puuttuu", this, StringUtils.isEmpty(getKoodi()));
        validointi.virhe("nimi-oltava-kaikilla-julkaisukielilla", this, getNimi() == null || !getNimi().hasKielet(ctx.getKielet()));
        validointi.varoitus("kuvausta-ei-ole-kirjoitettu-kaikilla-julkaisukielilla", this, getNimi() == null || !getNimi().hasKielet(ctx.getKielet()));

        boolean isIntegraatioOpintojakso = getOppiaineet().size() > 1 || getModuulit().isEmpty();
        if (isIntegraatioOpintojakso) {
            boolean isValid = true;
            Long yhteensa = 0L;
            for (Lops2019OpintojaksonOppiaine oa : getOppiaineet()) {
                Long laajuus = oa.getLaajuus();
                if (laajuus == null || laajuus < 1 || laajuus > 4) {
                    isValid = false;
                }
                else {
                    yhteensa += laajuus;
                }
            }
            validointi.virhe("opintojaksolla-virheellinen-laajuus", this, !isValid || yhteensa < 1 || yhteensa > 4);
        }
    }

    @Override
    public ValidationCategory category() {
        return ValidationCategory.OPINTOJAKSO;
    }
}
