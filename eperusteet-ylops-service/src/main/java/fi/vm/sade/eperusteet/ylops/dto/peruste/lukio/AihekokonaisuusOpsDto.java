package fi.vm.sade.eperusteet.ylops.dto.peruste.lukio;

import fi.vm.sade.eperusteet.ylops.dto.lukio.PerusteeseenViittaava;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class AihekokonaisuusOpsDto extends AihekokonaisuusDto
        implements PerusteeseenViittaava<AihekokonaisuusDto> {
    private AihekokonaisuusDto perusteen;
}
