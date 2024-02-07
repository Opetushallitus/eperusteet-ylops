package fi.vm.sade.eperusteet.ylops.dto.peruste.lukio;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LukioPerusteTekstikappaleViiteDto {
    private Long id;
    private LukioPerusteenOsaDto perusteenOsa;
    private List<LukioPerusteTekstikappaleViiteDto> lapset;
}
