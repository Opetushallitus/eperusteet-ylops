package fi.vm.sade.eperusteet.ylops.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * Created by autio on 2.2.2016.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RevisionDto {
    private Integer numero;
    private Date pvm;
    private String muokkaajaOid;
    private String kommentti = "";
    private String nimi = "";
}
