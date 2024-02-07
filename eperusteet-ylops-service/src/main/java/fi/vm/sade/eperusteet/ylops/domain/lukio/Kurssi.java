package fi.vm.sade.eperusteet.ylops.domain.lukio;

import fi.vm.sade.eperusteet.ylops.domain.AbstractAuditedReferenceableEntity;
import fi.vm.sade.eperusteet.ylops.domain.teksti.LokalisoituTeksti;
import fi.vm.sade.eperusteet.ylops.domain.validation.ValidHtml;
import fi.vm.sade.eperusteet.ylops.domain.validation.ValidHtml.WhitelistType;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Entity
@Table(name = "kurssi", schema = "public")
@Audited
@Inheritance(strategy = InheritanceType.JOINED)
public class Kurssi extends AbstractAuditedReferenceableEntity {

    @Getter
    @Setter
    @Column(nullable = false, unique = true, updatable = false)
    protected UUID tunniste;

    @Getter
    @Setter
    @NotNull
    @ValidHtml(whitelist = WhitelistType.MINIMAL)
    @OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "nimi_id", nullable = false)
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    protected LokalisoituTeksti nimi;

    @Getter
    @Setter
    @ValidHtml
    @OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "kuvaus_id")
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    protected LokalisoituTeksti kuvaus;

    @Getter
    @Setter
    @Column(name = "koodi_uri")
    protected String koodiUri;

    @Getter
    @Setter
    @Column(name = "koodi_arvo")
    protected String koodiArvo;

    protected Kurssi() {
    }

    public Kurssi(UUID tunniste) {
        this.tunniste = tunniste;
    }

    public Kurssi copyInto(Kurssi kurssi) {
        kurssi.setTunniste(this.tunniste);
        kurssi.setNimi(this.nimi);
        kurssi.setKuvaus(this.kuvaus);
        kurssi.setKoodiUri(this.koodiUri);
        kurssi.setKoodiArvo(this.koodiArvo);
        return kurssi;
    }
}
