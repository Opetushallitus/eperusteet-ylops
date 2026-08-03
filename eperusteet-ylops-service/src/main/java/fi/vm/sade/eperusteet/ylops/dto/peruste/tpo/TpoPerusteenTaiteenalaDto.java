package fi.vm.sade.eperusteet.ylops.dto.peruste.tpo;

import com.fasterxml.jackson.annotation.JsonTypeName;
import fi.vm.sade.eperusteet.ylops.service.external.impl.perustedto.PerusteenLokalisoituTekstiDto;
import fi.vm.sade.eperusteet.ylops.service.external.impl.perustedto.PerusteenOsaDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Taiteen perusopetuksen perusteen taiteenala.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@JsonTypeName("taiteenala")
public class TpoPerusteenTaiteenalaDto extends PerusteenOsaDto.Laaja {
    private BigDecimal laajuus;
    private PerusteenLokalisoituTekstiDto teksti;
    private List<PerusteTaiteenosaDto> taiteenOsat = new ArrayList<>();
}
