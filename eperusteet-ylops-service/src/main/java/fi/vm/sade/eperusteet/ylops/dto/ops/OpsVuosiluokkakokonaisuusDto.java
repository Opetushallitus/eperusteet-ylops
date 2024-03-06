package fi.vm.sade.eperusteet.ylops.dto.ops;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OpsVuosiluokkakokonaisuusDto {
    private boolean oma;
    private VuosiluokkakokonaisuusDto vuosiluokkakokonaisuus;
    private OpsVuosiluokkakokonaisuusLisatietoDto lisatieto;
}
