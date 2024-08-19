package fi.vm.sade.eperusteet.ylops.dto.ops;

import fi.vm.sade.eperusteet.ylops.dto.Reference;
import fi.vm.sade.eperusteet.ylops.dto.ReferenceableDto;
import fi.vm.sade.eperusteet.ylops.dto.teksti.LokalisoituTekstiDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LaajaalainenosaaminenDto implements ReferenceableDto {

    private Long id;
    private Reference laajaalainenosaaminen;
    private LokalisoituTekstiDto kuvaus;
    private boolean naytaPerusteenPaatasonLao = true;
    private boolean naytaPerusteenVlkTarkennettuLao;

}
