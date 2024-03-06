package fi.vm.sade.eperusteet.ylops.dto.lukio;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AihekokonaisuudetJarjestaDto implements Serializable {
    @NotNull
    @Valid
    private List<AihekokonaisuusJarjestysDto> aihekokonaisuudet = new ArrayList<>();
}
