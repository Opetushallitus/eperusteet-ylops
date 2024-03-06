package fi.vm.sade.eperusteet.ylops.service.external.impl.perustedto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TekstiOsaDto {
    private Long id;
    private PerusteenLokalisoituTekstiDto otsikko;
    private PerusteenLokalisoituTekstiDto teksti;
}
