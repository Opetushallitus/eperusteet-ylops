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

@Entity
@Table(name = "dokumentti_kuva")
@Getter
@Setter
public class DokumenttiKuva {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;

    @NotNull
    @Column(name = "ops_id")
    private Long opsId;

    @Column(updatable = false)
    @Enumerated(EnumType.STRING)
    @NotNull
    private Kieli kieli;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    private byte[] kansikuva;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    private byte[] ylatunniste;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    private byte[] alatunniste;
}
