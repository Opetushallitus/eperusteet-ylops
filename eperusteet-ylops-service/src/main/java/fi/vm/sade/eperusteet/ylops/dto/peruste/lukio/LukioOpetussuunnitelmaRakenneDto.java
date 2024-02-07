package fi.vm.sade.eperusteet.ylops.dto.peruste.lukio;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LukioOpetussuunnitelmaRakenneDto implements Serializable, PerusteenOsa {
    private Long perusteId;
    private Set<LukioPerusteOppiaineDto> oppiaineet = new HashSet<>();

    @Override
    @JsonIgnore
    public UUID getTunniste() {
        return null;
    }

    @Override
    public Stream<? extends PerusteenOsa> osat() {
        return oppiaineet.stream();
    }
}
