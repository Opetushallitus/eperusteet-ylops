package fi.vm.sade.eperusteet.ylops.domain;

import fi.vm.sade.eperusteet.ylops.service.util.SecurityUtil;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Getter;
import org.hibernate.envers.Audited;

/**
 * Kantaluokka entiteeteille joista ylläpidetään luotu/muokattu -tietoja.
 */
@MappedSuperclass
public abstract class AbstractAuditedEntity implements Serializable {

    @Audited
    @Column(updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date luotu;

    @Audited
    @Getter
    @Column(updatable = false)
    private String luoja;

    @Audited
    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Date muokattu;

    @Audited
    @Getter
    @Column
    private String muokkaaja;

    public Date getLuotu() {
        return luotu == null ? null : new Date(luotu.getTime());
    }

    public Date getMuokattu() {
        return muokattu == null ? null : new Date(muokattu.getTime());
    }

    public void muokattu() {
        this.muokattu = new Date();
    }

    @PrePersist
    private void prepersist() {
        luotu = new Date();
        updateMuokkaustiedot();
    }

    @PreUpdate
    private void preupdate() {
        this.updateMuokkaustiedot();
    }

    // Fixme: ilman tämän kutsumista, palauttaminen ei päivitä muokkaustietoja
    public void updateMuokkaustiedot() {
        muokattu = new Date();
        muokkaaja = SecurityUtil.getAuthenticatedPrincipal().getName();
    }
}
