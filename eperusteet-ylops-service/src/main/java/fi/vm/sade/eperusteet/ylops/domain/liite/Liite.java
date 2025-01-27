package fi.vm.sade.eperusteet.ylops.domain.liite;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

import java.io.Serializable;
import java.sql.Blob;
import java.util.Date;
import java.util.UUID;

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
