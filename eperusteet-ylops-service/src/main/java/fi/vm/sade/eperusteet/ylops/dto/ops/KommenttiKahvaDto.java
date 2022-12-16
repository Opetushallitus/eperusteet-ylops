package fi.vm.sade.eperusteet.ylops.dto.ops;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import fi.vm.sade.eperusteet.ylops.domain.teksti.Kieli;
import lombok.*;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KommenttiKahvaDto {
    private UUID thread;
    private Long opsId;
    private int start;
    private int stop;
    private Kieli kieli;
    private Long tekstiId;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Kommentti2019Dto aloituskommentti;

    static public KommenttiKahvaDto of(Long opsId, Long tekstiId, Kieli kieli, int start, int stop, String kommentti) {
        KommenttiKahvaDto result = new KommenttiKahvaDto();
        result.setThread(UUID.randomUUID());
        result.setOpsId(opsId);
        result.setAloituskommentti(Kommentti2019Dto.of(kommentti));
        result.setKieli(kieli);
        result.setTekstiId(tekstiId);
        result.setStart(start);
        result.setStop(stop);
        return result;
    }
}
