package fi.vm.sade.eperusteet.ylops.domain.dokumentti;

import fi.vm.sade.eperusteet.ylops.domain.teksti.Kieli;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "dokumentti")
@Getter
@Setter
public class Dokumentti {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;

    @NotNull
    @Column(name = "ops_id")
    private Long opsId;

    private String luoja;

    @Column(insertable = true, updatable = false)
    @Enumerated(EnumType.STRING)
    @NotNull
    private Kieli kieli;

    @Temporal(TemporalType.TIMESTAMP)
    private Date aloitusaika;

    @Temporal(TemporalType.TIMESTAMP)
    private Date valmistumisaika;

    @Enumerated(EnumType.STRING)
    @NotNull
    private DokumenttiTila tila = DokumenttiTila.EI_OLE;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "dokumenttidata")
    private byte[] data;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "dokumenttihtml")
    private byte[] html;

    @Column(name = "virhekoodi")
    private String virhekoodi;

    public List<String> getDataTyypit() {
        List<String> tyypit = new ArrayList<>();
        if (data != null) {
            tyypit.add("PDF");
        }
        if (html != null) {
            tyypit.add("HTML");
        }
        return tyypit;
    }
}
