package fi.vm.sade.eperusteet.ylops.service.external.impl.perustedto;

import fi.vm.sade.eperusteet.ylops.dto.ReferenceableDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TavoitteenArviointiDto implements ReferenceableDto {
    private Long id;
    private PerusteenLokalisoituTekstiDto arvioinninKohde;
    private PerusteenLokalisoituTekstiDto osaamisenKuvaus;
    private Integer arvosana;

    public PerusteenLokalisoituTekstiDto getHyvanOsaamisenKuvaus() {
        if(arvosana == null || arvosana == 8) {
            return osaamisenKuvaus;
        }

        return null;
    }
}
