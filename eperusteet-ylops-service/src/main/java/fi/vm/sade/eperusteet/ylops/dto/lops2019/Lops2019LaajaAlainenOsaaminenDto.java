package fi.vm.sade.eperusteet.ylops.dto.lops2019;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import fi.vm.sade.eperusteet.ylops.dto.teksti.LokalisoituTekstiDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Lops2019LaajaAlainenOsaaminenDto {
    private LokalisoituTekstiDto kuvaus;
    private List<Lops2019LaajaAlainenDto> laajaAlaisetOsaamiset = new ArrayList<>();
}
