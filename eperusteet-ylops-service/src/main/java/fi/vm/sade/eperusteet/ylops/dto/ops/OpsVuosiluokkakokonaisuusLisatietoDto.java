package fi.vm.sade.eperusteet.ylops.dto.ops;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OpsVuosiluokkakokonaisuusLisatietoDto {
    private Set<Long> piilotetutOppiaineet = new HashSet<>();
}
