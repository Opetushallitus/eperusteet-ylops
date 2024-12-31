package fi.vm.sade.eperusteet.ylops.dto.lukio;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AihekokonaisuusJarjestysDto {
    @NotNull
    private Long id;
}
