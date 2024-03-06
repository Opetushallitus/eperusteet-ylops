package fi.vm.sade.eperusteet.ylops.dto.kayttaja;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KayttajanProjektitiedotDto {
    Long opetussuunnitelmaId;
    String organisaatioOid;
    String tehtavanimike;
    Boolean passivoitu;
}
