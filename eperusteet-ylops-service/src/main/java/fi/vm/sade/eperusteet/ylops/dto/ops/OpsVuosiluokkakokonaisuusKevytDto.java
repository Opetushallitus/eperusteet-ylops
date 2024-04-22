package fi.vm.sade.eperusteet.ylops.dto.ops;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OpsVuosiluokkakokonaisuusKevytDto {
    private boolean oma;
    private VuosiluokkakokonaisuusSuppeaDto vuosiluokkakokonaisuus;
    private OpsVuosiluokkakokonaisuusLisatietoDto lisatieto;
}
