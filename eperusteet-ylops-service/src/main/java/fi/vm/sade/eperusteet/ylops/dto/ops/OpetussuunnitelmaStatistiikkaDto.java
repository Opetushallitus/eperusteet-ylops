package fi.vm.sade.eperusteet.ylops.dto.ops;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OpetussuunnitelmaStatistiikkaDto implements Serializable {
    private Map<String, Long> kielittain = new HashMap<>();
    private Map<String, Long> koulutustyypeittain = new HashMap<>();
    private Map<String, Long> tasoittain = new HashMap<>();
    private Map<String, Long> tiloittain = new HashMap<>();
}
