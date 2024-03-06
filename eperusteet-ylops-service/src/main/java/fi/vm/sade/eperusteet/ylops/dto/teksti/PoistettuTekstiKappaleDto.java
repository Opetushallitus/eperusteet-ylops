package fi.vm.sade.eperusteet.ylops.dto.teksti;

import fi.vm.sade.eperusteet.ylops.dto.PoistettuDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class PoistettuTekstiKappaleDto extends PoistettuDto {
    private Long tekstiKappale;
}
