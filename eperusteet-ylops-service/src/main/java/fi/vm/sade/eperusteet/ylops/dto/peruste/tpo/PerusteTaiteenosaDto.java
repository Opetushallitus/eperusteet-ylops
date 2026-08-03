package fi.vm.sade.eperusteet.ylops.dto.peruste.tpo;

import fi.vm.sade.eperusteet.ylops.service.external.impl.perustedto.PerusteenLokalisoituTekstiDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PerusteTaiteenosaDto {
    private Long id;
    private PerusteenLokalisoituTekstiDto nimi;
    private BigDecimal laajuus;
    private PerusteenLokalisoituTekstiDto kuvaus;
    private List<PerusteenLokalisoituTekstiDto> tavoitteet = new ArrayList<>();
}
