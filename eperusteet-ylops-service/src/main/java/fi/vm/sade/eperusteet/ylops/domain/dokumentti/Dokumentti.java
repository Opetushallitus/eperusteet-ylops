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
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

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

    private Date aloitusaika;

    private Date valmistumisaika;

    @Enumerated(EnumType.STRING)
    @NotNull
    private DokumenttiTila tila = DokumenttiTila.EI_OLE;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "dokumenttidata")
    private byte[] data;

    @Column(name = "virhekoodi")
    private String virhekoodi;
}
