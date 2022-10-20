package fi.vm.sade.eperusteet.ylops.dto.lops2019;

import fi.vm.sade.eperusteet.ylops.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.ylops.dto.KoodiDto;
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
public class Lops2019LaajaAlainenDto {
    private Long id;
    private LokalisoituTekstiDto nimi;
    private LokalisoituTekstiDto kuvaus;
    private KoodiDto koodi;

    static public Lops2019LaajaAlainenDto of(String koodisto, String koodiArvo, String nimi, Kieli kieli) {
        Lops2019LaajaAlainenDto lao = new Lops2019LaajaAlainenDto();
        lao.setKoodi(KoodiDto.of(koodisto, koodiArvo));
        lao.setNimi(LokalisoituTekstiDto.of(nimi, kieli));
        return lao;
    }
}
