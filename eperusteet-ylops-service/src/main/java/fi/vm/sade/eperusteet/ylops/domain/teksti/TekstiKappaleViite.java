package fi.vm.sade.eperusteet.ylops.domain.teksti;

import fi.vm.sade.eperusteet.ylops.domain.HistoriaTapahtuma;
import fi.vm.sade.eperusteet.ylops.domain.ReferenceableEntity;
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
import jakarta.persistence.NamedNativeQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;
import org.hibernate.envers.Audited;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Entity
@Audited
@Table(name = "tekstikappaleviite")
@NamedNativeQuery(
        name = "TekstiKappaleViite.findRootByTekstikappaleId",
        query
                = "with recursive vanhemmat(id,vanhempi_id,tekstikappale_id) as "
                + "(select tv.id, tv.vanhempi_id, tv.tekstikappale_id from tekstikappaleviite tv "
                + "where tv.tekstikappale_id = ?1 and tv.omistussuhde in (?2,?3) "
                + "union all "
                + "select tv.id, tv.vanhempi_id, v.tekstikappale_id "
                + "from tekstikappaleviite tv, vanhemmat v where tv.id = v.vanhempi_id) "
                + "select id from vanhemmat where vanhempi_id is null"
)
public class TekstiKappaleViite implements ReferenceableEntity, Serializable, HistoriaTapahtuma {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Getter
    @Setter
    private Long id;

    @Getter
    @Setter
    private boolean pakollinen;

    @Getter
    @Setter
    private boolean valmis;

    @ManyToOne
    @Getter
    @Setter
    private TekstiKappaleViite vanhempi;

    @ManyToOne
    @Getter
    @Setter
    private TekstiKappale tekstiKappale;

    /**
     * Kertoo viitattavan tekstikappaleen omistussuhteen.
     * Vain omaa tekstikappaletta voidaan muokata, lainatusta tekstikappaleesta
     * täytyy ensin tehdä oma kopio ennen kuin muokkaus on mahdollista.
     */
    @Enumerated(value = EnumType.STRING)
    @NotNull
    @Getter
    @Setter
    private Omistussuhde omistussuhde = Omistussuhde.OMA;

    @OneToMany(mappedBy = "vanhempi", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @OrderColumn
    @Getter
    @Setter
    @BatchSize(size = 100)
    private List<TekstiKappaleViite> lapset = new ArrayList<>();

    @Getter
    @Column(name = "peruste_tekstikappale_id")
    private Long perusteTekstikappaleId;

    @Getter
    @Setter
    @Column(name = "nayta_perusteen_teksti")
    private boolean naytaPerusteenTeksti = true;

    @Getter
    @Setter
    @Column(name = "nayta_pohjan_teksti")
    private boolean naytaPohjanTeksti = true;

    @Getter
    @Setter
    private boolean piilotettu = false;

    @Getter
    @Setter
    @NotNull
    private boolean liite = false;

    public TekstiKappaleViite() {
    }

    public TekstiKappaleViite(Omistussuhde omistussuhde) {
        this.omistussuhde = omistussuhde;
    }

    public static TekstiKappaleViite copy(TekstiKappaleViite original) {
        TekstiKappaleViite tkv = new TekstiKappaleViite();
        tkv.setOmistussuhde(Omistussuhde.OMA);
        tkv.setLapset(new ArrayList<>());
        tkv.setVanhempi(null);
        tkv.setPakollinen(original.isPakollinen());
        tkv.setNaytaPerusteenTeksti(original.isNaytaPerusteenTeksti());
        tkv.setPerusteTekstikappaleId(original.getPerusteTekstikappaleId());
        tkv.setLiite(original.isLiite());
        TekstiKappale copy = original.getTekstiKappale().copy();
        copy.setTeksti(null);
        tkv.setTekstiKappale(copy);
        return tkv;
    }

    public void kiinnitaHierarkia(TekstiKappaleViite parent) {
        this.setVanhempi(parent);
        if (lapset != null) {
            for (TekstiKappaleViite child : lapset) {
                child.kiinnitaHierarkia(this);
            }
        }
    }

    // Kopioi viitehierarkian ja siirtää irroitetut paikoilleen
    // UUID parentin tunniste
    public TekstiKappaleViite kopioiHierarkia(Map<UUID, TekstiKappaleViite> irroitetut) {
        TekstiKappaleViite result = new TekstiKappaleViite();
        result.setTekstiKappale(this.getTekstiKappale());
        result.setOmistussuhde(this.getOmistussuhde());

        if (lapset != null) {
            List<TekstiKappaleViite> ilapset = new ArrayList<>();
            for (TekstiKappaleViite lapsi : lapset) {
                TekstiKappaleViite uusiLapsi = lapsi.kopioiHierarkia(irroitetut);
                uusiLapsi.setVanhempi(result);
                ilapset.add(uusiLapsi);
            }
            for (Map.Entry<UUID, TekstiKappaleViite> lapsi : irroitetut.entrySet()) {
                if (this.getTekstiKappale().getTunniste() == lapsi.getKey()) {
                    ilapset.add(lapsi.getValue());
                    irroitetut.remove(lapsi.getKey());
                }
            }
            result.setLapset(ilapset);
        }
        return result;
    }

    public TekstiKappaleViite getRoot() {
        TekstiKappaleViite root = this;
        while (root.getVanhempi() != null) {
            root = root.getVanhempi();
        }
        return root;
    }

    public void setPerusteTekstikappaleId(Long perusteTekstikappaleId) {
        if (this.perusteTekstikappaleId == null) {
            this.perusteTekstikappaleId = perusteTekstikappaleId;
        }
    }

    @Override
    public Date getLuotu() {
        return tekstiKappale.getLuotu();
    }

    @Override
    public Date getMuokattu() {
        return tekstiKappale.getMuokattu();
    }

    @Override
    public String getLuoja() {
        return tekstiKappale.getLuoja();
    }

    @Override
    public String getMuokkaaja() {
        return tekstiKappale.getMuokkaaja();
    }

    @Override
    public LokalisoituTeksti getNimi() {
        return tekstiKappale.getNimi();
    }

    @Override
    public NavigationType getNavigationType() {
        return NavigationType.viite;
    }
}
