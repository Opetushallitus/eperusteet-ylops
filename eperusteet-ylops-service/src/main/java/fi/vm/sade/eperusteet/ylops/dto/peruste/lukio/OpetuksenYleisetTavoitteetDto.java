package fi.vm.sade.eperusteet.ylops.dto.peruste.lukio;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fi.vm.sade.eperusteet.ylops.dto.teksti.LokalisoituTekstiDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OpetuksenYleisetTavoitteetDto implements Serializable, PerusteenOsa {
    private OpetuksenYleisetTavoitteetDto parent;
    private UUID uuidTunniste;
    private Long id;
    private LokalisoituTekstiDto otsikko;
    private LokalisoituTekstiDto kuvaus;
    private Date muokattu;
    private String muokkaaja;


    @Override
    @JsonIgnore // uuidTunniste
    public UUID getTunniste() {
        return uuidTunniste;
    }
}
