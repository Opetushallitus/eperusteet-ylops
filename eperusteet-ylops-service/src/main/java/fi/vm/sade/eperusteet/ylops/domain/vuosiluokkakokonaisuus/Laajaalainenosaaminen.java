package fi.vm.sade.eperusteet.ylops.domain.vuosiluokkakokonaisuus;

import fi.vm.sade.eperusteet.ylops.domain.AbstractReferenceableEntity;
import fi.vm.sade.eperusteet.ylops.domain.LaajaalainenosaaminenViite;
import fi.vm.sade.eperusteet.ylops.domain.teksti.LokalisoituTeksti;
import fi.vm.sade.eperusteet.ylops.domain.validation.ValidHtml;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Audited
@Table(name = "vlkok_laaja_osaaminen")
public class Laajaalainenosaaminen extends AbstractReferenceableEntity {

    @Getter
    @Setter
    @Embedded
    private LaajaalainenosaaminenViite laajaalainenosaaminen;

    @ManyToOne
    @Getter
    @NotNull
    @JoinColumn(updatable = false, nullable = false)
    private Vuosiluokkakokonaisuus vuosiluokkakokonaisuus;

    @Getter
    @Setter
    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @ValidHtml
    private LokalisoituTeksti kuvaus;

    @Getter
    @Setter
    @Column(name = "nayta_perusteen_paatason_lao")
    private boolean naytaPerusteenPaatasonLao = true;

    @Getter
    @Setter
    @Column(name = "nayta_perusteen_vlk_tarkennettu_lao")
    private boolean naytaPerusteenVlkTarkennettuLao = false;

    public Laajaalainenosaaminen() {
    }

    public Laajaalainenosaaminen(Laajaalainenosaaminen other) {
        this.laajaalainenosaaminen = new LaajaalainenosaaminenViite(other.getLaajaalainenosaaminen());
        this.kuvaus = other.getKuvaus();
    }

    public void setVuosiluokkaKokonaisuus(Vuosiluokkakokonaisuus vuosiluokkakokonaisuus) {
        if (this.vuosiluokkakokonaisuus == null || this.vuosiluokkakokonaisuus.equals(vuosiluokkakokonaisuus)) {
            this.vuosiluokkakokonaisuus = vuosiluokkakokonaisuus;
        } else {
            throw new IllegalStateException("Vuosiluokkakokonaisuuteen kuulumista ei voi muuttaa");
        }
    }

}
