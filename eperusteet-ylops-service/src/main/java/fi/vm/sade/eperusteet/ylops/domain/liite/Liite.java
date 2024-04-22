package fi.vm.sade.eperusteet.ylops.domain.liite;

import java.io.Serializable;
import java.sql.Blob;
import java.util.Date;
import java.util.UUID;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Getter;

@Entity
@Table(name = "liite")
public class Liite implements Serializable {

    @Id
    @Getter
    @Column(updatable = false)
    private UUID id;
    @Getter
    @NotNull
    @Basic(optional = false)
    private String tyyppi;
    @Getter
    @Size(max = 1024)
    private String nimi;
    @Temporal(TemporalType.TIMESTAMP)
    private Date luotu;
    @Getter
    @Basic(fetch = FetchType.LAZY, optional = false)
    @Lob
    @NotNull
    private Blob data;

    protected Liite() {
        //JPA
    }

    public Liite(String tyyppi, String nimi, Blob data) {
        this.id = UUID.randomUUID();
        this.luotu = new Date();
        this.nimi = nimi;
        this.tyyppi = tyyppi;
        this.data = data;
    }

    public Date getLuotu() {
        return new Date(luotu.getTime());
    }

}
