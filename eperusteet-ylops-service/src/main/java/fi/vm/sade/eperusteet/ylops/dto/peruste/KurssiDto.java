package fi.vm.sade.eperusteet.ylops.dto.peruste;

import fi.vm.sade.eperusteet.ylops.dto.teksti.LokalisoituTekstiDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KurssiDto implements Serializable {
    private long id;
    private UUID tunniste;
    protected LokalisoituTekstiDto nimi;
    protected LokalisoituTekstiDto kuvaus;
    protected String koodiUri;
    protected String koodiArvo;
    protected LokalisoituTekstiDto lokalisoituKoodi;
}
