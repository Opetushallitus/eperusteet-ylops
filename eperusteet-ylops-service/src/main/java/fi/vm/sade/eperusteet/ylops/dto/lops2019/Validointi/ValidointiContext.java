package fi.vm.sade.eperusteet.ylops.dto.lops2019.Validointi;

import fi.vm.sade.eperusteet.ylops.domain.teksti.Kieli;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ValidointiContext {
    private Set<Kieli> kielet = new HashSet<>();
}
