package fi.vm.sade.eperusteet.ylops.dto.lukio;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AihekokonaisuusJarjestysDto {
    @NotNull
    private Long id;
}
