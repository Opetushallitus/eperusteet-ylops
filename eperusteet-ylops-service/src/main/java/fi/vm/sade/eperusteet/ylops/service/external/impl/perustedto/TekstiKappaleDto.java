package fi.vm.sade.eperusteet.ylops.service.external.impl.perustedto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TekstiKappaleDto {
    private Long id;
    private Date luotu;
    private Date muokattu;
    private String muokkaaja;
    private String muokkaajanNimi;
    private PerusteenLokalisoituTekstiDto nimi;
    private PerusteenLokalisoituTekstiDto teksti;
    private String tila;
    private String tunniste;
    private String osanTyyppi;
}
