package fi.vm.sade.eperusteet.ylops.dto.peruste.tpo;

import fi.vm.sade.eperusteet.ylops.service.external.impl.perustedto.PerusteenLokalisoituTekstiDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PerusteKevytTekstiKappaleDto {
    private Long id;
    private PerusteenLokalisoituTekstiDto nimi;
    private PerusteenLokalisoituTekstiDto teksti;
}
