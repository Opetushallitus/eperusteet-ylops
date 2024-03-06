package fi.vm.sade.eperusteet.ylops.domain.revision;

import java.io.Serializable;
import java.util.Date;

import lombok.EqualsAndHashCode;

import lombok.Getter;

@Getter
@EqualsAndHashCode
public class Revision implements Serializable {
    private final Long id;
    private final Integer numero;
    private final Date pvm;
    private final String muokkaajaOid;
    private final String kommentti;

    public Revision(Long id, Integer number, Long timestamp, String muokkaajaOid, String kommentti) {
        this.id = id;
        this.numero = number;
        this.pvm = new Date(timestamp);
        this.muokkaajaOid = muokkaajaOid;
        this.kommentti = kommentti;
    }
}
