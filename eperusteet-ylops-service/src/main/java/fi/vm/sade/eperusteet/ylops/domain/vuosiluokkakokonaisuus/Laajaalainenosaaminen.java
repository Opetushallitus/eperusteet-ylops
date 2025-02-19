package fi.vm.sade.eperusteet.ylops.domain.vuosiluokkakokonaisuus;

import fi.vm.sade.eperusteet.ylops.domain.AbstractReferenceableEntity;
import fi.vm.sade.eperusteet.ylops.domain.LaajaalainenosaaminenViite;
import fi.vm.sade.eperusteet.ylops.domain.teksti.LokalisoituTeksti;
import fi.vm.sade.eperusteet.ylops.domain.validation.ValidHtml;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

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

    public static Laajaalainenosaaminen copyOf(Laajaalainenosaaminen other) {
        Laajaalainenosaaminen lo = new Laajaalainenosaaminen(other);
        lo.setKuvaus(other.getKuvaus());
        lo.setNaytaPerusteenPaatasonLao(other.isNaytaPerusteenPaatasonLao());
        lo.setNaytaPerusteenVlkTarkennettuLao(other.isNaytaPerusteenVlkTarkennettuLao());

        return lo;
    }

    public Laajaalainenosaaminen(Laajaalainenosaaminen other) {
        this.laajaalainenosaaminen = new LaajaalainenosaaminenViite(other.getLaajaalainenosaaminen());
    }

    public void setVuosiluokkaKokonaisuus(Vuosiluokkakokonaisuus vuosiluokkakokonaisuus) {
        if (this.vuosiluokkakokonaisuus == null || this.vuosiluokkakokonaisuus.equals(vuosiluokkakokonaisuus)) {
            this.vuosiluokkakokonaisuus = vuosiluokkakokonaisuus;
        } else {
            throw new IllegalStateException("Vuosiluokkakokonaisuuteen kuulumista ei voi muuttaa");
        }
    }

}
