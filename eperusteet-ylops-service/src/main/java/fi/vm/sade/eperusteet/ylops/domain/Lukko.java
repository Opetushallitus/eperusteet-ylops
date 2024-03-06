package fi.vm.sade.eperusteet.ylops.domain;

import fi.vm.sade.eperusteet.ylops.service.util.SecurityUtil;

import java.time.Instant;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Getter;

@Entity
@Table(name = "lukko")
public class Lukko {

    @Id
    @Getter
    private Long id;

    @Column(name = "haltija_oid")
    @Getter
    private String haltijaOid;

    @Temporal(TemporalType.TIMESTAMP)
    private Date luotu;

    /**
     * lukon vanhenemisaika sekunteina
     */
    private int vanhenemisAika;

    protected Lukko() {
        //JPA
    }

    public Lukko(Long id, String haltijaOid, int vanhenemisAika) {
        this.id = id;
        this.haltijaOid = haltijaOid;
        this.luotu = new Date();
        this.vanhenemisAika = vanhenemisAika;
    }

    public Instant getLuotu() {
        return luotu.toInstant();
    }

    public void refresh() {
        this.luotu = new Date();
    }

    public Instant getVanhentuu() {
        return getLuotu().plusSeconds(vanhenemisAika);
    }

    public boolean isOma() {
        return haltijaOid.equals(SecurityUtil.getAuthenticatedPrincipal().getName());
    }

}
