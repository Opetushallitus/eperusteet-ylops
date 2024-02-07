package fi.vm.sade.eperusteet.ylops.dto.peruste.lukio;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class AihekokonaisuudetDto extends AihekokonaisuudetBaseDto
        implements Serializable, PerusteenOsa {
    private AihekokonaisuudetDto parent;
    private List<AihekokonaisuusDto> aihekokonaisuudet;

    @Override
    @JsonIgnore // uuidTunniste
    public UUID getTunniste() {
        return getUuidTunniste();
    }

    @Override
    public Stream<? extends PerusteenOsa> osat() {
        return aihekokonaisuudet.stream();
    }
}
