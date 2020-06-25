package fi.vm.sade.eperusteet.ylops.dto.ops;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
public class Kommentti2019Dto {
    private UUID thread;
    private UUID tunniste;
    private UUID reply;
    private String sisalto;
    private Long opsId;
    private Date luotu;
    private Date muokattu;
    private String muokkaaja;
    private String nimi;

    public static Kommentti2019Dto of(String kommentti) {
        Kommentti2019Dto result = new Kommentti2019Dto();
        result.sisalto = kommentti;
        return result;
    }

    public String getLuoja() {
        return muokkaaja;
    }

}
