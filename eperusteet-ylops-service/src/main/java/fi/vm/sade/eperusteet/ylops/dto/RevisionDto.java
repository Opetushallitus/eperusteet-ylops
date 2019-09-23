package fi.vm.sade.eperusteet.ylops.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * Created by autio on 2.2.2016.
 */
@Getter
@Setter
@EqualsAndHashCode
public class RevisionDto {
    private Integer numero;
    private Date pvm;
    private String muokkaajaOid;
    private String kommentti = "";
    private String nimi = "";
}
