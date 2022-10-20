package fi.vm.sade.eperusteet.ylops.dto.peruste.lukio;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Created by jsikio on 14.11.2015.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LukioPerusteTekstikappaleViiteDto {
    private Long id;
    private LukioPerusteenOsaDto perusteenOsa;
    private List<LukioPerusteTekstikappaleViiteDto> lapset;
}
