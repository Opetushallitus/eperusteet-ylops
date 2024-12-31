package fi.vm.sade.eperusteet.ylops.domain.teksti;

import fi.vm.sade.eperusteet.ylops.domain.AbstractAuditedEntity;
import fi.vm.sade.eperusteet.ylops.domain.HistoriaTapahtuma;
import fi.vm.sade.eperusteet.ylops.domain.ReferenceableEntity;
import fi.vm.sade.eperusteet.ylops.domain.Tila;
import fi.vm.sade.eperusteet.ylops.domain.validation.ValidHtml;
import fi.vm.sade.eperusteet.ylops.dto.navigation.NavigationType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "tekstikappale")
@Audited
public class TekstiKappale extends AbstractAuditedEntity
        implements Serializable, ReferenceableEntity, HistoriaTapahtuma {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Getter
    @Setter
    private Long id;

    @Getter
    @Column(updatable = false)
    private UUID tunniste;

    @ValidHtml(whitelist = ValidHtml.WhitelistType.MINIMAL)
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @Getter
    @Setter
    private LokalisoituTeksti nimi;

    @Getter
    @Setter
    private Boolean valmis;

    /**
     * Kertoo että onko viitattava tekstikappale merkitty pakolliseksi
     * ts. sitä ei voi poistaa eikä sen otsikkoa muokata.
     */
    @Getter
    @Setter
    private Boolean pakollinen;

    @ValidHtml
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @Getter
    @Setter
    private LokalisoituTeksti teksti;

    @Enumerated(value = EnumType.STRING)
    @NotNull
    @Getter
    private Tila tila = Tila.LUONNOS;

    public TekstiKappale() {
        tunniste = UUID.randomUUID();
    }

    public TekstiKappale(TekstiKappale other) {
        this.tunniste = other.tunniste;
        copyState(other);
    }

    public void asetaTunniste(UUID tunniste) {
        if (tunniste != null) {
            this.tunniste = tunniste;
        }
    }

    public void setTila(Tila tila) {
        if (this.tila == null || this.tila == Tila.LUONNOS) {
            this.tila = tila;
        }
    }

    public TekstiKappale copy() {
        return new TekstiKappale(this);
    }

    private void copyState(TekstiKappale other) {
        this.setNimi(other.getNimi());
        this.setTeksti(other.getTeksti());
    }


    @Override
    public NavigationType getNavigationType() {
        return NavigationType.viite;
    }

}
