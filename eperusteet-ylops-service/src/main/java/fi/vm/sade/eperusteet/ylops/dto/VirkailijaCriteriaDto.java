package fi.vm.sade.eperusteet.ylops.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VirkailijaCriteriaDto {

    private Set<String> organisaatioOids;
    private Map<String, Collection<String>> kayttooikeudet;

    // oppijanumerorekisterin hakuehdot
    private Boolean passivoitu;
    private Boolean duplikaatti;

}