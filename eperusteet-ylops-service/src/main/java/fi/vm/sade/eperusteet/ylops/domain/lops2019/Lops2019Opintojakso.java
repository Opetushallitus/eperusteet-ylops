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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
    private LokalisoituTeksti arviointi;

    public void setTavoitteet(List<Lops2019OpintojaksonTavoite> tavoitteet) {
        this.tavoitteet.clear();
        this.tavoitteet.addAll(tavoitteet);
    }

    public void setKeskeisetSisallot(List<Lops2019OpintojaksonKeskeinenSisalto> keskeisetSisallot) {
        this.keskeisetSisallot.clear();
        this.keskeisetSisallot.addAll(keskeisetSisallot);
    }

    @Getter
    @OrderColumn
    @OneToMany(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST}, orphanRemoval = true)
    @JoinTable(name = "lops2019_opintojakso_tavoite",
            joinColumns = @JoinColumn(name = "opintojakso_id"),
            inverseJoinColumns = @JoinColumn(name = "tavoite_id"))
    private List<Lops2019OpintojaksonTavoite> tavoitteet = new ArrayList<>();

    @Getter
    @OrderColumn
    @OneToMany(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST}, orphanRemoval = true)
    @JoinTable(name = "lops2019_opintojakso_sisalto",
            joinColumns = @JoinColumn(name = "opintojakso_id"),
            inverseJoinColumns = @JoinColumn(name = "keskeinen_sisalto_id"))
    private List<Lops2019OpintojaksonKeskeinenSisalto> keskeisetSisallot = new ArrayList<>();

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
    }

    @Override
    public ValidationCategory category() {
        return ValidationCategory.OPINTOJAKSO;
    }


    static public Lops2019Opintojakso copy(Lops2019Opintojakso original) {
        if (original != null) {
            Lops2019Opintojakso result = new Lops2019Opintojakso();
            result.setKeskeisetSisallot(original.getKeskeisetSisallot());
            result.setKoodi(original.getKoodi());
            result.setKuvaus(original.getKuvaus());
            result.setLaajaAlainenOsaaminen(original.getLaajaAlainenOsaaminen());
            result.setTavoitteet(original.getTavoitteet());
            result.setNimi(original.getNimi());
            result.setModuulit(original.getModuulit().stream()
                    .map(Lops2019OpintojaksonModuuli::copy)
                    .collect(Collectors.toSet()));
            result.setOppiaineet(original.getOppiaineet().stream()
                    .map(Lops2019OpintojaksonOppiaine::copy)
                    .collect(Collectors.toSet()));
            return result;
        }
        return null;
    }
}
