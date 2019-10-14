package fi.vm.sade.eperusteet.ylops.dto.lops2019;

import fi.vm.sade.eperusteet.ylops.dto.KoodiDto;
import fi.vm.sade.eperusteet.ylops.dto.teksti.LokalisoituTekstiDto;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Lops2019LaajaAlainenDto {
    private Long id;
    private LokalisoituTekstiDto nimi;
    private LokalisoituTekstiDto kuvaus;
    private KoodiDto koodi;

    static public Lops2019LaajaAlainenDto of(String koodisto, String koodiArvo, String nimi) {
        Lops2019LaajaAlainenDto lao = new Lops2019LaajaAlainenDto();
        lao.setKoodi(KoodiDto.of(koodisto, koodiArvo));
        lao.setNimi(LokalisoituTekstiDto.of(nimi));
        return lao;
    }
}
