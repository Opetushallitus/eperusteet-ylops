package fi.vm.sade.eperusteet.ylops.dto.teksti;

import com.fasterxml.jackson.annotation.JsonInclude;
import fi.vm.sade.eperusteet.ylops.domain.Tila;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TekstiKappaleDto {
    private Long id;
    private Date luotu;
    private Date muokattu;
    private String muokkaaja;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String muokkaajanNimi;

    private LokalisoituTekstiDto nimi;
    private LokalisoituTekstiDto teksti;
    private Tila tila;
    private UUID tunniste;
    private Boolean pakollinen;
    private Boolean valmis;

    public TekstiKappaleDto(LokalisoituTekstiDto nimi, LokalisoituTekstiDto teksti, Tila tila) {
        this.nimi = nimi;
        this.teksti = teksti;
        this.tila = tila;
    }
}
