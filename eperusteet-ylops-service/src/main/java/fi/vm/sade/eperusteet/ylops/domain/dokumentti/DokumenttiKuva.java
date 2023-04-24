package fi.vm.sade.eperusteet.ylops.domain.dokumentti;

import fi.vm.sade.eperusteet.ylops.domain.teksti.Kieli;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

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
