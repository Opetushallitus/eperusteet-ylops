package fi.vm.sade.eperusteet.ylops.domain.ops;

import fi.vm.sade.eperusteet.ylops.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.ylops.domain.teksti.LokalisoituTeksti;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Immutable;

import java.util.UUID;

/**
 * Luokan avulla voidaan liittää tekstikappaleisiin tageja mitkä eivät tule osaksi sisältöä.
 * Mahdollistaa esimerkiksi kommenttitagin upottamiseen tekstikappaleeseen. Tagit lisätään lokalisoituun
 * tekstiin sitä haettaessa ja tallennettaessa päivitetään ja siistitään.
 */
@Entity
@Table(name = "kommentti_kahva_2019")
@Immutable
public class KommenttiKahva {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Getter
    private Long id;

    @Getter
    @Setter
    @Column(updatable = false)
    @NotNull
    private UUID thread;

    @Getter
    @Setter
    @NotNull
    @Column(updatable = false)
    private Long opsId;

    @Getter
    @Setter
    @NotNull
    @Enumerated(value = EnumType.STRING)
    @Column
    private Kieli kieli;

    @ManyToOne
    @Getter
    @Setter
    @NotNull
    private LokalisoituTeksti teksti;

    /**
     * Kommentin alkukohta ilman muita kommentti-tageja
     */
    @Getter
    @Setter
    @Column
    @NotNull
    private int start;

    @Getter
    @Setter
    @Column
    @NotNull
    private int stop;

    static public KommenttiKahva copy(KommenttiKahva other, int start, int stop) {
        KommenttiKahva result = new KommenttiKahva();
        result.setThread(other.getThread());
        result.setOpsId(other.getOpsId());
        result.setKieli(other.getKieli());
        result.setStart(start);
        result.setStop(stop);
        return result;
    }

    static public KommenttiKahva of(Long opsId, LokalisoituTeksti teksti, int start, int stop) {
        KommenttiKahva result = new KommenttiKahva();
        result.setThread(UUID.randomUUID());
        result.setOpsId(opsId);
        result.setTeksti(teksti);
        result.setStart(start);
        result.setStop(stop);
        return result;
    }

}
