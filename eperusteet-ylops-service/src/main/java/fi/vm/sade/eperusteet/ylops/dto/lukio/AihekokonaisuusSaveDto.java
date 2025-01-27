package fi.vm.sade.eperusteet.ylops.dto.lukio;

import fi.vm.sade.eperusteet.ylops.dto.teksti.LokalisoituTekstiDto;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AihekokonaisuusSaveDto implements Serializable {
    @NotNull
    private LokalisoituTekstiDto otsikko;
    private LokalisoituTekstiDto yleiskuvaus;
}
