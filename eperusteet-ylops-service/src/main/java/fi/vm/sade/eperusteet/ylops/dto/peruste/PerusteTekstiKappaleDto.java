package fi.vm.sade.eperusteet.ylops.dto.peruste;

import fi.vm.sade.eperusteet.ylops.domain.Tila;
import fi.vm.sade.eperusteet.ylops.service.external.impl.perustedto.PerusteenLokalisoituTekstiDto;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PerusteTekstiKappaleDto {
    private Long id;
    private Date luotu;
    private Date muokattu;
    private PerusteenLokalisoituTekstiDto nimi;
    private PerusteenLokalisoituTekstiDto teksti;
    private Tila tila;
    private String tunniste;
    private String osanTyyppi;
    private Boolean liite;
}
