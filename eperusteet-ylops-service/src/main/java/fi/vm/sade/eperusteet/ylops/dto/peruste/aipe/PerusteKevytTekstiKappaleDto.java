package fi.vm.sade.eperusteet.ylops.dto.peruste.aipe;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import fi.vm.sade.eperusteet.ylops.dto.teksti.LokalisoituTekstiDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PerusteKevytTekstiKappaleDto {
    private Long id;
    private LokalisoituTekstiDto nimi;
    private LokalisoituTekstiDto teksti;
    private Integer jnro;
}
