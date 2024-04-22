package fi.vm.sade.eperusteet.ylops.dto.lukio;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fi.vm.sade.eperusteet.ylops.dto.peruste.lukio.AihekokonaisuudetBaseDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.lukio.AihekokonaisuudetDto;
import fi.vm.sade.eperusteet.ylops.dto.peruste.lukio.AihekokonaisuusOpsDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class AihekokonaisuudetOpsDto extends AihekokonaisuudetBaseDto
        implements PerusteeseenViittaava<AihekokonaisuudetDto> {
    @JsonIgnore
    private AihekokonaisuudetDto perusteen;
    private AihekokonaisuudetBaseDto parent;
    private List<AihekokonaisuusOpsDto> aihekokonaisuudet = new ArrayList<>();

    @Override
    @JsonIgnore // already uuidTunniste
    public UUID getTunniste() {
        return getUuidTunniste();
    }

    @Override
    public Stream<? extends PerusteeseenViittaava<?>> viittaukset() {
        return aihekokonaisuudet.stream();
    }
}
