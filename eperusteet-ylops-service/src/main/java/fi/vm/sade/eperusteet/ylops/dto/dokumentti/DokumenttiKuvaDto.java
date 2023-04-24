package fi.vm.sade.eperusteet.ylops.dto.dokumentti;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import fi.vm.sade.eperusteet.ylops.domain.teksti.Kieli;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class DokumenttiKuvaDto {
    private Long id;
    private Long opsId;
    private Kieli kieli;
    private boolean kansikuva = false;
    private boolean ylatunniste = false;
    private boolean alatunniste = false;
}
